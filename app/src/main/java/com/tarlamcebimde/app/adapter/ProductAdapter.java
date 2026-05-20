package com.tarlamcebimde.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private final OnProductClickListener listener;

    public interface OnProductClickListener { void onProductClick(Product product); }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateProducts(List<Product> products) { this.products = products; notifyDataSetChanged(); }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText(String.format("₺%.2f/kg", product.getPricePerKg()));
        holder.tvCategory.setText(product.getCategory());
        holder.tvAvailableKg.setText(String.format("%.1f kg", product.getAvailableKg()));
        holder.tvLocation.setText(product.getLocationCity());

        if (product.getFirstImageUrl() != null && !product.getFirstImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(product.getFirstImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override public int getItemCount() { return products.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvCategory, tvAvailableKg, tvLocation;
        ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.iv_product_image);
            tvTitle = v.findViewById(R.id.tv_title);
            tvPrice = v.findViewById(R.id.tv_price);
            tvCategory = v.findViewById(R.id.tv_category);
            tvAvailableKg = v.findViewById(R.id.tv_available_kg);
            tvLocation = v.findViewById(R.id.tv_location);
        }
    }
}
