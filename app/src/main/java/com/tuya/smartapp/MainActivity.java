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
    private static final String APP_VERSION = "3.3-Debug";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "========================================");
        Log.d(TAG, "=== PANDO APP STARTING ===");
        Log.d(TAG, "=== Version: " + APP_VERSION + " ===");
        Log.d(TAG, "========================================");
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
        Log.d(TAG, "initializeViews() started");
        
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        TextView tvFeatures = findViewById(R.id.tv_features);
        Button btnGetStarted = findViewById(R.id.btn_get_started);
        Button btnShopProducts = findViewById(R.id.btnShopProducts);
        
        Log.d(TAG, "Views found - tvAppName: " + (tvAppName != null) + 
                   ", tvWelcome: " + (tvWelcome != null) +
                   ", tvFeatures: " + (tvFeatures != null) +
                   ", btnGetStarted: " + (btnGetStarted != null) +
                   ", btnShopProducts: " + (btnShopProducts != null));
        
        if (tvAppName != null) {
            tvAppName.setText("PANDO v" + APP_VERSION);
            Log.d(TAG, "App name set with version");
        }
        if (tvWelcome != null) tvWelcome.setText("Welcome to PANDO Smart Home");
        if (tvFeatures != null) {
            tvFeatures.setText("Control your smart pet care devices\n\n" +
                    "✓ Water Fountains\n" +
                    "✓ Automatic Feeders\n" +
                    "✓ Pet Cameras\n" +
                    "✓ Smart Devices\n\n" +
                    "Get started to explore all features!\n\n" +
                    "Version: " + APP_VERSION);
            Log.d(TAG, "Features text set");
        }
        
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                Log.d(TAG, ">>> GET STARTED button clicked");
                try {
                    Log.d(TAG, "Creating intent for LoginActivity");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    Log.d(TAG, "Starting LoginActivity");
                    startActivity(intent);
                    Log.d(TAG, "LoginActivity started successfully");
                } catch (Exception e) {
                    Log.e(TAG, "!!! ERROR starting LoginActivity !!!", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            Log.d(TAG, "Get Started button listener set");
        }
        
        if (btnShopProducts != null) {
            btnShopProducts.setOnClickListener(v -> {
                Log.d(TAG, ">>> SHOP PRODUCTS button clicked");
                try {
                    Log.d(TAG, "Creating intent for ProductCatalogActivity");
                    Intent intent = new Intent(MainActivity.this, ProductCatalogActivity.class);
                    Log.d(TAG, "Starting ProductCatalogActivity");
                    startActivity(intent);
                    Log.d(TAG, "ProductCatalogActivity started successfully");
                } catch (Exception e) {
                    Log.e(TAG, "!!! ERROR starting ProductCatalogActivity !!!", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            Log.d(TAG, "Shop Products button listener set");
        }
        
        Log.d(TAG, "initializeViews() completed successfully");
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "=== MainActivity onStart ===");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "=== MainActivity onResume ===");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "=== MainActivity onPause ===");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=== MainActivity onDestroy ===");
    }
}
