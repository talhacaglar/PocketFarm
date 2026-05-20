package com.tarlamcebimde.app.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

/**
 * Kimlik doğrulama işlemleri için repository
 */
public class AuthRepository {
    private final FirebaseHelper firebase;

    public AuthRepository() {
        this.firebase = FirebaseHelper.getInstance();
    }

    /**
     * E-posta ve şifre ile giriş
     */
    public Task<AuthResult> login(String email, String password) {
        return firebase.getAuth().signInWithEmailAndPassword(email, password);
    }

    /**
     * Yeni kullanıcı kaydı
     */
    public Task<AuthResult> register(String email, String password) {
        return firebase.getAuth().createUserWithEmailAndPassword(email, password);
    }

    /**
     * Kullanıcı bilgilerini Firestore'a kaydet
     */
    public Task<Void> saveUserToFirestore(User user) {
        String userId = firebase.getCurrentUserId();
        if (userId == null) {
            return com.google.android.gms.tasks.Tasks.forException(
                    new Exception("Kullanıcı oturumu bulunamadı"));
        }
        user.setUserId(userId);
        return firebase.getDb()
                .collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(user);
    }

    /**
     * Mevcut kullanıcı bilgilerini getir
     */
    public LiveData<User> getCurrentUserData() {
        MutableLiveData<User> userData = new MutableLiveData<>();
        String userId = firebase.getCurrentUserId();

        if (userId != null) {
            firebase.getDb()
                    .collection(Constants.COLLECTION_USERS)
                    .document(userId)
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            userData.setValue(null);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            User user = snapshot.toObject(User.class);
                            userData.setValue(user);
                        }
                    });
        }
        return userData;
    }

    /**
     * Şifre güncelle
     */
    public Task<Void> updatePassword(String newPassword) {
        FirebaseUser user = firebase.getCurrentUser();
        if (user != null) {
            return user.updatePassword(newPassword);
        }
        return com.google.android.gms.tasks.Tasks.forException(
                new Exception("Kullanıcı oturumu bulunamadı"));
    }

    /**
     * E-posta güncelle
     */
    public Task<Void> updateEmail(String newEmail) {
        FirebaseUser user = firebase.getCurrentUser();
        if (user != null) {
            return user.verifyBeforeUpdateEmail(newEmail);
        }
        return com.google.android.gms.tasks.Tasks.forException(
                new Exception("Kullanıcı oturumu bulunamadı"));
    }

    public boolean isLoggedIn() {
        return firebase.isLoggedIn();
    }

    public String getCurrentUserId() {
        return firebase.getCurrentUserId();
    }

    public void signOut() {
        firebase.signOut();
    }
}
