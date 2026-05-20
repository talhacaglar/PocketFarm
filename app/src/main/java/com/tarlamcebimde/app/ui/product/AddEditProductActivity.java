package com.tarlamcebimde.app.ui.product;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.repository.ProductRepository;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Ürün ekleme/düzenleme ekranı
 * Haritadan konum seçimi yapılabilir
 */
public class AddEditProductActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText etTitle, etDescription, etPrice, etAvailableKg;
    private TextInputLayout tilTitle, tilDescription, tilPrice, tilAvailableKg, tilCategory;
    private AutoCompleteTextView actvCategory;
    private ImageView ivProductPhoto;
    private MaterialButton btnSave;
    private Toolbar toolbar;
    private View progressBar;

    private ProductRepository productRepository;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private Uri selectedImageUri;
    private User currentUser;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProductPhoto.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        productRepository = new ProductRepository();
        initViews();
        setupToolbar();
        setupCategoryDropdown();
        setupListeners();
        loadCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_pick_location);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etAvailableKg = findViewById(R.id.et_available_kg);
        actvCategory = findViewById(R.id.actv_category);
        ivProductPhoto = findViewById(R.id.iv_product_photo);
        btnSave = findViewById(R.id.btn_save);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        tilTitle = findViewById(R.id.til_title);
        tilDescription = findViewById(R.id.til_description);
        tilPrice = findViewById(R.id.til_price);
        tilAvailableKg = findViewById(R.id.til_available_kg);
        tilCategory = findViewById(R.id.til_category);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ürün Ekle");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Constants.CATEGORIES);
        actvCategory.setAdapter(adapter);
    }

    private void setupListeners() {
        ivProductPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void loadCurrentUser() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) return;

        FirebaseHelper.getInstance().getDb()
                .collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    currentUser = doc.toObject(User.class);
                });
    }

    private void saveProduct() {
        String title = getText(etTitle);
        String description = getText(etDescription);
        String priceStr = getText(etPrice);
        String kgStr = getText(etAvailableKg);
        String category = actvCategory.getText().toString();

        // Doğrulama
        boolean valid = true;
        if (title.isEmpty()) { tilTitle.setError("Gerekli"); valid = false; } else tilTitle.setError(null);
        if (description.isEmpty()) { tilDescription.setError("Gerekli"); valid = false; } else tilDescription.setError(null);
        if (priceStr.isEmpty()) { tilPrice.setError("Gerekli"); valid = false; } else tilPrice.setError(null);
        if (kgStr.isEmpty()) { tilAvailableKg.setError("Gerekli"); valid = false; } else tilAvailableKg.setError(null);
        if (category.isEmpty()) { tilCategory.setError("Gerekli"); valid = false; } else tilCategory.setError(null);

        if (!valid) return;
        if (currentUser == null) {
            Toast.makeText(this, "Kullanıcı bilgileri yüklenemedi", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        double price = Double.parseDouble(priceStr);
        double kg = Double.parseDouble(kgStr);
        String userId = FirebaseHelper.getInstance().getCurrentUserId();

        Product product = new Product(title, description, category, price, kg,
                userId, currentUser.getFullName());
        product.setSellerImageUrl(currentUser.getProfileImageUrl());

        if (selectedLocation != null) {
            product.setLocationData(selectedLocation.latitude, selectedLocation.longitude,
                    currentUser.getCity(), currentUser.getDistrict(), "");
        }

        // Önce ürünü kaydet
        productRepository.addProduct(product)
                .addOnSuccessListener(productId -> {
                    // Fotoğraf varsa yükle
                    if (selectedImageUri != null) {
                        productRepository.uploadProductImage(productId, selectedImageUri)
                                .addOnSuccessListener(imageUrl -> {
                                    List<String> urls = new ArrayList<>();
                                    urls.add(imageUrl);
                                    product.setProductId(productId);
                                    product.setImageUrls(urls);
                                    productRepository.updateProduct(product)
                                            .addOnCompleteListener(t -> {
                                                setLoading(false);
                                                Toast.makeText(this, "Ürün eklendi!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    setLoading(false);
                                    Toast.makeText(this, "Ürün eklendi (fotoğraf yüklenemedi)", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    } else {
                        setLoading(false);
                        Toast.makeText(this, "Ürün eklendi!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        LatLng turkey = new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, Constants.DEFAULT_ZOOM));

        googleMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Seçilen Konum"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.CITY_ZOOM));
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
