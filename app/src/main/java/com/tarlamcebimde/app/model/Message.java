package com.tarlamcebimde.app.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Mesaj modeli - Firestore 'chats/{chatId}/messages' subcollection
 */
public class Message {
    @DocumentId
    private String messageId;
    private String senderId;
    private String senderName;
    private String text;
    private String type; // "text", "offer", "system"
    @ServerTimestamp
    private Timestamp timestamp;

    public Message() {}

    public Message(String senderId, String senderName, String text, String type) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.type = type;
    }

    public static Message createTextMessage(String senderId, String senderName, String text) {
        return new Message(senderId, senderName, text, "text");
    }

    public static Message createOfferMessage(String senderId, String senderName,
                                              double pricePerKg, double kg) {
        String text = String.format("💰 Teklif: ₺%.2f/kg × %.1f kg = ₺%.2f",
                pricePerKg, kg, pricePerKg * kg);
        return new Message(senderId, senderName, text, "offer");
    }

    public static Message createSystemMessage(String text) {
        return new Message("system", "Sistem", text, "system");
    }

    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getText() { return text; }
    public String getType() { return type; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setText(String text) { this.text = text; }
    public void setType(String type) { this.type = type; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public boolean isSentBy(String userId) {
        return senderId != null && senderId.equals(userId);
    }

    public boolean isSystemMessage() {
        return "system".equals(type);
    }

    public boolean isOfferMessage() {
        return "offer".equals(type);
    }
}
