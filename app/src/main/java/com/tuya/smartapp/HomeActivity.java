package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    
    private TextView tvWelcome, tvNoDevices;
    private Button btnPairCamera, btnAddDevice, btnShopProducts, btnBlog, btnSettings, btnLogout;
    private RecyclerView recyclerDevices;
    
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== HomeActivity onCreate START ===");
            
            setContentView(R.layout.activity_home);
            DebugLogger.d(TAG, "Content view set successfully");
            
            // Get user email from intent
            userEmail = getIntent().getStringExtra("user_email");
            DebugLogger.d(TAG, "User email: " + (userEmail != null ? userEmail : "null"));
            
            initializeViews();
            setupListeners();
            loadDevices();
            
            DebugLogger.d(TAG, "=== HomeActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== HomeActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error loading home screen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        try {
            DebugLogger.d(TAG, "Initializing views...");
            
            tvWelcome = findViewById(R.id.tv_welcome);
            tvNoDevices = findViewById(R.id.tv_no_devices);
            btnPairCamera = findViewById(R.id.btn_pair_camera);
            btnAddDevice = findViewById(R.id.btn_add_device);
            btnShopProducts = findViewById(R.id.btn_shop_products);
            btnBlog = findViewById(R.id.btn_blog);
            btnSettings = findViewById(R.id.btn_settings);
            btnLogout = findViewById(R.id.btn_logout);
            recyclerDevices = findViewById(R.id.recycler_devices);
            
            // Set welcome message
            if (tvWelcome != null && userEmail != null) {
                tvWelcome.setText("Welcome, " + userEmail.split("@")[0] + "!");
            }
            
            // Setup RecyclerView
            if (recyclerDevices != null) {
                recyclerDevices.setLayoutManager(new LinearLayoutManager(this));
            }
            
            DebugLogger.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error initializing views", e);
        }
    }
    
    private void setupListeners() {
        try {
            DebugLogger.d(TAG, "Setting up listeners...");
            
            if (btnPairCamera != null) {
                btnPairCamera.setOnClickListener(v -> {
                    DebugLogger.d(TAG, ">>> PAIR CAMERA button clicked");
                    startCameraPairing();
                });
            }
            
            if (btnAddDevice != null) {
                btnAddDevice.setOnClickListener(v -> {
                    DebugLogger.d(TAG, ">>> ADD DEVICE button clicked");
                    startDevicePairing();
                });
            }
            
            if (btnShopProducts != null) {
                btnShopProducts.setOnClickListener(v -> {
                    DebugLogger.d(TAG, ">>> SHOP PRODUCTS button clicked");
                    openProductCatalog();
                });
            }
            
            if (btnBlog != null) {
                btnBlog.setOnClickListener(v -> {
                    DebugLogger.d(TAG, ">>> BLOG button clicked");
                    openBlog();
                });
            }
            
            if (btnSettings != null) {
                btnSettings.setOnClickListener(v -> {
                    DebugLogger.d(TAG, ">>> SETTINGS button clicked");
                    Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
                });
            }
            
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> {
                    DebugLogger.d(TAG, ">>> LOGOUT button clicked");
                    logout();
                });
            }
            
            DebugLogger.d(TAG, "Listeners setup successfully");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error setting up listeners", e);
        }
    }
    
    private void loadDevices() {
        try {
            DebugLogger.d(TAG, "Loading devices...");
            
            // TODO: Load devices from Tuya SDK
            // For now, show "no devices" message
            if (tvNoDevices != null) {
                tvNoDevices.setVisibility(android.view.View.VISIBLE);
            }
            
            DebugLogger.d(TAG, "No devices found (expected for new users)");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error loading devices", e);
        }
    }
    
    private void startCameraPairing() {
        try {
            DebugLogger.d(TAG, "Starting camera pairing...");
            
            Intent intent = new Intent(this, PairingModeSelectionActivity.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
            
            DebugLogger.d(TAG, "Camera pairing activity started");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error starting camera pairing", e);
            Toast.makeText(this, "Camera pairing coming soon!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void startDevicePairing() {
        try {
            DebugLogger.d(TAG, "Starting device pairing...");
            
            Intent intent = new Intent(this, DevicePairingActivity.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
            
            DebugLogger.d(TAG, "Device pairing activity started");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error starting device pairing", e);
            Toast.makeText(this, "Device pairing coming soon!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openBlog() {
        try {
            DebugLogger.d(TAG, "Opening PANDO Story blog...");
            
            Intent intent = new Intent(this, BlogActivity.class);
            startActivity(intent);
            
            DebugLogger.d(TAG, "Blog opened");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error opening blog", e);
            Toast.makeText(this, "Error opening blog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openProductCatalog() {
        try {
            DebugLogger.d(TAG, "Opening product catalog...");
            
            Intent intent = new Intent(this, ProductCatalogActivity.class);
            startActivity(intent);
            
            DebugLogger.d(TAG, "Product catalog opened");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error opening product catalog", e);
            Toast.makeText(this, "Error opening catalog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void logout() {
        try {
            DebugLogger.d(TAG, "Logging out...");
            
            // Clear any saved session data
            // TODO: Clear Tuya SDK session
            
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            
            DebugLogger.d(TAG, "Logout successful");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error logging out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        DebugLogger.d(TAG, "=== HomeActivity onResume ===");
        // Reload devices when returning to this screen
        loadDevices();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        DebugLogger.d(TAG, "=== HomeActivity onPause ===");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== HomeActivity onDestroy ===");
    }
}
