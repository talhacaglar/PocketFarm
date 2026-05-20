package com.tarlamcebimde.app.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.repository.AuthRepository;
import com.tarlamcebimde.app.repository.UserRepository;
import com.tarlamcebimde.app.util.FirebaseHelper;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView ivPhoto;
    private TextInputEditText etName, etPhone, etCity, etDistrict, etNewPassword;
    private MaterialButton btnSave, btnChangePassword;
    private View progressBar;
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> picker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) { selectedImageUri = uri; ivPhoto.setImageURI(uri); }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userRepository = new UserRepository();
        authRepository = new AuthRepository();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Profili Düzenle"); }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivPhoto = findViewById(R.id.iv_profile_photo);
        etName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etCity = findViewById(R.id.et_city);
        etDistrict = findViewById(R.id.et_district);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSave = findViewById(R.id.btn_save);
        btnChangePassword = findViewById(R.id.btn_change_password);
        progressBar = findViewById(R.id.progress_bar);

        ivPhoto.setOnClickListener(v -> picker.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
        loadProfile();
    }

    private void loadProfile() {
        authRepository.getCurrentUserData().observe(this, user -> {
            if (user == null) return;
            etName.setText(user.getFullName());
            etPhone.setText(user.getPhone());
            etCity.setText(user.getCity());
            etDistrict.setText(user.getDistrict());
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty())
                Glide.with(this).load(user.getProfileImageUrl()).placeholder(R.drawable.ic_person).into(ivPhoto);
        });
    }

    private void saveProfile() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) return;
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", etName.getText().toString().trim());
        updates.put("phone", etPhone.getText().toString().trim());
        updates.put("city", etCity.getText().toString().trim());
        updates.put("district", etDistrict.getText().toString().trim());

        if (selectedImageUri != null) {
            userRepository.uploadProfileImage(userId, selectedImageUri)
                    .addOnSuccessListener(uri -> {
                        updates.put("profileImageUrl", uri.toString());
                        userRepository.updateUser(userId, updates).addOnCompleteListener(t -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> { progressBar.setVisibility(View.GONE); Toast.makeText(this, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show(); });
        } else {
            userRepository.updateUser(userId, updates).addOnCompleteListener(t -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void changePassword() {
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        if (newPass.length() < 6) { Toast.makeText(this, "En az 6 karakter", Toast.LENGTH_SHORT).show(); return; }
        authRepository.updatePassword(newPass)
                .addOnSuccessListener(v -> Toast.makeText(this, "Şifre güncellendi", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
