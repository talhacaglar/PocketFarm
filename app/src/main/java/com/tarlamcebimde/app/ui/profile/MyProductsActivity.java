package com.tarlamcebimde.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.adapter.ProductAdapter;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.repository.ProductRepository;
import com.tarlamcebimde.app.ui.product.ProductDetailActivity;
import com.tarlamcebimde.app.util.Constants;
import com.tarlamcebimde.app.util.FirebaseHelper;
import java.util.ArrayList;
import android.view.View;

public class MyProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private RecyclerView rvProducts;
    private TextView tvEmpty;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Ürünlerim"); }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvProducts = findViewById(R.id.rv_products);
        tvEmpty = findViewById(R.id.tv_empty);
        adapter = new ProductAdapter(new ArrayList<>(), this);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId != null) {
            new ProductRepository().getProductsBySeller(userId).observe(this, products -> {
                if (products == null || products.isEmpty()) { tvEmpty.setVisibility(View.VISIBLE); rvProducts.setVisibility(View.GONE); }
                else { tvEmpty.setVisibility(View.GONE); rvProducts.setVisibility(View.VISIBLE); adapter.updateProducts(products); }
            });
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }
}
