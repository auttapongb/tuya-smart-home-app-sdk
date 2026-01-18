package com.tuya.smartapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductCatalogAdapter extends RecyclerView.Adapter<ProductCatalogAdapter.ProductViewHolder> {
    
    private Context context;
    private List<ProductCatalogActivity.Product> products;
    private OnProductClickListener listener;
    
    public interface OnProductClickListener {
        void onProductClick(ProductCatalogActivity.Product product);
    }
    
    public ProductCatalogAdapter(Context context, List<ProductCatalogActivity.Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_catalog, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductCatalogActivity.Product product = products.get(position);
        
        holder.textName.setText(product.getName());
        holder.textPrice.setText(String.format("%,d ฿", product.getPrice()));
        holder.textOriginalPrice.setText(String.format("%,d ฿", product.getOriginalPrice()));
        holder.textOriginalPrice.setPaintFlags(holder.textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.textDiscount.setText(String.format("-%d%%", product.getDiscountPercent()));
        
        // Load product image
        int imageResId = context.getResources().getIdentifier(
            product.getImageResource(), "drawable", context.getPackageName()
        );
        if (imageResId != 0) {
            holder.imageProduct.setImageResource(imageResId);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return products.size();
    }
    
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textName;
        TextView textPrice;
        TextView textOriginalPrice;
        TextView textDiscount;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textName = itemView.findViewById(R.id.textProductName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textOriginalPrice = itemView.findViewById(R.id.textOriginalPrice);
            textDiscount = itemView.findViewById(R.id.textDiscount);
        }
    }
}
