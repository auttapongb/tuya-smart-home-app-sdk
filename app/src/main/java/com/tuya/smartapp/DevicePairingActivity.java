package com.tuya.smartapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DevicePairingActivity extends AppCompatActivity {
    private static final String TAG = "DevicePairingActivity";
    
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== DevicePairingActivity onCreate START ===");
            
            setContentView(R.layout.activity_device_pairing);
            DebugLogger.d(TAG, "Content view set successfully");
            
            // Get user email from intent
            userEmail = getIntent().getStringExtra("user_email");
            DebugLogger.d(TAG, "User email: " + (userEmail != null ? userEmail : "null"));
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Pair Device");
            } else {
                setTitle("Pair Device");
            }
            
            Toast.makeText(this, "Device pairing screen loaded!", Toast.LENGTH_SHORT).show();
            
            DebugLogger.d(TAG, "=== DevicePairingActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== DevicePairingActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error loading device pairing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== DevicePairingActivity onDestroy ===");
    }
}
