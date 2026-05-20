package com.tarlamcebimde.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.ui.auth.LoginActivity;
import com.tarlamcebimde.app.ui.main.MainActivity;
import com.tarlamcebimde.app.util.FirebaseHelper;

/**
 * Uygulama açılış ekranı - Oturum kontrolü yapıp yönlendirir
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 saniye

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (FirebaseHelper.getInstance().isLoggedIn()) {
                // Kullanıcı rolünü kontrol et ve yönlendir
                checkUserRoleAndNavigate();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }

    private void checkUserRoleAndNavigate() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        FirebaseHelper.getInstance().getDb()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    Intent intent;
                    if (doc.exists() && "admin".equals(doc.getString("role"))) {
                        intent = new Intent(this, com.tarlamcebimde.app.ui.admin.AdminActivity.class);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
    }
}
