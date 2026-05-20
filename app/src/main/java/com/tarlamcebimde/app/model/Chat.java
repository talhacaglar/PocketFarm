package com.tarlamcebimde.app.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sohbet modeli - Firestore 'chats' koleksiyonu
 */
public class Chat {
    @DocumentId
    private String chatId;
    private List<String> participants;
    private Map<String, String> participantNames;
    private Map<String, String> participantImages;
    private String productId;
    private String productTitle;
    private String lastMessage;
    private String lastSenderId;
    @ServerTimestamp
    private Timestamp lastMessageTime;

    public Chat() {
        this.participants = new ArrayList<>();
        this.participantNames = new HashMap<>();
        this.participantImages = new HashMap<>();
    }

    public Chat(String buyerId, String buyerName, String buyerImage,
                String sellerId, String sellerName, String sellerImage,
                String productId, String productTitle) {
        this();
        this.participants.add(buyerId);
        this.participants.add(sellerId);
        this.participantNames.put(buyerId, buyerName);
        this.participantNames.put(sellerId, sellerName);
        this.participantImages.put(buyerId, buyerImage != null ? buyerImage : "");
        this.participantImages.put(sellerId, sellerImage != null ? sellerImage : "");
        this.productId = productId;
        this.productTitle = productTitle;
        this.lastMessage = "";
    }

    // Getters
    public String getChatId() { return chatId; }
    public List<String> getParticipants() { return participants; }
    public Map<String, String> getParticipantNames() { return participantNames; }
    public Map<String, String> getParticipantImages() { return participantImages; }
    public String getProductId() { return productId; }
    public String getProductTitle() { return productTitle; }
    public String getLastMessage() { return lastMessage; }
    public String getLastSenderId() { return lastSenderId; }
    public Timestamp getLastMessageTime() { return lastMessageTime; }

    // Setters
    public void setChatId(String chatId) { this.chatId = chatId; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
    public void setParticipantNames(Map<String, String> participantNames) { this.participantNames = participantNames; }
    public void setParticipantImages(Map<String, String> participantImages) { this.participantImages = participantImages; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastSenderId(String lastSenderId) { this.lastSenderId = lastSenderId; }
    public void setLastMessageTime(Timestamp lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    /**
     * Verilen userId'ye göre diğer katılımcının adını döndürür
     */
    public String getOtherParticipantName(String currentUserId) {
        for (Map.Entry<String, String> entry : participantNames.entrySet()) {
            if (!entry.getKey().equals(currentUserId)) {
                return entry.getValue();
            }
        }
        return "";
    }

    public String getOtherParticipantImage(String currentUserId) {
        for (Map.Entry<String, String> entry : participantImages.entrySet()) {
            if (!entry.getKey().equals(currentUserId)) {
                return entry.getValue();
            }
        }
        return "";
    }

    public String getOtherParticipantId(String currentUserId) {
        for (String participantId : participants) {
            if (!participantId.equals(currentUserId)) {
                return participantId;
            }
        }
        return "";
    }
}
