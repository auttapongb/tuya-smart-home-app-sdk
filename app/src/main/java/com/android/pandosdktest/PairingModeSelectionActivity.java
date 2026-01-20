package com.android.pandosdktest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PairingModeSelectionActivity extends AppCompatActivity {
    private static final String TAG = "PairingModeSelection";
    
    private LinearLayout btnQRCode, btnEZMode, btnAPMode, btnBluetooth;
    private String userEmail;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== PairingModeSelectionActivity onCreate START ===");
            
            setContentView(R.layout.activity_pairing_mode_selection);
            
            // Get user email from intent
            userEmail = getIntent().getStringExtra("user_email");
            
            // Initialize views
            btnQRCode = findViewById(R.id.btnQRCode);
            btnEZMode = findViewById(R.id.btnEZMode);
            btnAPMode = findViewById(R.id.btnAPMode);
            btnBluetooth = findViewById(R.id.btnBluetooth);
            
            // Set click listeners
            btnQRCode.setOnClickListener(v -> {
                DebugLogger.d(TAG, "QR Code mode selected");
                startPairing("qr_code");
            });
            
            btnEZMode.setOnClickListener(v -> {
                DebugLogger.d(TAG, "EZ Mode selected");
                startPairing("ez_mode");
            });
            
            btnAPMode.setOnClickListener(v -> {
                DebugLogger.d(TAG, "AP Mode selected");
                startPairing("ap_mode");
            });
            
            btnBluetooth.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Bluetooth mode selected");
                Toast.makeText(this, "Bluetooth pairing coming soon!", Toast.LENGTH_SHORT).show();
            });
            
            DebugLogger.d(TAG, "=== PairingModeSelectionActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== PairingModeSelectionActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void startPairing(String mode) {
        DebugLogger.d(TAG, "Starting pairing with mode: " + mode);
        
        Intent intent = new Intent(this, TuyaCameraPairingActivity.class);
        intent.putExtra("pairing_mode", mode);
        intent.putExtra("user_email", userEmail);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== PairingModeSelectionActivity onDestroy ===");
    }
}
