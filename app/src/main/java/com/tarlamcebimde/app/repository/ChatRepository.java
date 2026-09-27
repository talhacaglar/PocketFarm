package com.tarlamcebimde.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.Query;
import com.tarlamcebimde.app.model.Chat;
import com.tarlamcebimde.app.model.Message;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mesajlaşma işlemleri için repository
 */
public class ChatRepository {
    private final FirebaseHelper firebase;

    public ChatRepository() {
        this.firebase = FirebaseHelper.getInstance();
    }

    /**
     * Kullanıcının tüm sohbetlerini getir
     */
    public LiveData<List<Chat>> getUserChats(String userId) {
        MutableLiveData<List<Chat>> chatsData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_CHATS)
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        chatsData.setValue(new ArrayList<>());
                        return;
                    }
                    chatsData.setValue(snapshots.toObjects(Chat.class));
                });
        return chatsData;
    }

    /**
     * İki kullanıcı arasında belirli bir ürün için mevcut chat var mı kontrol et
     */
    public Task<Chat> findExistingChat(String buyerId, String sellerId, String productId) {
        return firebase.getDb()
                .collection(Constants.COLLECTION_CHATS)
                .whereArrayContains("participants", buyerId)
                .whereEqualTo("productId", productId)
                .get()
                .onSuccessTask(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Chat chat = doc.toObject(Chat.class);
                        if (chat != null && chat.getParticipants().contains(sellerId)) {
                            return com.google.android.gms.tasks.Tasks.forResult(chat);
                        }
                    }
                    return com.google.android.gms.tasks.Tasks.forResult(null);
                });
    }

    /**
     * Yeni sohbet oluştur veya mevcut olanı döndür
     */
    public Task<String> getOrCreateChat(Chat chat) {
        return findExistingChat(
                chat.getParticipants().get(0),
                chat.getParticipants().get(1),
                chat.getProductId()
        ).onSuccessTask(existingChat -> {
            if (existingChat != null) {
                return com.google.android.gms.tasks.Tasks.forResult(existingChat.getChatId());
            }

            // Yeni chat oluştur
            String chatId = firebase.getDb()
                    .collection(Constants.COLLECTION_CHATS)
                    .document().getId();

            return firebase.getDb()
                    .collection(Constants.COLLECTION_CHATS)
                    .document(chatId)
                    .set(chat)
                    .onSuccessTask(result -> com.google.android.gms.tasks.Tasks.forResult(chatId));
        });
    }

    /**
     * Mesaj gönder
     */
    public Task<Void> sendMessage(String chatId, Message message) {
        // Commit the message and its chat preview together.
        DocumentReference chatRef = firebase.getDb()
                .collection(Constants.COLLECTION_CHATS)
                .document(chatId);
        WriteBatch batch = firebase.getDb().batch();
        batch.set(chatRef.collection(Constants.COLLECTION_MESSAGES).document(), message);

        // Chat'in son mesajını güncelle
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("lastMessage", message.getText());
        chatUpdate.put("lastSenderId", message.getSenderId());
        chatUpdate.put("lastMessageTime", FieldValue.serverTimestamp());

        batch.update(chatRef, chatUpdate);
        return batch.commit();
    }

    /**
     * Sohbetin mesajlarını dinle (gerçek zamanlı)
     */
    public LiveData<List<Message>> getMessages(String chatId) {
        MutableLiveData<List<Message>> messagesData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .collection(Constants.COLLECTION_MESSAGES)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        messagesData.setValue(new ArrayList<>());
                        return;
                    }
                    messagesData.setValue(snapshots.toObjects(Message.class));
                });
        return messagesData;
    }
}
