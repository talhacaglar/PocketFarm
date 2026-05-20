package com.tarlamcebimde.app.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Teklif modeli - Firestore 'offers' koleksiyonu
 */
public class Offer {
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";

    @DocumentId
    private String offerId;
    private String productId;
    private String productTitle;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private double offeredPricePerKg;
    private double requestedKg;
    private double totalPrice;
    private String status;
    private String message;
    private String chatId;
    @ServerTimestamp
    private Timestamp createdAt;

    public Offer() {
        this.status = STATUS_PENDING;
    }

    public Offer(String productId, String productTitle,
                 String buyerId, String buyerName,
                 String sellerId, String sellerName,
                 double offeredPricePerKg, double requestedKg, String message) {
        this();
        this.productId = productId;
        this.productTitle = productTitle;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.offeredPricePerKg = offeredPricePerKg;
        this.requestedKg = requestedKg;
        this.totalPrice = offeredPricePerKg * requestedKg;
        this.message = message;
    }

    // Getters
    public String getOfferId() { return offerId; }
    public String getProductId() { return productId; }
    public String getProductTitle() { return productTitle; }
    public String getBuyerId() { return buyerId; }
    public String getBuyerName() { return buyerName; }
    public String getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public double getOfferedPricePerKg() { return offeredPricePerKg; }
    public double getRequestedKg() { return requestedKg; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getChatId() { return chatId; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setOfferId(String offerId) { this.offerId = offerId; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setOfferedPricePerKg(double offeredPricePerKg) { this.offeredPricePerKg = offeredPricePerKg; }
    public void setRequestedKg(double requestedKg) { this.requestedKg = requestedKg; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isPending() { return STATUS_PENDING.equals(status); }
    public boolean isAccepted() { return STATUS_ACCEPTED.equals(status); }
    public boolean isRejected() { return STATUS_REJECTED.equals(status); }
}
