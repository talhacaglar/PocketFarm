package com.tarlamcebimde.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Product;
import com.tarlamcebimde.app.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;

public class AdminProductsFragment extends Fragment {
    private RecyclerView rv;
    private TextView tvEmpty;
    private ProductRepository repo;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_admin_list, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        rv = v.findViewById(R.id.rv_admin_list);
        tvEmpty = v.findViewById(R.id.tv_empty);
        repo = new ProductRepository();
        AdminProductAdapter adapter = new AdminProductAdapter(new ArrayList<>(), product -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Ürün Sil")
                    .setMessage(product.getTitle() + " silinsin mi?")
                    .setPositiveButton("Evet", (d, w) -> repo.deleteProduct(product.getProductId())
                            .addOnSuccessListener(x -> Toast.makeText(requireContext(), "Silindi", Toast.LENGTH_SHORT).show()))
                    .setNegativeButton("Hayır", null).show();
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
        repo.getAllProductsAdmin().observe(getViewLifecycleOwner(), products -> {
            if (products == null || products.isEmpty()) { tvEmpty.setVisibility(View.VISIBLE); rv.setVisibility(View.GONE); }
            else { tvEmpty.setVisibility(View.GONE); rv.setVisibility(View.VISIBLE); adapter.updateProducts(products); }
        });
    }

    static class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.VH> {
        private List<Product> products;
        private final OnDeleteListener listener;
        interface OnDeleteListener { void onDelete(Product p); }
        AdminProductAdapter(List<Product> p, OnDeleteListener l) { products = p; listener = l; }
        void updateProducts(List<Product> p) { products = p; notifyDataSetChanged(); }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_admin_product, p, false)); }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(products.get(pos), listener); }
        @Override public int getItemCount() { return products.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvSeller;
            View btnDelete;
            VH(View v) { super(v); tvTitle = v.findViewById(R.id.tv_title); tvPrice = v.findViewById(R.id.tv_price); tvSeller = v.findViewById(R.id.tv_seller); btnDelete = v.findViewById(R.id.btn_delete); }
            void bind(Product p, OnDeleteListener l) {
                tvTitle.setText(p.getTitle());
                tvPrice.setText(String.format("₺%.2f/kg", p.getPricePerKg()));
                tvSeller.setText(p.getSellerName());
                btnDelete.setOnClickListener(v -> l.onDelete(p));
            }
        }
    }
}
