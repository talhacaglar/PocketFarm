package com.tarlamcebimde.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.adapter.OfferAdapter;
import com.tarlamcebimde.app.repository.OfferRepository;
import java.util.ArrayList;

public class AdminOffersFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_admin_list, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        RecyclerView rv = v.findViewById(R.id.rv_admin_list);
        TextView tvEmpty = v.findViewById(R.id.tv_empty);
        OfferRepository repo = new OfferRepository();
        OfferAdapter adapter = new OfferAdapter(new ArrayList<>(), null, (offer, accepted) ->
                repo.updateOfferStatus(offer.getOfferId(), accepted ? "accepted" : "rejected"));
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
        repo.getAllOffers().observe(getViewLifecycleOwner(), offers -> {
            if (offers == null || offers.isEmpty()) { tvEmpty.setVisibility(View.VISIBLE); rv.setVisibility(View.GONE); }
            else { tvEmpty.setVisibility(View.GONE); rv.setVisibility(View.VISIBLE); adapter.updateOffers(offers); }
        });
    }
}
