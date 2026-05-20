package com.tarlamcebimde.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.repository.AuthRepository;
import com.tarlamcebimde.app.ui.auth.LoginActivity;
import com.tarlamcebimde.app.ui.profile.EditProfileActivity;
import com.tarlamcebimde.app.ui.profile.MyOffersActivity;
import com.tarlamcebimde.app.ui.profile.MyProductsActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Profil fragment'ı - Kullanıcı bilgileri ve navigasyon
 */
public class ProfileFragment extends Fragment {

    private CircleImageView ivProfilePhoto;
    private TextView tvFullName, tvEmail, tvCity, tvRole;
    private MaterialButton btnEditProfile, btnMyProducts, btnMyOffers, btnMessages, btnLogout;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();
        initViews(view);
        setupListeners();
        loadProfile();
    }

    private void initViews(View view) {
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo);
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvCity = view.findViewById(R.id.tv_city);
        tvRole = view.findViewById(R.id.tv_role);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnMyProducts = view.findViewById(R.id.btn_my_products);
        btnMyOffers = view.findViewById(R.id.btn_my_offers);
        btnMessages = view.findViewById(R.id.btn_messages);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditProfileActivity.class)));

        btnMyProducts.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MyProductsActivity.class)));

        btnMyOffers.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MyOffersActivity.class)));

        btnMessages.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_messages);
            }
        });

        btnLogout.setOnClickListener(v -> {
            authRepository.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadProfile() {
        authRepository.getCurrentUserData().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            tvFullName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvCity.setText(user.getCity() + (user.getDistrict() != null && !user.getDistrict().isEmpty()
                    ? ", " + user.getDistrict() : ""));

            String roleText = user.isSeller() ? "Satıcı" : user.isBuyer() ? "Alıcı" : "Yönetici";
            tvRole.setText(roleText);

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.ic_person)
                        .into(ivProfilePhoto);
            }
        });
    }
}
