package com.android.pandosdktest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

/**
 * RegisterActivity with real Tuya SDK registration
 * Flow:
 * 1. Enter email
 * 2. Send verification code
 * 3. Enter code + password
 * 4. Register account
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    
    private EditText etEmail, etPassword, etCode;
    private Button btnSendCode, btnRegister;
    private ProgressBar progressBar;
    private TextView tvBackToLogin;
    private boolean codeSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DebugLogger.d(TAG, "=== RegisterActivity onCreate START ===");
        
        try {
            setContentView(R.layout.activity_register);
            
            initViews();
            setupListeners();
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Register for PANDO");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            DebugLogger.d(TAG, "=== RegisterActivity onCreate SUCCESS ===");
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error loading registration", e);
            Toast.makeText(this, "Error loading registration", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etCode = findViewById(R.id.et_code);
        btnSendCode = findViewById(R.id.btn_send_code);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
        
        // Initially hide code and password fields
        if (etCode != null) etCode.setVisibility(View.GONE);
        if (etPassword != null) etPassword.setVisibility(View.GONE);
        if (btnRegister != null) btnRegister.setVisibility(View.GONE);
    }
    
    private void setupListeners() {
        if (btnSendCode != null) {
            btnSendCode.setOnClickListener(v -> handleSendCode());
        }
        
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> handleRegister());
        }
        
        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> finish());
        }
    }
    
    private void handleSendCode() {
        try {
            if (etEmail == null) {
                Toast.makeText(this, "Error: Form not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = etEmail.getText().toString().trim();
            
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            
            DebugLogger.d(TAG, "Sending verification code to: " + email);
            
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (btnSendCode != null) btnSendCode.setEnabled(false);
            
            sendVerificationCode(email);
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error sending code", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resetUI();
        }
    }
    
    private void sendVerificationCode(String email) {
        try {
            // Get ThingHomeSdk.getUserInstance()
            Class<?> thingHomeSdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
            Method getUserInstanceMethod = thingHomeSdkClass.getMethod("getUserInstance");
            Object userInstance = getUserInstanceMethod.invoke(null);
            
            if (userInstance == null) {
                DebugLogger.e(TAG, "Failed to get ThingHomeSdk user instance");
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tuya SDK not initialized", Toast.LENGTH_SHORT).show();
                    resetUI();
                });
                return;
            }
            
            // Create callback
            Class<?> iResultCallbackClass = Class.forName("com.thingclips.smart.sdk.api.IResultCallback");
            
            Object callback = java.lang.reflect.Proxy.newProxyInstance(
                getClassLoader(),
                new Class[]{iResultCallbackClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    DebugLogger.d(TAG, "Send code callback: " + methodName);
                    
                    if ("onSuccess".equals(methodName)) {
                        DebugLogger.d(TAG, "✅ Verification code sent successfully!");
                        
                        runOnUiThread(() -> {
                            codeSent = true;
                            Toast.makeText(RegisterActivity.this, "Verification code sent to " + email, Toast.LENGTH_LONG).show();
                            
                            // Show code and password fields
                            if (etCode != null) etCode.setVisibility(View.VISIBLE);
                            if (etPassword != null) etPassword.setVisibility(View.VISIBLE);
                            if (btnRegister != null) btnRegister.setVisibility(View.VISIBLE);
                            if (btnSendCode != null) btnSendCode.setText("Resend Code");
                            
                            resetUI();
                        });
                        
                    } else if ("onError".equals(methodName)) {
                        String errorCode = args != null && args.length > 0 ? String.valueOf(args[0]) : "UNKNOWN";
                        String errorMsg = args != null && args.length > 1 ? String.valueOf(args[1]) : "Unknown error";
                        
                        DebugLogger.e(TAG, "❌ Failed to send code: " + errorCode + " - " + errorMsg);
                        
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Failed to send code: " + errorMsg, Toast.LENGTH_LONG).show();
                            resetUI();
                        });
                    }
                    
                    return null;
                }
            );
            
            // Call sendVerifyCodeWithUserName(email, region, countryCode, type, callback)
            // type = 1 for registration
            String countryCode = "1"; // Default to USA
            String region = ""; // Empty for default region
            int type = 1; // 1 = registration
            
            Method sendCodeMethod = userInstance.getClass().getMethod(
                "sendVerifyCodeWithUserName",
                String.class, // userName (email)
                String.class, // region
                String.class, // countryCode
                int.class,    // type
                iResultCallbackClass // callback
            );
            
            DebugLogger.d(TAG, "Calling sendVerifyCodeWithUserName");
            sendCodeMethod.invoke(userInstance, email, region, countryCode, type, callback);
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error sending verification code", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                resetUI();
            });
        }
    }
    
    private void handleRegister() {
        try {
            if (etEmail == null || etPassword == null || etCode == null) {
                Toast.makeText(this, "Error: Form not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            DebugLogger.d(TAG, "Registering account for: " + email);
            
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (btnRegister != null) btnRegister.setEnabled(false);
            
            registerAccount(email, password, code);
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error registering", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resetUI();
        }
    }
    
    private void registerAccount(String email, String password, String code) {
        try {
            // Get ThingHomeSdk.getUserInstance()
            Class<?> thingHomeSdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
            Method getUserInstanceMethod = thingHomeSdkClass.getMethod("getUserInstance");
            Object userInstance = getUserInstanceMethod.invoke(null);
            
            if (userInstance == null) {
                DebugLogger.e(TAG, "Failed to get ThingHomeSdk user instance");
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tuya SDK not initialized", Toast.LENGTH_SHORT).show();
                    resetUI();
                });
                return;
            }
            
            // Create callback
            Class<?> iRegisterCallbackClass = Class.forName("com.thingclips.smart.android.user.api.IRegisterCallback");
            
            Object callback = java.lang.reflect.Proxy.newProxyInstance(
                getClassLoader(),
                new Class[]{iRegisterCallbackClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    DebugLogger.d(TAG, "Register callback: " + methodName);
                    
                    if ("onSuccess".equals(methodName)) {
                        Object user = args != null && args.length > 0 ? args[0] : null;
                        DebugLogger.d(TAG, "✅ Registration successful!");
                        
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                            
                            // Go back to login
                            finish();
                        });
                        
                    } else if ("onError".equals(methodName)) {
                        String errorCode = args != null && args.length > 0 ? String.valueOf(args[0]) : "UNKNOWN";
                        String errorMsg = args != null && args.length > 1 ? String.valueOf(args[1]) : "Unknown error";
                        
                        DebugLogger.e(TAG, "❌ Registration failed: " + errorCode + " - " + errorMsg);
                        
                        runOnUiThread(() -> {
                            String message = "Registration failed: " + errorMsg;
                            
                            if (errorMsg.contains("code") || errorMsg.contains("verification")) {
                                message = "Invalid verification code. Please try again.";
                            } else if (errorMsg.contains("exist")) {
                                message = "Email already registered. Please login instead.";
                            }
                            
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                            resetUI();
                        });
                    }
                    
                    return null;
                }
            );
            
            // Call registerAccountWithEmail(countryCode, email, password, code, callback)
            String countryCode = "1"; // Default to USA
            
            Method registerMethod = userInstance.getClass().getMethod(
                "registerAccountWithEmail",
                String.class, // countryCode
                String.class, // email
                String.class, // password
                String.class, // code
                iRegisterCallbackClass // callback
            );
            
            DebugLogger.d(TAG, "Calling registerAccountWithEmail");
            registerMethod.invoke(userInstance, countryCode, email, password, code, callback);
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error registering account", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                resetUI();
            });
        }
    }
    
    private void resetUI() {
        runOnUiThread(() -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (btnSendCode != null) btnSendCode.setEnabled(true);
            if (btnRegister != null) btnRegister.setEnabled(true);
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
