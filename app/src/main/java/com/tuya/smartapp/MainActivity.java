package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements DebugLogger.LogUpdateListener {
    private static final String TAG = "MainActivity";
    private static final String APP_VERSION = "3.5-Fixed";
    
    private View debugLogContainer;
    private TextView tvLogs;
    private ScrollView scrollLogs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DebugLogger.d(TAG, "========================================");
        DebugLogger.d(TAG, "=== PANDO APP STARTING ===");
        DebugLogger.d(TAG, "=== Version: " + APP_VERSION + " ===");
        DebugLogger.d(TAG, "========================================");
        DebugLogger.d(TAG, "MainActivity onCreate started");
        
        try {
            setContentView(R.layout.activity_main);
            DebugLogger.d(TAG, "Layout set successfully");
            
            setupDebugViewer();
            initializeViews();
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void setupDebugViewer() {
        DebugLogger.d(TAG, "Setting up debug viewer");
        
        debugLogContainer = findViewById(R.id.debug_log_container);
        tvLogs = findViewById(R.id.tv_logs);
        scrollLogs = findViewById(R.id.scroll_logs);
        
        Button btnShowDebug = findViewById(R.id.btn_show_debug);
        Button btnClearLogs = findViewById(R.id.btn_clear_logs);
        Button btnCloseLogs = findViewById(R.id.btn_close_logs);
        
        if (btnShowDebug != null) {
            btnShowDebug.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Show debug logs button clicked");
                if (debugLogContainer != null) {
                    debugLogContainer.setVisibility(View.VISIBLE);
                    updateLogDisplay();
                }
            });
        }
        
        if (btnClearLogs != null) {
            btnClearLogs.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Clear logs button clicked");
                DebugLogger.clear();
            });
        }
        
        if (btnCloseLogs != null) {
            btnCloseLogs.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Hide logs button clicked");
                if (debugLogContainer != null) {
                    debugLogContainer.setVisibility(View.GONE);
                }
            });
        }
        
        // Set this activity as the log update listener
        DebugLogger.setListener(this);
        
        DebugLogger.d(TAG, "Debug viewer setup complete");
    }
    
    private void initializeViews() {
        DebugLogger.d(TAG, "initializeViews() started");
        
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        TextView tvFeatures = findViewById(R.id.tv_features);
        Button btnGetStarted = findViewById(R.id.btn_get_started);
        Button btnShopProducts = findViewById(R.id.btnShopProducts);
        
        DebugLogger.d(TAG, "Views found - tvAppName: " + (tvAppName != null) + 
                   ", tvWelcome: " + (tvWelcome != null) +
                   ", tvFeatures: " + (tvFeatures != null) +
                   ", btnGetStarted: " + (btnGetStarted != null) +
                   ", btnShopProducts: " + (btnShopProducts != null));
        
        if (tvAppName != null) {
            tvAppName.setText("PANDO v" + APP_VERSION);
            DebugLogger.d(TAG, "App name set with version");
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
            DebugLogger.d(TAG, "Features text set");
        }
        
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                DebugLogger.d(TAG, ">>> GET STARTED button clicked");
                try {
                    DebugLogger.d(TAG, "Creating intent for LoginActivity");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    DebugLogger.d(TAG, "Starting LoginActivity");
                    startActivity(intent);
                    DebugLogger.d(TAG, "LoginActivity started successfully");
                } catch (Exception e) {
                    DebugLogger.e(TAG, "!!! ERROR starting LoginActivity !!!", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            DebugLogger.d(TAG, "Get Started button listener set");
        }
        
        if (btnShopProducts != null) {
            btnShopProducts.setOnClickListener(v -> {
                DebugLogger.d(TAG, ">>> SHOP PRODUCTS button clicked");
                try {
                    DebugLogger.d(TAG, "Creating intent for ProductCatalogActivity");
                    Intent intent = new Intent(MainActivity.this, ProductCatalogActivity.class);
                    DebugLogger.d(TAG, "Starting ProductCatalogActivity");
                    startActivity(intent);
                    DebugLogger.d(TAG, "ProductCatalogActivity started successfully");
                } catch (Exception e) {
                    DebugLogger.e(TAG, "!!! ERROR starting ProductCatalogActivity !!!", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            DebugLogger.d(TAG, "Shop Products button listener set");
        }
        
        DebugLogger.d(TAG, "initializeViews() completed successfully");
    }
    
    @Override
    public void onLogUpdate() {
        updateLogDisplay();
    }
    
    private void updateLogDisplay() {
        if (tvLogs != null) {
            tvLogs.setText(DebugLogger.getLogsAsString());
            
            // Auto-scroll to bottom
            if (scrollLogs != null) {
                scrollLogs.post(() -> scrollLogs.fullScroll(View.FOCUS_DOWN));
            }
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        DebugLogger.d(TAG, "=== MainActivity onStart ===");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        DebugLogger.d(TAG, "=== MainActivity onResume ===");
        DebugLogger.setListener(this);
        updateLogDisplay();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        DebugLogger.d(TAG, "=== MainActivity onPause ===");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== MainActivity onDestroy ===");
        DebugLogger.setListener(null);
    }
}
