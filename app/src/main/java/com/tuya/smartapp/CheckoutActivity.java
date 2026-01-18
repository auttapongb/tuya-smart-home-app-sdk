package com.tuya.smartapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CheckoutActivity extends AppCompatActivity {
    
    private ImageView imageProduct;
    private TextView textProductName, textProductPrice, textTotal;
    private Button btnConfirmOrder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Checkout");
        }
        
        initViews();
        displayOrderInfo();
        setupButtons();
    }
    
    private void initViews() {
        imageProduct = findViewById(R.id.imageCheckoutProduct);
        textProductName = findViewById(R.id.textCheckoutProductName);
        textProductPrice = findViewById(R.id.textCheckoutPrice);
        textTotal = findViewById(R.id.textTotal);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
    }
    
    private void displayOrderInfo() {
        String productName = getIntent().getStringExtra("product_name");
        int productPrice = getIntent().getIntExtra("product_price", 0);
        String productImage = getIntent().getStringExtra("product_image");
        
        textProductName.setText(productName);
        textProductPrice.setText(String.format("%,d ฿", productPrice));
        textTotal.setText(String.format("%,d ฿", productPrice));
        
        // Load product image
        int imageResId = getResources().getIdentifier(productImage, "drawable", getPackageName());
        if (imageResId != 0) {
            imageProduct.setImageResource(imageResId);
        }
    }
    
    private void setupButtons() {
        btnConfirmOrder.setOnClickListener(v -> {
            // Placeholder for payment integration
            Toast.makeText(this, "Payment integration coming soon!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Order confirmed (Demo)", Toast.LENGTH_SHORT).show();
            finish();
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
