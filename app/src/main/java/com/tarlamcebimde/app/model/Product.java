package com.tarlamcebimde.app.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ürün modeli - Firestore 'products' koleksiyonu
 */
public class Product {
    @DocumentId
    private String productId;
    private String title;
    private String description;
    private String category;
    private double pricePerKg;
    private double availableKg;
    private List<String> imageUrls;
    private String sellerId;
    private String sellerName;
    private String sellerImageUrl;
    private Map<String, Object> location; // lat, lng, city, district, address
    private boolean isActive;
    private boolean isSold;
    @ServerTimestamp
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product() {
        this.imageUrls = new ArrayList<>();
        this.location = new HashMap<>();
        this.isActive = true;
        this.isSold = false;
    }

    public Product(String title, String description, String category,
                   double pricePerKg, double availableKg,
                   String sellerId, String sellerName) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.pricePerKg = pricePerKg;
        this.availableKg = availableKg;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
    }

    // Getters
    public String getProductId() { return productId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPricePerKg() { return pricePerKg; }
    public double getAvailableKg() { return availableKg; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getSellerImageUrl() { return sellerImageUrl; }
    public Map<String, Object> getLocation() { return location; }
    public boolean isActive() { return isActive; }
    public boolean isSold() { return isSold; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Setters
    public void setProductId(String productId) { this.productId = productId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }
    public void setAvailableKg(double availableKg) { this.availableKg = availableKg; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setSellerImageUrl(String sellerImageUrl) { this.sellerImageUrl = sellerImageUrl; }
    public void setLocation(Map<String, Object> location) { this.location = location; }
    public void setActive(boolean active) { isActive = active; }
    public void setSold(boolean sold) { isSold = sold; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public double getLatitude() {
        if (location != null && location.containsKey("lat")) {
            return ((Number) location.get("lat")).doubleValue();
        }
        return 0;
    }

    public double getLongitude() {
        if (location != null && location.containsKey("lng")) {
            return ((Number) location.get("lng")).doubleValue();
        }
        return 0;
    }

    public String getLocationCity() {
        if (location != null && location.containsKey("city")) {
            return (String) location.get("city");
        }
        return "";
    }

    public String getLocationAddress() {
        if (location != null && location.containsKey("address")) {
            return (String) location.get("address");
        }
        return "";
    }

    public String getFirstImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return "";
    }

    public void setLocationData(double lat, double lng, String city, String district, String address) {
        this.location = new HashMap<>();
        this.location.put("lat", lat);
        this.location.put("lng", lng);
        this.location.put("city", city);
        this.location.put("district", district);
        this.location.put("address", address);
    }
}
