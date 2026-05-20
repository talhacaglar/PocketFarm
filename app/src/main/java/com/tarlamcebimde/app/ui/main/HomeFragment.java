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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.adapter.ProductAdapter;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.repository.ProductRepository;
import com.tarlamcebimde.app.ui.product.ProductDetailActivity;
import com.tarlamcebimde.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Ana sayfa - Ürün listesi (en ucuzdan pahalıya)
 */
public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView rvProducts;
    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroupCategories;
    private TextView tvEmpty;
    private ProductAdapter adapter;
    private ProductRepository productRepository;
    private String selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository = new ProductRepository();
        initViews(view);
        setupCategoryChips();
        setupRecyclerView();
        loadProducts();
    }

    private void initViews(View view) {
        rvProducts = view.findViewById(R.id.rv_products);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        chipGroupCategories = view.findViewById(R.id.chip_group_categories);
        tvEmpty = view.findViewById(R.id.tv_empty);

        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(this::loadProducts);
    }

    private void setupCategoryChips() {
        // "Tümü" chip'i
        Chip allChip = new Chip(requireContext());
        allChip.setText("Tümü");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategory = null;
            loadProducts();
        });
        chipGroupCategories.addView(allChip);

        // Kategori chip'leri
        for (String category : Constants.CATEGORIES) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                loadProducts();
            });
            chipGroupCategories.addView(chip);
        }

        chipGroupCategories.setSingleSelection(true);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new ArrayList<>(), this);
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(adapter);
    }

    private void loadProducts() {
        swipeRefresh.setRefreshing(true);

        if (selectedCategory != null) {
            productRepository.getProductsByCategory(selectedCategory)
                    .observe(getViewLifecycleOwner(), this::updateProductList);
        } else {
            productRepository.getAllProducts()
                    .observe(getViewLifecycleOwner(), this::updateProductList);
        }
    }

    private void updateProductList(List<Product> products) {
        swipeRefresh.setRefreshing(false);
        if (products == null || products.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            adapter.updateProducts(products);
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }
}
