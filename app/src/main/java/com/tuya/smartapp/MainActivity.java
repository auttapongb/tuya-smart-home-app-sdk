package com.tuya.smartapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.bean.HomeBean;

import java.util.List;

/**
 * Main Activity for Tuya Smart Home Application
 * 
 * This activity demonstrates basic Tuya SDK initialization and usage,
 * including user login, home management, and device listing.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TuyaSmartApp";
    
    private TextView statusTextView;
    private Button loginButton;
    private Button getHomeButton;
    private Button getDevicesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        statusTextView = findViewById(R.id.status_text);
        loginButton = findViewById(R.id.btn_login);
        getHomeButton = findViewById(R.id.btn_get_home);
        getDevicesButton = findViewById(R.id.btn_get_devices);
        
        // Initialize Tuya SDK
        initTuyaSdk();
        
        // Set up button listeners
        setupButtonListeners();
    }

    /**
     * Initialize Tuya Smart Home SDK
     */
    private void initTuyaSdk() {
        try {
            // Initialize Tuya SDK
            TuyaHomeSdk.init(this);
            
            // Enable debug logging
            L.setDebugEnable(true);
            
            updateStatus("Tuya SDK initialized successfully");
            Log.d(TAG, "Tuya SDK initialized");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Tuya SDK", e);
            updateStatus("Failed to initialize SDK: " + e.getMessage());
        }
    }

    /**
     * Set up button click listeners
     */
    private void setupButtonListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        getHomeButton.setOnClickListener(v -> handleGetHome());
        getDevicesButton.setOnClickListener(v -> handleGetDevices());
    }

    /**
     * Handle user login
     * Note: This is a placeholder. Implement actual login logic based on your requirements.
     */
    private void handleLogin() {
        try {
            // Check if user is already logged in
            if (TuyaHomeSdk.getCurrentUser() != null) {
                updateStatus("User already logged in: " + TuyaHomeSdk.getCurrentUser().getNickname());
                Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            } else {
                updateStatus("Please implement login UI");
                Toast.makeText(this, "Login UI not implemented", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Login error", e);
            updateStatus("Login error: " + e.getMessage());
        }
    }

    /**
     * Handle getting home information
     */
    private void handleGetHome() {
        try {
            // Get all homes for current user
            List<HomeBean> homes = TuyaHomeSdk.getHomeManagerInstance().getHomes();
            
            if (homes != null && !homes.isEmpty()) {
                HomeBean home = homes.get(0);
                updateStatus("Home: " + home.getName() + " (ID: " + home.getHomeId() + ")");
                Toast.makeText(this, "Home found: " + home.getName(), Toast.LENGTH_SHORT).show();
            } else {
                updateStatus("No homes found");
                Toast.makeText(this, "No homes available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting homes", e);
            updateStatus("Error: " + e.getMessage());
        }
    }

    /**
     * Handle getting devices
     */
    private void handleGetDevices() {
        try {
            // Get all homes first
            List<HomeBean> homes = TuyaHomeSdk.getHomeManagerInstance().getHomes();
            
            if (homes != null && !homes.isEmpty()) {
                HomeBean home = homes.get(0);
                
                // Get devices in the home
                List<DeviceBean> devices = TuyaHomeSdk.getHomeManagerInstance()
                        .getHomeDetail(home.getHomeId())
                        .getDeviceList();
                
                if (devices != null && !devices.isEmpty()) {
                    StringBuilder deviceInfo = new StringBuilder("Devices found: " + devices.size() + "\n");
                    for (DeviceBean device : devices) {
                        deviceInfo.append("- ").append(device.getName())
                                .append(" (").append(device.getProductId()).append(")\n");
                    }
                    updateStatus(deviceInfo.toString());
                    Toast.makeText(this, "Found " + devices.size() + " devices", Toast.LENGTH_SHORT).show();
                } else {
                    updateStatus("No devices found in home");
                    Toast.makeText(this, "No devices available", Toast.LENGTH_SHORT).show();
                }
            } else {
                updateStatus("No homes found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting devices", e);
            updateStatus("Error: " + e.getMessage());
        }
    }

    /**
     * Update status text view
     */
    private void updateStatus(String message) {
        statusTextView.setText(message);
        Log.d(TAG, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up Tuya SDK resources
        TuyaHomeSdk.onDestroy();
    }
}
