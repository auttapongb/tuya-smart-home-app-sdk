package com.tuya.smartapp;

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
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
import java.lang.reflect.Method;

/**
 * Camera Pairing Activity using Tuya SDK's official QR code generation
 * Replaces generic WiFi QR with Tuya token-based QR system
 */
public class TuyaCameraPairingActivity extends AppCompatActivity {
    private static final String TAG = "TuyaCameraPairingActivity";
    private static final int REQUEST_WIFI_SELECTION = 1001;
    private static final int BLACK = 0xFF000000;
    
    private String userEmail;
    private String pairingMode = "qr_code";
    
    // Views
    private TextView tvPairingMode, tvInstructions, tvStatus;
    private EditText etSSID, etPassword;
    private Button btnSelectWiFi, btnStartPairing, btnGenerateQR;
    private Button btnHeardPrompt, btnNoPrompt;
    private ImageView ivQRCode;
    private LinearLayout layoutQRCode, layoutWiFiInput, layoutAudioPrompt;
    private ProgressBar progressBar;
    
    private WiFiScanner wifiScanner;
    private String selectedSSID;
    
    // Tuya SDK objects (using reflection to avoid compile-time dependency)
    private Object tuyaActivator;
    private String pairingToken;
    private long homeId = 0; // Default home ID
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== Tuya Camera Pairing Activity START ===");
            
            setContentView(R.layout.activity_camera_pairing);
            
            // Get extras
            userEmail = getIntent().getStringExtra("user_email");
            pairingMode = getIntent().getStringExtra("pairing_mode");
            if (pairingMode == null) pairingMode = "qr_code";
            
            DebugLogger.d(TAG, "Pairing mode: " + pairingMode);
            
            initializeViews();
            wifiScanner = new WiFiScanner(this);
            
            // Auto-detect WiFi
            String currentSSID = wifiScanner.getCurrentSSID();
            if (currentSSID != null && !currentSSID.isEmpty()) {
                selectedSSID = currentSSID;
                etSSID.setText(currentSSID);
                DebugLogger.d(TAG, "Auto-detected WiFi: " + currentSSID);
            }
            
            setupPairingMode();
            setupListeners();
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Pair Camera (Tuya SDK)");
            }
            
            // Request pairing token from Tuya cloud
            requestPairingToken();
            
            DebugLogger.d(TAG, "=== Tuya Camera Pairing Activity SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== Tuya Camera Pairing Activity FAILED ===", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        tvPairingMode = findViewById(R.id.tvPairingMode);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvStatus = findViewById(R.id.tvStatus);
        etSSID = findViewById(R.id.etSSID);
        etPassword = findViewById(R.id.etPassword);
        btnSelectWiFi = findViewById(R.id.btnSelectWiFi);
        btnStartPairing = findViewById(R.id.btnStartPairing);
        btnGenerateQR = findViewById(R.id.btnScanQR);
        btnHeardPrompt = findViewById(R.id.btnHeardPrompt);
        btnNoPrompt = findViewById(R.id.btnNoPrompt);
        ivQRCode = findViewById(R.id.ivQRCode);
        layoutQRCode = findViewById(R.id.layoutQRCode);
        layoutWiFiInput = findViewById(R.id.layoutWiFiInput);
        layoutAudioPrompt = findViewById(R.id.layoutAudioPrompt);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void setupPairingMode() {
        if (tvPairingMode != null) {
            tvPairingMode.setText("📷 QR Code Pairing (Tuya SDK)");
        }
        
        if (tvInstructions != null) {
            tvInstructions.setText(
                "1. Enter your WiFi credentials\n" +
                "2. Generate QR code with Tuya token\n" +
                "3. Point camera at screen (15-20cm away)\n" +
                "4. Camera will scan and connect automatically"
            );
        }
        
        // Show WiFi input and QR sections
        if (layoutWiFiInput != null) layoutWiFiInput.setVisibility(View.VISIBLE);
        if (layoutQRCode != null) layoutQRCode.setVisibility(View.VISIBLE);
        if (layoutAudioPrompt != null) layoutAudioPrompt.setVisibility(View.VISIBLE);
    }
    
    private void setupListeners() {
        if (btnSelectWiFi != null) {
            btnSelectWiFi.setOnClickListener(v -> openWiFiSelection());
        }
        
        if (btnGenerateQR != null) {
            btnGenerateQR.setOnClickListener(v -> generateTuyaQRCode());
        }
        
        if (btnStartPairing != null) {
            btnStartPairing.setOnClickListener(v -> startTuyaPairing());
        }
        
        if (btnHeardPrompt != null) {
            btnHeardPrompt.setOnClickListener(v -> handleAudioPromptHeard());
        }
        
        if (btnNoPrompt != null) {
            btnNoPrompt.setOnClickListener(v -> handleNoAudioPrompt());
        }
    }
    
    /**
     * Request pairing token from Tuya cloud
     * Token is required for QR code generation
     */
    private void requestPairingToken() {
        DebugLogger.d(TAG, "Requesting pairing token from Tuya cloud...");
        
        new Thread(() -> {
            try {
                // Get ThingHomeSdk class
                Class<?> sdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
                
                // Get activator instance
                Method getActivatorMethod = sdkClass.getMethod("getActivatorInstance");
                Object activatorInstance = getActivatorMethod.invoke(null);
                
                // Create callback handler
                Object tokenCallback = java.lang.reflect.Proxy.newProxyInstance(
                    getClassLoader(),
                    new Class[]{Class.forName("com.thingclips.smart.sdk.api.IThingActivatorGetToken")},
                    (proxy, method, args) -> {
                        String methodName = method.getName();
                        
                        if ("onSuccess".equals(methodName)) {
                            pairingToken = (String) args[0];
                            DebugLogger.d(TAG, "✅ Pairing token received: " + pairingToken.substring(0, 20) + "...");
                            
                            runOnUiThread(() -> {
                                Toast.makeText(this, "✅ Token received! Ready to generate QR code", Toast.LENGTH_SHORT).show();
                                if (tvStatus != null) {
                                    tvStatus.setVisibility(View.VISIBLE);
                                    tvStatus.setText("✅ Connected to Tuya cloud. Token valid for 10 minutes.");
                                }
                            });
                            return null;
                        }
                        
                        if ("onFailure".equals(methodName)) {
                            String errorCode = (String) args[0];
                            String errorMsg = (String) args[1];
                            DebugLogger.e(TAG, "❌ Token request failed: " + errorCode + " - " + errorMsg);
                            
                            runOnUiThread(() -> {
                                Toast.makeText(this, "❌ Failed to get token: " + errorMsg, Toast.LENGTH_LONG).show();
                                if (tvStatus != null) {
                                    tvStatus.setVisibility(View.VISIBLE);
                                    tvStatus.setText("❌ Token request failed. Check internet connection.");
                                }
                            });
                            return null;
                        }
                        
                        return null;
                    }
                );
                
                // Call getActivatorToken(homeId, callback)
                Method getTokenMethod = activatorInstance.getClass().getMethod(
                    "getActivatorToken",
                    long.class,
                    Class.forName("com.thingclips.smart.sdk.api.IThingActivatorGetToken")
                );
                
                getTokenMethod.invoke(activatorInstance, homeId, tokenCallback);
                
                DebugLogger.d(TAG, "Token request sent to Tuya cloud");
                
            } catch (Exception e) {
                DebugLogger.e(TAG, "Error requesting pairing token", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "⚠️ Tuya SDK not available. Using fallback method.", Toast.LENGTH_LONG).show();
                    if (tvStatus != null) {
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("⚠️ Tuya SDK unavailable. QR code may not work with camera.");
                    }
                });
            }
        }).start();
    }
    
    /**
     * Generate QR code using Tuya SDK's method
     * Uses pairing token + WiFi credentials
     */
    private void generateTuyaQRCode() {
        DebugLogger.d(TAG, "Generate Tuya QR Code button clicked");
        
        String ssid = etSSID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (ssid.isEmpty()) {
            Toast.makeText(this, "Please enter WiFi SSID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter WiFi password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (pairingToken == null || pairingToken.isEmpty()) {
            Toast.makeText(this, "⚠️ No pairing token. Requesting from cloud...", Toast.LENGTH_SHORT).show();
            requestPairingToken();
            return;
        }
        
        DebugLogger.d(TAG, "Generating Tuya QR code with token...");
        
        new Thread(() -> {
            try {
                // Create ThingCameraActivatorBuilder
                Class<?> builderClass = Class.forName("com.thingclips.smart.sdk.api.activator.ThingCameraActivatorBuilder");
                Object builder = builderClass.newInstance();
                
                // Set context
                Method setContextMethod = builderClass.getMethod("setContext", android.content.Context.class);
                setContextMethod.invoke(builder, this);
                
                // Set SSID
                Method setSsidMethod = builderClass.getMethod("setSsid", String.class);
                setSsidMethod.invoke(builder, ssid);
                
                // Set password
                Method setPasswordMethod = builderClass.getMethod("setPassword", String.class);
                setPasswordMethod.invoke(builder, password);
                
                // Set token
                Method setTokenMethod = builderClass.getMethod("setToken", String.class);
                setTokenMethod.invoke(builder, pairingToken);
                
                // Set timeout (100 seconds)
                Method setTimeoutMethod = builderClass.getMethod("setTimeOut", long.class);
                setTimeoutMethod.invoke(builder, 100L);
                
                // Create listener callback
                Object listener = java.lang.reflect.Proxy.newProxyInstance(
                    getClassLoader(),
                    new Class[]{Class.forName("com.thingclips.smart.sdk.api.activator.IThingSmartCameraActivatorListener")},
                    (proxy, method, args) -> {
                        String methodName = method.getName();
                        
                        if ("onQRCodeSuccess".equals(methodName)) {
                            String qrcodeUrl = (String) args[0];
                            DebugLogger.d(TAG, "✅ QR code URL generated: " + qrcodeUrl.substring(0, 50) + "...");
                            
                            // Generate QR code bitmap from Tuya URL
                            try {
                                Bitmap qrBitmap = createTuyaQRCodeBitmap(qrcodeUrl, 1000, 1000);
                                
                                runOnUiThread(() -> {
                                    if (qrBitmap != null && ivQRCode != null) {
                                        ivQRCode.setImageBitmap(qrBitmap);
                                        ivQRCode.setVisibility(View.VISIBLE);
                                        Toast.makeText(this, "✅ Tuya QR Code generated!", Toast.LENGTH_SHORT).show();
                                        DebugLogger.d(TAG, "QR code displayed successfully");
                                        
                                        if (tvStatus != null) {
                                            tvStatus.setVisibility(View.VISIBLE);
                                            tvStatus.setText("✅ QR Code ready! Point camera at screen (15-20cm away)");
                                        }
                                    }
                                });
                            } catch (WriterException e) {
                                DebugLogger.e(TAG, "Error creating QR bitmap", e);
                                runOnUiThread(() -> Toast.makeText(this, "Error creating QR code", Toast.LENGTH_SHORT).show());
                            }
                            
                            return null;
                        }
                        
                        if ("onError".equals(methodName)) {
                            String errorCode = (String) args[0];
                            String errorMsg = (String) args[1];
                            DebugLogger.e(TAG, "❌ QR generation error: " + errorCode + " - " + errorMsg);
                            
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                            });
                            return null;
                        }
                        
                        if ("onActiveSuccess".equals(methodName)) {
                            DebugLogger.d(TAG, "✅ Device paired successfully!");
                            
                            runOnUiThread(() -> {
                                Toast.makeText(this, "✅ Camera paired successfully!", Toast.LENGTH_LONG).show();
                                if (tvStatus != null) {
                                    tvStatus.setText("✅ Camera paired and added to your account!");
                                }
                                // Return to home after 2 seconds
                                new android.os.Handler().postDelayed(() -> finish(), 2000);
                            });
                            return null;
                        }
                        
                        return null;
                    }
                );
                
                // Set listener
                Method setListenerMethod = builderClass.getMethod("setListener", 
                    Class.forName("com.thingclips.smart.sdk.api.activator.IThingSmartCameraActivatorListener"));
                setListenerMethod.invoke(builder, listener);
                
                // Get SDK instance
                Class<?> sdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
                Method getActivatorMethod = sdkClass.getMethod("getActivatorInstance");
                Object activatorInstance = getActivatorMethod.invoke(null);
                
                // Create camera activator
                Method newActivatorMethod = activatorInstance.getClass().getMethod(
                    "newCameraDevActivator",
                    builderClass
                );
                tuyaActivator = newActivatorMethod.invoke(activatorInstance, builder);
                
                // Create QR code
                Method createQRMethod = tuyaActivator.getClass().getMethod("createQRCode");
                createQRMethod.invoke(tuyaActivator);
                
                DebugLogger.d(TAG, "Tuya QR code generation initiated");
                
            } catch (Exception e) {
                DebugLogger.e(TAG, "Error generating Tuya QR code", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "⚠️ Tuya SDK error. Try restarting app.", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    /**
     * Create QR code bitmap from Tuya URL using ZXing
     * This is the official Tuya SDK method
     */
    private Bitmap createTuyaQRCodeBitmap(String url, int width, int height) throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        
        int matrixWidth = matrix.getWidth();
        int matrixHeight = matrix.getHeight();
        int[] pixels = new int[matrixWidth * matrixHeight];
        
        for (int y = 0; y < matrixHeight; y++) {
            for (int x = 0; x < matrixWidth; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * matrixWidth + x] = BLACK;
                }
            }
        }
        
        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
        
        return bitmap;
    }
    
    /**
     * Start Tuya pairing process
     */
    private void startTuyaPairing() {
        DebugLogger.d(TAG, "Start Tuya pairing button clicked");
        
        if (tuyaActivator == null) {
            Toast.makeText(this, "Please generate QR code first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // Call start() on activator
            Method startMethod = tuyaActivator.getClass().getMethod("start");
            startMethod.invoke(tuyaActivator);
            
            DebugLogger.d(TAG, "Tuya pairing started - listening for device");
            Toast.makeText(this, "Listening for camera...", Toast.LENGTH_SHORT).show();
            
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (tvStatus != null) {
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText("🔍 Waiting for camera to scan QR code...");
            }
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error starting Tuya pairing", e);
            Toast.makeText(this, "Error starting pairing", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openWiFiSelection() {
        Intent intent = new Intent(this, WiFiSelectionActivity.class);
        startActivityForResult(intent, REQUEST_WIFI_SELECTION);
    }
    
    private void handleAudioPromptHeard() {
        DebugLogger.d(TAG, "Camera audio prompt detected");
        Toast.makeText(this, "Great! Camera is ready. Continue with pairing.", Toast.LENGTH_SHORT).show();
        
        if (layoutAudioPrompt != null) layoutAudioPrompt.setVisibility(View.GONE);
        
        if (tvStatus != null) {
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("✅ Camera detected! Enter WiFi details to continue.");
        }
        
        if (layoutWiFiInput != null) layoutWiFiInput.setVisibility(View.VISIBLE);
    }
    
    private void handleNoAudioPrompt() {
        DebugLogger.d(TAG, "No audio prompt from camera");
        Toast.makeText(this, "Make sure camera is powered on and in pairing mode", Toast.LENGTH_LONG).show();
        
        if (tvStatus != null) {
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("⚠️ No prompt? Check if camera LED is blinking rapidly.");
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_WIFI_SELECTION && resultCode == RESULT_OK && data != null) {
            selectedSSID = data.getStringExtra("ssid");  // Fix: use "ssid" not "selected_ssid"
            if (selectedSSID != null && etSSID != null) {
                etSSID.setText(selectedSSID);
                DebugLogger.d(TAG, "WiFi selected: " + selectedSSID);
                Toast.makeText(this, "✅ WiFi selected: " + selectedSSID, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up Tuya activator
        if (tuyaActivator != null) {
            try {
                Method stopMethod = tuyaActivator.getClass().getMethod("stop");
                stopMethod.invoke(tuyaActivator);
                
                Method destroyMethod = tuyaActivator.getClass().getMethod("onDestory");
                destroyMethod.invoke(tuyaActivator);
                
                DebugLogger.d(TAG, "Tuya activator cleaned up");
            } catch (Exception e) {
                DebugLogger.e(TAG, "Error cleaning up activator", e);
            }
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
