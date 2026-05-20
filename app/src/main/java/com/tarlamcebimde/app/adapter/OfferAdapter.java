package com.tarlamcebimde.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Offer;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    private List<Offer> offers;
    private final String currentUserId;
    private final OnOfferActionListener listener;

    public interface OnOfferActionListener { void onAction(Offer offer, boolean accepted); }

    public OfferAdapter(List<Offer> offers, String currentUserId, OnOfferActionListener listener) {
        this.offers = offers; this.currentUserId = currentUserId; this.listener = listener;
    }

    public void updateOffers(List<Offer> offers) { this.offers = offers; notifyDataSetChanged(); }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        holder.tvProduct.setText(offer.getProductTitle());
        holder.tvBuyer.setText("Alıcı: " + offer.getBuyerName());
        holder.tvPrice.setText(String.format("₺%.2f/kg × %.1f kg = ₺%.2f",
                offer.getOfferedPricePerKg(), offer.getRequestedKg(), offer.getTotalPrice()));

        String statusText;
        int statusColor;
        if (offer.isPending()) { statusText = "Beklemede"; statusColor = 0xFFFF9800; }
        else if (offer.isAccepted()) { statusText = "Kabul Edildi"; statusColor = 0xFF4CAF50; }
        else { statusText = "Reddedildi"; statusColor = 0xFFF44336; }
        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(statusColor);

        boolean isSeller = currentUserId != null && currentUserId.equals(offer.getSellerId());
        boolean showButtons = isSeller && offer.isPending();
        holder.btnAccept.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        holder.btnReject.setVisibility(showButtons ? View.VISIBLE : View.GONE);

        holder.btnAccept.setOnClickListener(v -> listener.onAction(offer, true));
        holder.btnReject.setOnClickListener(v -> listener.onAction(offer, false));
    }

    @Override public int getItemCount() { return offers.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProduct, tvBuyer, tvPrice, tvStatus;
        MaterialButton btnAccept, btnReject;
        ViewHolder(View v) {
            super(v);
            tvProduct = v.findViewById(R.id.tv_offer_product);
            tvBuyer = v.findViewById(R.id.tv_offer_buyer);
            tvPrice = v.findViewById(R.id.tv_offer_price);
            tvStatus = v.findViewById(R.id.tv_offer_status);
            btnAccept = v.findViewById(R.id.btn_accept);
            btnReject = v.findViewById(R.id.btn_reject);
        }
    }
}
