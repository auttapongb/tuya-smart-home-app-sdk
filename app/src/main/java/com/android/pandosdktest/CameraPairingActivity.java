package com.android.pandosdktest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class CameraPairingActivity extends AppCompatActivity {
    private static final String TAG = "CameraPairingActivity";
    private static final int REQUEST_WIFI_SELECTION = 1001;
    
    private String userEmail;
    private String pairingMode = "qr_code"; // Default
    
    // Views
    private TextView tvPairingMode, tvInstructions;
    private EditText etSSID, etPassword;
    private Button btnSelectWiFi, btnStartPairing, btnScanQR;
    private Button btnHeardPrompt, btnNoPrompt;
    private ImageView ivQRCode;
    private LinearLayout layoutQRCode, layoutWiFiInput, layoutAudioPrompt;
    private ProgressBar progressBar;
    private TextView tvStatus;
    
    private WiFiScanner wifiScanner;
    private String selectedSSID;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== CameraPairingActivity onCreate START ===");
            
            setContentView(R.layout.activity_camera_pairing);
            
            // Get extras from intent
            userEmail = getIntent().getStringExtra("user_email");
            pairingMode = getIntent().getStringExtra("pairing_mode");
            if (pairingMode == null) {
                pairingMode = "qr_code";
            }
            
            DebugLogger.d(TAG, "Pairing mode: " + pairingMode);
            DebugLogger.d(TAG, "User email: " + userEmail);
            
            // Initialize views
            initializeViews();
            
            // Initialize WiFi scanner
            wifiScanner = new WiFiScanner(this);
            
            // Auto-detect current WiFi
            String currentSSID = wifiScanner.getCurrentSSID();
            if (currentSSID != null && !currentSSID.isEmpty()) {
                selectedSSID = currentSSID;
                etSSID.setText(currentSSID);
                DebugLogger.d(TAG, "Auto-detected WiFi: " + currentSSID);
            }
            
            // Setup UI based on pairing mode
            setupPairingMode();
            
            // Setup button listeners
            setupListeners();
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Pair Camera");
            }
            
            DebugLogger.d(TAG, "=== CameraPairingActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== CameraPairingActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        tvPairingMode = findViewById(R.id.tvPairingMode);
        tvInstructions = findViewById(R.id.tvInstructions);
        etSSID = findViewById(R.id.etSSID);
        etPassword = findViewById(R.id.etPassword);
        btnSelectWiFi = findViewById(R.id.btnSelectWiFi);
        btnStartPairing = findViewById(R.id.btnStartPairing);
        btnScanQR = findViewById(R.id.btnScanQR);
        ivQRCode = findViewById(R.id.ivQRCode);
        layoutQRCode = findViewById(R.id.layoutQRCode);
        layoutWiFiInput = findViewById(R.id.layoutWiFiInput);
        layoutAudioPrompt = findViewById(R.id.layoutAudioPrompt);
        btnHeardPrompt = findViewById(R.id.btnHeardPrompt);
        btnNoPrompt = findViewById(R.id.btnNoPrompt);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        
        DebugLogger.d(TAG, "Views initialized");
    }
    
    private void setupPairingMode() {
        String modeTitle = "";
        String instructions = "";
        
        switch (pairingMode) {
            case "qr_code":
                modeTitle = "📷 QR Code Pairing";
                instructions = "1. Power on your camera\n" +
                              "2. Wait for LED to blink (pairing mode)\n" +
                              "3. Enter WiFi details below\n" +
                              "4. Tap 'Generate QR Code'\n" +
                              "5. Point camera at QR code on screen";
                layoutQRCode.setVisibility(View.VISIBLE);
                btnScanQR.setVisibility(View.VISIBLE);
                break;
                
            case "ez_mode":
                modeTitle = "⚡ EZ Mode Pairing";
                instructions = "1. Power on your camera\n" +
                              "2. Wait for LED to blink rapidly\n" +
                              "3. Enter WiFi details below\n" +
                              "4. Tap 'Start Pairing'\n" +
                              "5. Wait for camera to connect (30-60s)";
                layoutQRCode.setVisibility(View.GONE);
                btnScanQR.setVisibility(View.GONE);
                break;
                
            case "ap_mode":
                modeTitle = "📡 AP Mode Pairing";
                instructions = "1. Power on your camera\n" +
                              "2. Wait for LED to blink slowly\n" +
                              "3. Connect phone to camera's WiFi hotspot\n" +
                              "4. Return to app and enter home WiFi details\n" +
                              "5. Tap 'Start Pairing'";
                layoutQRCode.setVisibility(View.GONE);
                btnScanQR.setVisibility(View.GONE);
                break;
        }
        
        tvPairingMode.setText(modeTitle);
        tvInstructions.setText(instructions);
        
        DebugLogger.d(TAG, "Pairing mode setup complete: " + modeTitle);
    }
    
    private void setupListeners() {
        btnSelectWiFi.setOnClickListener(v -> {
            DebugLogger.d(TAG, "Select WiFi button clicked");
            openWiFiSelection();
        });
        
        btnStartPairing.setOnClickListener(v -> {
            DebugLogger.d(TAG, "Start Pairing button clicked");
            startPairing();
        });
        
        btnScanQR.setOnClickListener(v -> {
            DebugLogger.d(TAG, "Scan QR Code button clicked");
            generateQRCode();
        });
        
        btnHeardPrompt.setOnClickListener(v -> {
            DebugLogger.d(TAG, "I Heard a Prompt button clicked");
            handleAudioPromptHeard();
        });
        
        btnNoPrompt.setOnClickListener(v -> {
            DebugLogger.d(TAG, "No Prompts button clicked");
            handleNoAudioPrompt();
        });
    }
    
    private void openWiFiSelection() {
        Intent intent = new Intent(this, WiFiSelectionActivity.class);
        intent.putExtra("pairing_mode", pairingMode);
        startActivityForResult(intent, REQUEST_WIFI_SELECTION);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_WIFI_SELECTION && resultCode == RESULT_OK && data != null) {
            boolean manualEntry = data.getBooleanExtra("manual_entry", false);
            
            if (!manualEntry) {
                String ssid = data.getStringExtra("ssid");
                String frequency = data.getStringExtra("frequency");
                
                if (ssid != null) {
                    selectedSSID = ssid;
                    etSSID.setText(ssid);
                    DebugLogger.d(TAG, "WiFi selected: " + ssid + " (" + frequency + ")");
                    Toast.makeText(this, "Selected: " + ssid + " (" + frequency + ")", 
                        Toast.LENGTH_SHORT).show();
                }
            } else {
                DebugLogger.d(TAG, "Manual entry selected");
                etSSID.requestFocus();
            }
        }
    }
    
    private void generateQRCode() {
        String ssid = etSSID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (ssid.isEmpty()) {
            Toast.makeText(this, "Please enter WiFi name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter WiFi password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        DebugLogger.d(TAG, "Generating QR code for SSID: " + ssid);
        
        try {
            // Show progress
            progressBar.setVisibility(View.VISIBLE);
            tvStatus.setText("Generating QR code...");
            tvStatus.setVisibility(View.VISIBLE);
            
            // Create QR code data in WiFi format
            String qrData = "WIFI:T:WPA;S:" + ssid + ";P:" + password + ";;";
            
            // Generate QR code bitmap using ZXing (larger for better scanning)
            Bitmap qrBitmap = generateQRCodeBitmap(qrData, 1000, 1000);
            
            if (qrBitmap != null) {
                ivQRCode.setImageBitmap(qrBitmap);
                ivQRCode.setVisibility(View.VISIBLE);
            } else {
                throw new Exception("Failed to generate QR code bitmap");
            }
            
            // Hide progress
            progressBar.setVisibility(View.GONE);
            tvStatus.setText("✅ QR Code ready! Point camera at screen");
            
            DebugLogger.d(TAG, "QR code generated successfully");
            Toast.makeText(this, "QR Code generated! Point camera at screen", 
                Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error generating QR code", e);
            progressBar.setVisibility(View.GONE);
            tvStatus.setText("❌ Error generating QR code");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void startPairing() {
        String ssid = etSSID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (ssid.isEmpty()) {
            Toast.makeText(this, "Please select or enter WiFi name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter WiFi password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        DebugLogger.d(TAG, "Starting pairing...");
        DebugLogger.d(TAG, "Mode: " + pairingMode);
        DebugLogger.d(TAG, "SSID: " + ssid);
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        btnStartPairing.setEnabled(false);
        
        String statusMessage = "";
        switch (pairingMode) {
            case "qr_code":
                statusMessage = "Waiting for camera to scan QR code...";
                break;
            case "ez_mode":
                statusMessage = "Broadcasting WiFi credentials...";
                break;
            case "ap_mode":
                statusMessage = "Connecting to camera...";
                break;
        }
        
        tvStatus.setText(statusMessage);
        
        // Simulate pairing process (in real implementation, use Tuya SDK)
        Toast.makeText(this, "Pairing started! This requires Tuya SDK integration.", 
            Toast.LENGTH_LONG).show();
        
        DebugLogger.d(TAG, "Pairing process initiated");
        
        // Note: Real implementation would use:
        // ThingHomeSdk.getActivatorInstance().getActivatorToken()
        // ThingCameraActivatorBuilder for QR code
        // ThingActivator for EZ/AP modes
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    private void handleAudioPromptHeard() {
        DebugLogger.d(TAG, "Camera audio prompt detected");
        Toast.makeText(this, "Great! Camera is ready. Continue with pairing.", Toast.LENGTH_SHORT).show();
        
        // Hide audio prompt section
        layoutAudioPrompt.setVisibility(View.GONE);
        
        // Show success status
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("✅ Camera detected! Enter WiFi details to continue.");
        
        // Enable WiFi input
        layoutWiFiInput.setVisibility(View.VISIBLE);
    }
    
    private void handleNoAudioPrompt() {
        DebugLogger.d(TAG, "No audio prompt from camera");
        Toast.makeText(this, "Make sure camera is powered on and in pairing mode", Toast.LENGTH_LONG).show();
        
        // Show troubleshooting message
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("⚠️ No prompt? Check if camera LED is blinking rapidly.");
    }
    
    /**
     * Generate QR code bitmap from text data using ZXing library
     * @param data The text data to encode in QR code
     * @param width Width of the QR code image in pixels
     * @param height Height of the QR code image in pixels
     * @return Bitmap of the QR code, or null if generation fails
     */
    private Bitmap generateQRCodeBitmap(String data, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Set pixel to black if bit is set, white otherwise
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            
            DebugLogger.d(TAG, "QR code bitmap generated: " + width + "x" + height);
            return bitmap;
            
        } catch (WriterException e) {
            DebugLogger.e(TAG, "Error generating QR code bitmap", e);
            return null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiScanner != null) {
            wifiScanner.cleanup();
        }
        DebugLogger.d(TAG, "=== CameraPairingActivity onDestroy ===");
    }
}
