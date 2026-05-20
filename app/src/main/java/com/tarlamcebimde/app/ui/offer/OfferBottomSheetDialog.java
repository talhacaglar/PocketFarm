package com.tarlamcebimde.app.ui.offer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Message;
import com.tarlamcebimde.app.model.Offer;
import com.tarlamcebimde.app.repository.ChatRepository;
import com.tarlamcebimde.app.repository.OfferRepository;
import com.tarlamcebimde.app.model.Chat;
import com.tarlamcebimde.app.util.FirebaseHelper;

public class OfferBottomSheetDialog extends BottomSheetDialogFragment {
    private String productId, productTitle, sellerId, sellerName;
    private double currentPrice;
    private OfferRepository offerRepository;
    private ChatRepository chatRepository;

    public static OfferBottomSheetDialog newInstance(String productId, String productTitle,
                                                     String sellerId, String sellerName, double currentPrice) {
        OfferBottomSheetDialog dialog = new OfferBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString("productId", productId);
        args.putString("productTitle", productTitle);
        args.putString("sellerId", sellerId);
        args.putString("sellerName", sellerName);
        args.putDouble("currentPrice", currentPrice);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            productTitle = getArguments().getString("productTitle");
            sellerId = getArguments().getString("sellerId");
            sellerName = getArguments().getString("sellerName");
            currentPrice = getArguments().getDouble("currentPrice");
        }
        offerRepository = new OfferRepository();
        chatRepository = new ChatRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_offer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvProductTitle = view.findViewById(R.id.tv_offer_product_title);
        TextView tvCurrentPrice = view.findViewById(R.id.tv_current_price);
        TextInputEditText etOfferPrice = view.findViewById(R.id.et_offer_price);
        TextInputEditText etRequestedKg = view.findViewById(R.id.et_requested_kg);
        TextInputEditText etOfferMessage = view.findViewById(R.id.et_offer_message);
        MaterialButton btnSendOffer = view.findViewById(R.id.btn_send_offer);

        tvProductTitle.setText(productTitle);
        tvCurrentPrice.setText(String.format("Mevcut Fiyat: ₺%.2f/kg", currentPrice));

        btnSendOffer.setOnClickListener(v -> {
            String priceStr = etOfferPrice.getText() != null ? etOfferPrice.getText().toString().trim() : "";
            String kgStr = etRequestedKg.getText() != null ? etRequestedKg.getText().toString().trim() : "";
            String msg = etOfferMessage.getText() != null ? etOfferMessage.getText().toString().trim() : "";

            if (priceStr.isEmpty() || kgStr.isEmpty()) {
                Toast.makeText(requireContext(), "Fiyat ve kg alanları gerekli", Toast.LENGTH_SHORT).show();
                return;
            }

            double offerPrice = Double.parseDouble(priceStr);
            double requestedKg = Double.parseDouble(kgStr);
            String buyerId = FirebaseHelper.getInstance().getCurrentUserId();

            FirebaseHelper.getInstance().getDb().collection("users").document(buyerId).get()
                    .addOnSuccessListener(doc -> {
                        String buyerName = doc.getString("fullName");
                        String buyerImage = doc.getString("profileImageUrl");

                        Offer offer = new Offer(productId, productTitle, buyerId, buyerName,
                                sellerId, sellerName, offerPrice, requestedKg, msg);

                        offerRepository.sendOffer(offer).addOnSuccessListener(offerId -> {
                            // Teklifi chat mesajı olarak da gönder
                            Chat chat = new Chat(buyerId, buyerName, buyerImage,
                                    sellerId, sellerName, "", productId, productTitle);
                            chatRepository.getOrCreateChat(chat).addOnSuccessListener(chatId -> {
                                Message offerMsg = Message.createOfferMessage(buyerId, buyerName, offerPrice, requestedKg);
                                chatRepository.sendMessage(chatId, offerMsg);
                            });
                            Toast.makeText(requireContext(), "Teklif gönderildi!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    });
        });
    }
}
