package com.tarlamcebimde.app.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.repository.AuthRepository;
import com.tarlamcebimde.app.repository.UserRepository;
import com.tarlamcebimde.app.ui.main.MainActivity;
import com.tarlamcebimde.app.util.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Kayıt ekranı
 */
public class RegisterActivity extends AppCompatActivity {

    private CircleImageView ivProfilePhoto;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etCity, etDistrict;
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword, tilPhone, tilCity, tilDistrict, tilRole;
    private AutoCompleteTextView actvRole;
    private MaterialButton btnRegister;
    private View tvLogin, progressBar;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProfilePhoto.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        initViews();
        setupListeners();
        setupRoleDropdown();
    }

    private void initViews() {
        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etPhone = findViewById(R.id.et_phone);
        etCity = findViewById(R.id.et_city);
        etDistrict = findViewById(R.id.et_district);
        actvRole = findViewById(R.id.actv_role);
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        tilPhone = findViewById(R.id.til_phone);
        tilCity = findViewById(R.id.til_city);
        tilDistrict = findViewById(R.id.til_district);
        tilRole = findViewById(R.id.til_role);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        ivProfilePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void setupRoleDropdown() {
        String[] roles = {"Alıcı", "Satıcı"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, roles);
        actvRole.setAdapter(adapter);
    }

    private void attemptRegister() {
        String fullName = getText(etFullName);
        String email = getText(etEmail);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);
        String phone = getText(etPhone);
        String city = getText(etCity);
        String district = getText(etDistrict);
        String roleText = actvRole.getText().toString();

        // Doğrulama
        boolean valid = true;
        if (fullName.isEmpty()) { tilFullName.setError("Ad Soyad gerekli"); valid = false; }
        else tilFullName.setError(null);
        if (email.isEmpty()) { tilEmail.setError("E-posta gerekli"); valid = false; }
        else tilEmail.setError(null);
        if (password.isEmpty()) { tilPassword.setError("Şifre gerekli"); valid = false; }
        else if (password.length() < 6) { tilPassword.setError("En az 6 karakter"); valid = false; }
        else tilPassword.setError(null);
        if (!password.equals(confirmPassword)) { tilConfirmPassword.setError("Şifreler eşleşmiyor"); valid = false; }
        else tilConfirmPassword.setError(null);
        if (phone.isEmpty()) { tilPhone.setError("Telefon gerekli"); valid = false; }
        else tilPhone.setError(null);
        if (city.isEmpty()) { tilCity.setError("Şehir gerekli"); valid = false; }
        else tilCity.setError(null);
        if (roleText.isEmpty()) { tilRole.setError("Rol seçin"); valid = false; }
        else tilRole.setError(null);

        if (!valid) return;

        String role = roleText.equals("Satıcı") ? Constants.ROLE_SELLER : Constants.ROLE_BUYER;

        setLoading(true);

        authRepository.register(email, password)
                .addOnSuccessListener(authResult -> {
                    User user = new User(email, fullName, phone, role, city, district);

                    authRepository.saveUserToFirestore(user)
                            .addOnSuccessListener(v -> {
                                // Profil fotoğrafı varsa yükle
                                if (selectedImageUri != null) {
                                    uploadProfileImageAndNavigate();
                                } else {
                                    navigateToMain();
                                }
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Toast.makeText(this, "Kayıt hatası: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Kayıt başarısız: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void uploadProfileImageAndNavigate() {
        String userId = authRepository.getCurrentUserId();
        userRepository.uploadProfileImage(userId, selectedImageUri)
                .addOnSuccessListener(uri -> {
                    userRepository.updateProfileImage(userId, uri.toString())
                            .addOnCompleteListener(t -> navigateToMain());
                })
                .addOnFailureListener(e -> navigateToMain());
    }

    private void navigateToMain() {
        setLoading(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
