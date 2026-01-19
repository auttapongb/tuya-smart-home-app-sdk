package com.tuya.smartapp;

import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DebugLogger.d(TAG, "=== CameraActivity onCreate START ===");
        
        try {
            setContentView(R.layout.activity_camera_placeholder);
            DebugLogger.d(TAG, "Layout set successfully");
            
            String deviceId = getIntent().getStringExtra("device_id");
            String deviceName = getIntent().getStringExtra("device_name");
            
            DebugLogger.d(TAG, "Device ID: " + deviceId + ", Name: " + deviceName);
            
            TextView tvDeviceName = findViewById(R.id.tv_device_name);
            TextView tvStatus = findViewById(R.id.tv_status);
            Button btnViewGuide = findViewById(R.id.btn_view_guide);
            Button btnBack = findViewById(R.id.btn_back);
            
            if (tvDeviceName != null) {
                tvDeviceName.setText(deviceName != null ? deviceName : "PANDO Camera");
            }
            
            if (tvStatus != null) {
                tvStatus.setText("Device ID: " + (deviceId != null ? deviceId : "N/A") + 
                               "\n\nStatus: Ready\n\nCamera SDK: Placeholder Mode");
            }
            
            if (btnViewGuide != null) {
                btnViewGuide.setOnClickListener(v -> showIntegrationGuide());
            }
            
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Camera Control");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            DebugLogger.d(TAG, "=== CameraActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== CameraActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Camera feature coming soon!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void showIntegrationGuide() {
        try {
            new AlertDialog.Builder(this)
                .setTitle("IPC Camera SDK Integration")
                .setMessage("Full camera functionality requires:\n\n" +
                        "• Live video streaming\n" +
                        "• PTZ controls\n" +
                        "• Two-way audio\n" +
                        "• Video recording\n\n" +
                        "Download IPC SDK from:\n" +
                        "https://platform.tuya.com/")
                .setPositiveButton("OK", null)
                .show();
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error showing guide", e);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        DebugLogger.d(TAG, "Navigate up - finishing activity");
        finish();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== CameraActivity onDestroy ===");
    }
}
