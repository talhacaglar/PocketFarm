package com.tarlamcebimde.app.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Firebase servislerine merkezi erişim sağlayan yardımcı sınıf
 */
public class FirebaseHelper {
    private static FirebaseHelper instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public StorageReference getProfileImageRef(String userId) {
        return storage.getReference()
                .child(Constants.STORAGE_PROFILE_IMAGES)
                .child(userId + ".jpg");
    }

    public StorageReference getProductImageRef(String productId, String fileName) {
        return storage.getReference()
                .child(Constants.STORAGE_PRODUCT_IMAGES)
                .child(productId)
                .child(fileName);
    }

    public void signOut() {
        auth.signOut();
    }
}
