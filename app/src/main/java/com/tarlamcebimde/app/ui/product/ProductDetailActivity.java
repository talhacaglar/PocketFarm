package com.tarlamcebimde.app.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Chat;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.repository.ChatRepository;
import com.tarlamcebimde.app.repository.ProductRepository;
import com.tarlamcebimde.app.ui.chat.ChatActivity;
import com.tarlamcebimde.app.ui.offer.OfferBottomSheetDialog;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Ürün detay sayfası - Haritadan veya listeden tıklanınca açılır
 * Veriler veritabanından (Firestore) getirilir
 */
public class ProductDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView ivProductImage;
    private TextView tvTitle, tvPrice, tvAvailableKg, tvCategory, tvDescription;
    private TextView tvSellerName, tvSellerCity, tvAddress;
    private CircleImageView ivSellerPhoto;
    private MaterialButton btnMessage, btnOffer;
    private Toolbar toolbar;

    private ProductRepository productRepository;
    private ChatRepository chatRepository;
    private Product currentProduct;
    private User currentUser;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productRepository = new ProductRepository();
        chatRepository = new ChatRepository();

        initViews();
        setupToolbar();
        loadProduct();
        loadCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_detail);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.iv_product_image);
        tvTitle = findViewById(R.id.tv_title);
        tvPrice = findViewById(R.id.tv_price);
        tvAvailableKg = findViewById(R.id.tv_available_kg);
        tvCategory = findViewById(R.id.tv_category);
        tvDescription = findViewById(R.id.tv_description);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvSellerCity = findViewById(R.id.tv_seller_city);
        ivSellerPhoto = findViewById(R.id.iv_seller_photo);
        tvAddress = findViewById(R.id.tv_address);
        btnMessage = findViewById(R.id.btn_message);
        btnOffer = findViewById(R.id.btn_offer);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadProduct() {
        String productId = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_ID);
        if (productId == null) {
            finish();
            return;
        }

        productRepository.getProduct(productId).observe(this, product -> {
            if (product == null) return;
            currentProduct = product;
            displayProduct(product);
        });
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
                    setupButtons();
                });
    }

    private void displayProduct(Product product) {
        tvTitle.setText(product.getTitle());
        tvPrice.setText(String.format("₺%.2f/kg", product.getPricePerKg()));
        tvAvailableKg.setText(String.format("%.1f kg mevcut", product.getAvailableKg()));
        tvCategory.setText(product.getCategory());
        tvDescription.setText(product.getDescription());
        tvSellerName.setText(product.getSellerName());
        tvAddress.setText(product.getLocationAddress());

        String city = product.getLocationCity();
        tvSellerCity.setText(city != null && !city.isEmpty() ? city : "");

        if (product.getFirstImageUrl() != null && !product.getFirstImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getFirstImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(ivProductImage);
        }

        if (product.getSellerImageUrl() != null && !product.getSellerImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getSellerImageUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(ivSellerPhoto);
        }

        // Haritayı güncelle
        if (googleMap != null) {
            showProductOnMap();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(product.getTitle());
        }
    }

    private void setupButtons() {
        if (currentProduct == null || currentUser == null) return;

        String currentUserId = FirebaseHelper.getInstance().getCurrentUserId();

        // Kendi ürünüyse butonları gizle
        if (currentProduct.getSellerId().equals(currentUserId)) {
            btnMessage.setVisibility(View.GONE);
            btnOffer.setVisibility(View.GONE);
            return;
        }

        btnMessage.setOnClickListener(v -> openChat());
        btnOffer.setOnClickListener(v -> openOfferDialog());
    }

    private void openChat() {
        String currentUserId = FirebaseHelper.getInstance().getCurrentUserId();
        if (currentProduct == null || currentUser == null) return;

        Chat chat = new Chat(
                currentUserId,
                currentUser.getFullName(),
                currentUser.getProfileImageUrl(),
                currentProduct.getSellerId(),
                currentProduct.getSellerName(),
                currentProduct.getSellerImageUrl(),
                currentProduct.getProductId(),
                currentProduct.getTitle()
        );

        chatRepository.getOrCreateChat(chat)
                .addOnSuccessListener(chatId -> {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(Constants.EXTRA_CHAT_ID, chatId);
                    startActivity(intent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Sohbet açılamadı", Toast.LENGTH_SHORT).show());
    }

    private void openOfferDialog() {
        if (currentProduct == null) return;
        OfferBottomSheetDialog dialog = OfferBottomSheetDialog.newInstance(
                currentProduct.getProductId(),
                currentProduct.getTitle(),
                currentProduct.getSellerId(),
                currentProduct.getSellerName(),
                currentProduct.getPricePerKg()
        );
        dialog.show(getSupportFragmentManager(), "offer_dialog");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        if (currentProduct != null) {
            showProductOnMap();
        }
    }

    private void showProductOnMap() {
        double lat = currentProduct.getLatitude();
        double lng = currentProduct.getLongitude();
        if (lat != 0 && lng != 0) {
            LatLng location = new LatLng(lat, lng);
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(currentProduct.getTitle()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, Constants.DETAIL_ZOOM));
        }
    }
}
