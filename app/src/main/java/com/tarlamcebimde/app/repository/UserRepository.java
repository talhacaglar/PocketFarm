package com.tarlamcebimde.app.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kullanıcı işlemleri için repository
 */
public class UserRepository {
    private final FirebaseHelper firebase;

    public UserRepository() {
        this.firebase = FirebaseHelper.getInstance();
    }

    /**
     * Kullanıcı bilgilerini getir
     */
    public LiveData<User> getUser(String userId) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_USERS)
                .document(userId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) {
                        userData.setValue(null);
                        return;
                    }
                    if (snapshot.exists()) {
                        userData.setValue(snapshot.toObject(User.class));
                    }
                });
        return userData;
    }

    /**
     * Tüm kullanıcıları getir (Admin için)
     */
    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> usersData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_USERS)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        usersData.setValue(null);
                        return;
                    }
                    usersData.setValue(snapshots.toObjects(User.class));
                });
        return usersData;
    }

    /**
     * Kullanıcı bilgilerini güncelle
     */
    public Task<Void> updateUser(String userId, Map<String, Object> updates) {
        return firebase.getDb()
                .collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update(updates);
    }

    /**
     * Profil fotoğrafı yükle
     */
    public Task<Uri> uploadProfileImage(String userId, Uri imageUri) {
        return firebase.getProfileImageRef(userId)
                .putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return firebase.getProfileImageRef(userId).getDownloadUrl();
                });
    }

    /**
     * Profil fotoğrafı URL'sini güncelle
     */
    public Task<Void> updateProfileImage(String userId, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageUrl", imageUrl);
        return updateUser(userId, updates);
    }

    /**
     * Kullanıcı sil (Admin için)
     */
    public Task<Void> deleteUser(String userId) {
        return firebase.getDb()
                .collection(Constants.COLLECTION_USERS)
                .document(userId)
                .delete();
    }
}
