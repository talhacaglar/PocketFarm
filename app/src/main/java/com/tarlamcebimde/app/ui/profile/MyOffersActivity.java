package com.tarlamcebimde.app.ui.profile;

import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.adapter.OfferAdapter;
import com.tarlamcebimde.app.repository.OfferRepository;
import com.tarlamcebimde.app.util.FirebaseHelper;
import java.util.ArrayList;

public class MyOffersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Tekliflerim"); }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView rvOffers = findViewById(R.id.rv_offers);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        OfferRepository repo = new OfferRepository();
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        OfferAdapter adapter = new OfferAdapter(new ArrayList<>(), userId, (offer, accepted) -> {
            repo.updateOfferStatus(offer.getOfferId(), accepted ? "accepted" : "rejected");
        });
        rvOffers.setLayoutManager(new LinearLayoutManager(this));
        rvOffers.setAdapter(adapter);

        if (userId != null) {
            // Gelen ve giden teklifleri birleştir
            repo.getOffersBySeller(userId).observe(this, sellerOffers -> {
                repo.getOffersByBuyer(userId).observe(this, buyerOffers -> {
                    java.util.List<com.tarlamcebimde.app.model.Offer> all = new ArrayList<>();
                    if (sellerOffers != null) all.addAll(sellerOffers);
                    if (buyerOffers != null) all.addAll(buyerOffers);
                    if (all.isEmpty()) { tvEmpty.setVisibility(View.VISIBLE); rvOffers.setVisibility(View.GONE); }
                    else { tvEmpty.setVisibility(View.GONE); rvOffers.setVisibility(View.VISIBLE); adapter.updateOffers(all); }
                });
            });
        }
    }
}
