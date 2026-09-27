package com.tarlamcebimde.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.tarlamcebimde.app.model.Offer;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Teklif işlemleri için repository
 */
public class OfferRepository {
    private final FirebaseHelper firebase;

    public OfferRepository() {
        this.firebase = FirebaseHelper.getInstance();
    }

    /**
     * Teklif gönder
     */
    public Task<String> sendOffer(Offer offer) {
        String offerId = firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .document().getId();
        offer.setOfferId(offerId);

        return firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .document(offerId)
                .set(offer)
                .onSuccessTask(result -> com.google.android.gms.tasks.Tasks.forResult(offerId));
    }

    /**
     * Alıcının gönderdiği teklifleri getir
     */
    public LiveData<List<Offer>> getOffersByBuyer(String buyerId) {
        MutableLiveData<List<Offer>> offersData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .whereEqualTo("buyerId", buyerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        offersData.setValue(new ArrayList<>());
                        return;
                    }
                    offersData.setValue(snapshots.toObjects(Offer.class));
                });
        return offersData;
    }

    /**
     * Satıcıya gelen teklifleri getir
     */
    public LiveData<List<Offer>> getOffersBySeller(String sellerId) {
        MutableLiveData<List<Offer>> offersData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .whereEqualTo("sellerId", sellerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        offersData.setValue(new ArrayList<>());
                        return;
                    }
                    offersData.setValue(snapshots.toObjects(Offer.class));
                });
        return offersData;
    }

    /**
     * Teklif durumunu güncelle (kabul/red)
     */
    public Task<Void> updateOfferStatus(String offerId, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        return firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .document(offerId)
                .update(updates);
    }

    /**
     * Teklif sil
     */
    public Task<Void> deleteOffer(String offerId) {
        return firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .document(offerId)
                .delete();
    }

    /**
     * Tüm teklifleri getir (Admin için)
     */
    public LiveData<List<Offer>> getAllOffers() {
        MutableLiveData<List<Offer>> offersData = new MutableLiveData<>();
        firebase.getDb()
                .collection(Constants.COLLECTION_OFFERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        offersData.setValue(new ArrayList<>());
                        return;
                    }
                    offersData.setValue(snapshots.toObjects(Offer.class));
                });
        return offersData;
    }
}
