package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "MainActivity onCreate started");
        
        try {
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout set successfully");
            initializeViews();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initializeViews() {
        
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        TextView tvFeatures = findViewById(R.id.tv_features);
        Button btnGetStarted = findViewById(R.id.btn_get_started);
        Button btnShopProducts = findViewById(R.id.btnShopProducts);
        
        if (tvAppName != null) tvAppName.setText("PANDO");
        if (tvWelcome != null) tvWelcome.setText("Welcome to PANDO Smart Home");
        if (tvFeatures != null) {
            tvFeatures.setText("Control your smart pet care devices\n\n" +
                    "✓ Water Fountains\n" +
                    "✓ Automatic Feeders\n" +
                    "✓ Pet Cameras\n" +
                    "✓ Smart Devices\n\n" +
                    "Get started to explore all features!");
        }
        
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting LoginActivity", e);
                    Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        if (btnShopProducts != null) {
            btnShopProducts.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, ProductCatalogActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting ProductCatalogActivity", e);
                    Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
