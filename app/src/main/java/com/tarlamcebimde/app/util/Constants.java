package com.tarlamcebimde.app.util;

/**
 * Uygulama genelinde kullanılan sabitler
 */
public class Constants {
    // Firestore Koleksiyonları
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_CHATS = "chats";
    public static final String COLLECTION_MESSAGES = "messages";
    public static final String COLLECTION_OFFERS = "offers";

    // Kullanıcı Rolleri
    public static final String ROLE_BUYER = "buyer";
    public static final String ROLE_SELLER = "seller";
    public static final String ROLE_ADMIN = "admin";

    // Ürün Kategorileri
    public static final String[] CATEGORIES = {
        "Meyve", "Sebze", "Tahıl", "Baklagil", "Süt Ürünleri", "Diğer"
    };

    // Intent Extras
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_CHAT_ID = "chat_id";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_PRODUCT = "product";
    public static final String EXTRA_IS_EDIT = "is_edit";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";

    // Firebase Storage Paths
    public static final String STORAGE_PROFILE_IMAGES = "profile_images";
    public static final String STORAGE_PRODUCT_IMAGES = "product_images";

    // Teklif Durumları
    public static final String OFFER_PENDING = "pending";
    public static final String OFFER_ACCEPTED = "accepted";
    public static final String OFFER_REJECTED = "rejected";

    // Varsayılan Harita Konumu (Türkiye merkezi)
    public static final double DEFAULT_LAT = 39.9334;
    public static final double DEFAULT_LNG = 32.8597;
    public static final float DEFAULT_ZOOM = 6f;
    public static final float CITY_ZOOM = 12f;
    public static final float DETAIL_ZOOM = 15f;
}
