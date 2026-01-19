package com.tuya.smartapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DeviceDiscoveryActivity extends AppCompatActivity {
    private static final String TAG = "DeviceDiscoveryActivity";
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2001;
    
    private String userEmail;
    private ImageView ivRadar;
    private TextView tvScanningStatus;
    private LinearLayout layoutBluetoothPrompt;
    private Button btnContinue, btnAddManually;
    private Handler handler;
    private boolean isScanning = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== DeviceDiscoveryActivity onCreate START ===");
            
            setContentView(R.layout.activity_device_discovery);
            
            // Get user email from intent
            userEmail = getIntent().getStringExtra("user_email");
            
            // Initialize views
            ivRadar = findViewById(R.id.ivRadar);
            tvScanningStatus = findViewById(R.id.tvScanningStatus);
            layoutBluetoothPrompt = findViewById(R.id.layoutBluetoothPrompt);
            btnContinue = findViewById(R.id.btnContinue);
            btnAddManually = findViewById(R.id.btnAddManually);
            
            handler = new Handler();
            
            // Setup action bar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Add Device");
            }
            
            // Setup button listeners
            btnContinue.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Continue button clicked - enabling Bluetooth");
                layoutBluetoothPrompt.setVisibility(View.GONE);
                checkBluetoothPermission();
            });
            
            btnAddManually.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Add Manually button clicked");
                goToPairingModeSelection();
            });
            
            // Check Bluetooth permission on start
            checkBluetoothPermission();
            
            DebugLogger.d(TAG, "=== DeviceDiscoveryActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== DeviceDiscoveryActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void checkBluetoothPermission() {
        // Check if Bluetooth permission is granted
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12+ requires BLUETOOTH_SCAN and BLUETOOTH_CONNECT
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) 
                    != PackageManager.PERMISSION_GRANTED) {
                DebugLogger.d(TAG, "Bluetooth permission not granted, showing prompt");
                showBluetoothPrompt();
                return;
            }
        } else {
            // Android 11 and below use BLUETOOTH and BLUETOOTH_ADMIN
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) 
                    != PackageManager.PERMISSION_GRANTED) {
                DebugLogger.d(TAG, "Bluetooth permission not granted, showing prompt");
                showBluetoothPrompt();
                return;
            }
        }
        
        // Permission already granted, start scanning
        DebugLogger.d(TAG, "Bluetooth permission already granted");
        startDeviceScanning();
    }
    
    private void showBluetoothPrompt() {
        layoutBluetoothPrompt.setVisibility(View.VISIBLE);
        tvScanningStatus.setText("Bluetooth permission required");
    }
    
    private void requestBluetoothPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                },
                REQUEST_BLUETOOTH_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                },
                REQUEST_BLUETOOTH_PERMISSION);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DebugLogger.d(TAG, "Bluetooth permission granted!");
                Toast.makeText(this, "Permission granted! Scanning for devices...", Toast.LENGTH_SHORT).show();
                startDeviceScanning();
            } else {
                DebugLogger.d(TAG, "Bluetooth permission denied");
                Toast.makeText(this, "Permission denied. Using manual pairing only.", Toast.LENGTH_SHORT).show();
                tvScanningStatus.setText("Bluetooth disabled. Tap 'Add Manually' below.");
            }
        }
    }
    
    private void startDeviceScanning() {
        if (isScanning) return;
        
        isScanning = true;
        tvScanningStatus.setText("Searching for nearby devices. Make sure your device has entered pairing mode");
        
        // Start radar animation
        startRadarAnimation();
        
        // Simulate device scanning (in real implementation, use Tuya SDK)
        handler.postDelayed(() -> {
            // After 5 seconds, show "no devices found" message
            tvScanningStatus.setText("No devices found nearby. Try manual pairing.");
            stopRadarAnimation();
            isScanning = false;
        }, 5000);
        
        DebugLogger.d(TAG, "Device scanning started");
    }
    
    private void startRadarAnimation() {
        RotateAnimation rotate = new RotateAnimation(
            0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        ivRadar.startAnimation(rotate);
    }
    
    private void stopRadarAnimation() {
        ivRadar.clearAnimation();
    }
    
    private void goToPairingModeSelection() {
        Intent intent = new Intent(this, PairingModeSelectionActivity.class);
        intent.putExtra("user_email", userEmail);
        startActivity(intent);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        stopRadarAnimation();
        DebugLogger.d(TAG, "=== DeviceDiscoveryActivity onDestroy ===");
    }
}
