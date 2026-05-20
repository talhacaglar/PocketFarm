package com.tarlamcebimde.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.ui.auth.LoginActivity;
import com.tarlamcebimde.app.util.FirebaseHelper;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Yönetici Paneli");

        BottomNavigationView nav = findViewById(R.id.admin_bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();
            if (id == R.id.admin_users) f = new AdminUsersFragment();
            else if (id == R.id.admin_products) f = new AdminProductsFragment();
            else if (id == R.id.admin_offers) f = new AdminOffersFragment();
            if (f != null) { getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, f).commit(); return true; }
            return false;
        });

        // Çıkış butonu toolbar'da
        toolbar.inflateMenu(R.menu.menu_admin);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                FirebaseHelper.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, new AdminUsersFragment()).commit();
    }
}
