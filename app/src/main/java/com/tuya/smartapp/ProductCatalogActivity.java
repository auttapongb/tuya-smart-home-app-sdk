package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProductCatalogActivity extends AppCompatActivity {
    private static final String TAG = "ProductCatalogActivity";
    
    private RecyclerView recyclerView;
    private ProductCatalogAdapter adapter;
    private List<Product> productList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DebugLogger.d(TAG, "=== ProductCatalogActivity onCreate START ===");
        
        try {
            DebugLogger.d(TAG, "Setting content view");
            setContentView(R.layout.activity_product_catalog);
            DebugLogger.d(TAG, "Content view set successfully");
            
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("PANDO Products");
            }
            
            recyclerView = findViewById(R.id.recyclerViewProducts);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
            
            loadProducts();
            
            DebugLogger.d(TAG, "Products loaded: " + (productList != null ? productList.size() : 0));
            
            if (recyclerView != null && productList != null) {
                DebugLogger.d(TAG, "Setting up adapter");
                adapter = new ProductCatalogAdapter(this, productList, product -> {
                    try {
                        Intent intent = new Intent(ProductCatalogActivity.this, ProductDetailActivity.class);
                        intent.putExtra("product_id", product.getId());
                        intent.putExtra("product_name", product.getName());
                        intent.putExtra("product_price", product.getPrice());
                        intent.putExtra("product_original_price", product.getOriginalPrice());
                        intent.putExtra("product_image", product.getImageResource());
                        intent.putExtra("product_description", product.getDescription());
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Product details coming soon!", Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(adapter);
                DebugLogger.d(TAG, "Adapter set successfully");
            }
            
            DebugLogger.d(TAG, "=== ProductCatalogActivity onCreate SUCCESS ===");
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== ProductCatalogActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error loading products: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        DebugLogger.d(TAG, "=== ProductCatalogActivity onStart ===");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== ProductCatalogActivity onDestroy ===");
    }
    
    private void loadProducts() {
        productList = new ArrayList<>();
        
        // Water Fountains
        productList.add(new Product(
            1,
            "Automatic Cordless Pet Water Fountain 3.5L",
            2390,
            3590,
            "product_water_fountain_1",
            "Smart water fountain with wireless design, 3.5L capacity, ultra-quiet pump, and automatic water circulation."
        ));
        
        // Camera Feeders
        productList.add(new Product(
            2,
            "Pet Smart Camera Feeder 2.6L",
            3090,
            3990,
            "product_camera_feeder_2",
            "Smart feeder with HD camera, 2-way audio, portion control, and mobile app connectivity."
        ));
        
        productList.add(new Product(
            3,
            "Pet Smart Camera Feeder 4L",
            3690,
            5990,
            "product_camera_feeder_1",
            "Large capacity smart feeder with 1080p camera, night vision, and scheduled feeding."
        ));
        
        // Grooming Kit
        productList.add(new Product(
            4,
            "G1 Pet Grooming Vacuum Kit",
            2690,
            3590,
            "product_grooming_kit_1",
            "Professional grooming kit with 350W motor, 6 attachments, and 1.4L dust container."
        ));
        
        // Feeder with Fountain
        productList.add(new Product(
            5,
            "Pet Feeder with Water Fountain (Moonlight)",
            550,
            990,
            "product_feeder_fountain_1",
            "Dual-function feeder and water fountain combo, food-grade materials, easy to clean."
        ));
        
        // Additional Water Fountain
        productList.add(new Product(
            6,
            "Pet Automatic Wireless Water Fountain",
            2390,
            3590,
            "product_water_fountain_2",
            "Cordless design with rechargeable battery, multi-stage filtration, and LED indicators."
        ));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Product model class
    public static class Product {
        private int id;
        private String name;
        private int price;
        private int originalPrice;
        private String imageResource;
        private String description;
        
        public Product(int id, String name, int price, int originalPrice, String imageResource, String description) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.originalPrice = originalPrice;
            this.imageResource = imageResource;
            this.description = description;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public int getPrice() { return price; }
        public int getOriginalPrice() { return originalPrice; }
        public String getImageResource() { return imageResource; }
        public String getDescription() { return description; }
        public int getDiscountPercent() {
            return (int) (((originalPrice - price) * 100.0) / originalPrice);
        }
    }
}
