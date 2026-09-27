package com.tarlamcebimde.app.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.Query;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Ürün işlemleri için repository
 */
public class ProductRepository {
    private final FirebaseHelper firebase;

    public ProductRepository() {
        this.firebase = FirebaseHelper.getInstance();
    }

    /**
     * Tüm aktif ürünleri getir (en ucuzdan pahalıya)
     */
    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productsData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .whereEqualTo("isActive", true)
                .orderBy("pricePerKg", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        productsData.setValue(new ArrayList<>());
                        return;
                    }
                    productsData.setValue(snapshots.toObjects(Product.class));
                });
        return productsData;
    }

    /**
     * Kategoriye göre ürünleri getir (en ucuzdan pahalıya)
     */
    public LiveData<List<Product>> getProductsByCategory(String category) {
        MutableLiveData<List<Product>> productsData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .whereEqualTo("isActive", true)
                .whereEqualTo("category", category)
                .orderBy("pricePerKg", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        productsData.setValue(new ArrayList<>());
                        return;
                    }
                    productsData.setValue(snapshots.toObjects(Product.class));
                });
        return productsData;
    }

    /**
     * Satıcının ürünlerini getir
     */
    public LiveData<List<Product>> getProductsBySeller(String sellerId) {
        MutableLiveData<List<Product>> productsData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .whereEqualTo("sellerId", sellerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        productsData.setValue(new ArrayList<>());
                        return;
                    }
                    productsData.setValue(snapshots.toObjects(Product.class));
                });
        return productsData;
    }

    /**
     * Tek ürün getir
     */
    public LiveData<Product> getProduct(String productId) {
        MutableLiveData<Product> productData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .document(productId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) {
                        productData.setValue(null);
                        return;
                    }
                    if (snapshot.exists()) {
                        productData.setValue(snapshot.toObject(Product.class));
                    }
                });
        return productData;
    }

    /**
     * Ürün ekle
     */
    public Task<String> addProduct(Product product) {
        String productId = firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .document().getId();
        product.setProductId(productId);

        return firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .document(productId)
                .set(product)
                .onSuccessTask(result -> com.google.android.gms.tasks.Tasks.forResult(productId));
    }

    /**
     * Ürün güncelle
     */
    public Task<Void> updateProduct(Product product) {
        return firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .document(product.getProductId())
                .set(product);
    }

    /**
     * Ürün sil
     */
    public Task<Void> deleteProduct(String productId) {
        return firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .document(productId)
                .delete();
    }

    /**
     * Ürün fotoğrafı yükle
     */
    public Task<String> uploadProductImage(String productId, Uri imageUri) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        return firebase.getProductImageRef(productId, fileName)
                .putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return firebase.getProductImageRef(productId, fileName).getDownloadUrl();
                })
                .continueWith(task -> task.getResult().toString());
    }

    /**
     * Tüm ürünleri getir - Admin için (aktif/pasif fark etmez)
     */
    public LiveData<List<Product>> getAllProductsAdmin() {
        MutableLiveData<List<Product>> productsData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_PRODUCTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        productsData.setValue(new ArrayList<>());
                        return;
                    }
                    productsData.setValue(snapshots.toObjects(Product.class));
                });
        return productsData;
    }
}
