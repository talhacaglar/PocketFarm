package com.tarlamcebimde.app.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Kullanıcı modeli - Firestore 'users' koleksiyonu
 */
public class User {
    @DocumentId
    private String userId;
    private String email;
    private String fullName;
    private String phone;
    private String profileImageUrl;
    private String role; // "buyer", "seller", "admin"
    private String city;
    private String district;
    @ServerTimestamp
    private Timestamp createdAt;

    // Boş constructor (Firestore için gerekli)
    public User() {}

    public User(String email, String fullName, String phone, String role, String city, String district) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.city = city;
        this.district = district;
        this.profileImageUrl = "";
    }

    // Getters
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getRole() { return role; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setRole(String role) { this.role = role; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isSeller() {
        return "seller".equals(role);
    }

    public boolean isBuyer() {
        return "buyer".equals(role);
    }
}
