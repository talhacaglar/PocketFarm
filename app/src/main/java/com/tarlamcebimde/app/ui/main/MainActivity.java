package com.tarlamcebimde.app.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.ui.product.AddEditProductActivity;
import com.tarlamcebimde.app.util.FirebaseHelper;

/**
 * Ana Activity - Bottom Navigation ile 4 fragment barındırır
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add_product);

        setupBottomNavigation();
        setupFab();

        // Varsayılan olarak Home fragment'ı göster
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
                fabAdd.show();
            } else if (itemId == R.id.nav_map) {
                fragment = new MapFragment();
                fabAdd.hide();
            } else if (itemId == R.id.nav_messages) {
                fragment = new ChatListFragment();
                fabAdd.hide();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
                fabAdd.hide();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Dışarıdan belirli bir sekmeye yönlendirme
     */
    public void navigateToTab(int tabId) {
        bottomNav.setSelectedItemId(tabId);
    }
}
