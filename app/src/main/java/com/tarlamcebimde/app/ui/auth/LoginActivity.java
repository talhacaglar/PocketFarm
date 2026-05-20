package com.tarlamcebimde.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.repository.AuthRepository;
import com.tarlamcebimde.app.ui.admin.AdminActivity;
import com.tarlamcebimde.app.ui.main.MainActivity;
import com.tarlamcebimde.app.util.FirebaseHelper;

/**
 * Giriş ekranı
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton btnLogin;
    private View tvRegister;
    private View progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Doğrulama
        if (email.isEmpty()) {
            tilEmail.setError("E-posta gerekli");
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Şifre gerekli");
            return;
        }

        tilEmail.setError(null);
        tilPassword.setError(null);
        setLoading(true);

        authRepository.login(email, password)
                .addOnSuccessListener(authResult -> {
                    // Kullanıcı rolünü kontrol et
                    checkUserRoleAndNavigate();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Giriş başarısız: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void checkUserRoleAndNavigate() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        FirebaseHelper.getInstance().getDb()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    setLoading(false);
                    Intent intent;
                    if (doc.exists() && "admin".equals(doc.getString("role"))) {
                        intent = new Intent(this, AdminActivity.class);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}
