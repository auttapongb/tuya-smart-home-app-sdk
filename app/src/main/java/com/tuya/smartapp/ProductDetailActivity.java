package com.tuya.smartapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProductDetailActivity extends AppCompatActivity {
    
    private ImageView imageProduct;
    private TextView textName, textPrice, textOriginalPrice, textDiscount;
    private TextView textDescription, textStock;
    private Button btnAddToCart, btnBuyNow;
    
    private int productId;
    private String productName;
    private int productPrice;
    private int productOriginalPrice;
    private String productImage;
    private String productDescription;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Details");
        }
        
        // Get data from intent
        productId = getIntent().getIntExtra("product_id", 0);
        productName = getIntent().getStringExtra("product_name");
        productPrice = getIntent().getIntExtra("product_price", 0);
        productOriginalPrice = getIntent().getIntExtra("product_original_price", 0);
        productImage = getIntent().getStringExtra("product_image");
        productDescription = getIntent().getStringExtra("product_description");
        
        initViews();
        displayProductInfo();
        setupButtons();
    }
    
    private void initViews() {
        imageProduct = findViewById(R.id.imageProductDetail);
        textName = findViewById(R.id.textProductNameDetail);
        textPrice = findViewById(R.id.textPriceDetail);
        textOriginalPrice = findViewById(R.id.textOriginalPriceDetail);
        textDiscount = findViewById(R.id.textDiscountDetail);
        textDescription = findViewById(R.id.textDescription);
        textStock = findViewById(R.id.textStock);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }
    
    private void displayProductInfo() {
        textName.setText(productName);
        textPrice.setText(String.format("%,d ฿", productPrice));
        textOriginalPrice.setText(String.format("%,d ฿", productOriginalPrice));
        textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        
        int discount = (int) (((productOriginalPrice - productPrice) * 100.0) / productOriginalPrice);
        textDiscount.setText(String.format("Save %d%%", discount));
        
        textDescription.setText(productDescription);
        textStock.setText("In Stock");
        
        // Load product image
        int imageResId = getResources().getIdentifier(productImage, "drawable", getPackageName());
        if (imageResId != 0) {
            imageProduct.setImageResource(imageResId);
        }
    }
    
    private void setupButtons() {
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Added to cart: " + productName, Toast.LENGTH_SHORT).show();
        });
        
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
            intent.putExtra("product_name", productName);
            intent.putExtra("product_price", productPrice);
            intent.putExtra("product_image", productImage);
            startActivity(intent);
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
