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
 * LoginActivity with real Tuya SDK authentication
 * Supports:
 * - Email + Password login
 * - Email + Verification Code login
 * - Registration link
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText etEmail, etPassword;
    private Button btnLogin, btnLoginWithCode, btnSendCode;
    private ProgressBar progressBar;
    private TextView tvRegister, tvSwitchMode;
    private boolean isCodeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        DebugLogger.d(TAG, "=== LoginActivity onCreate START ===");
        
        try {
            DebugLogger.d(TAG, "Setting content view");
            setContentView(R.layout.activity_login);
            DebugLogger.d(TAG, "Content view set successfully");
            
            initViews();
            setupListeners();
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Login to PANDO");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            DebugLogger.d(TAG, "=== LoginActivity onCreate SUCCESS ===");
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== LoginActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error loading login screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvRegister = findViewById(R.id.tv_register);
        
        // Optional: Add these views to layout if needed
        // btnLoginWithCode = findViewById(R.id.btn_login_with_code);
        // btnSendCode = findViewById(R.id.btn_send_code);
        // tvSwitchMode = findViewById(R.id.tv_switch_mode);
    }
    
    private void setupListeners() {
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> handleLogin());
        }
        
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        DebugLogger.d(TAG, "=== LoginActivity onStart ===");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLogger.d(TAG, "=== LoginActivity onDestroy ===");
    }
    
    private void handleLogin() {
        try {
            if (etEmail == null || etPassword == null) {
                Toast.makeText(this, "Error: Form not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
        
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }
        
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            
            DebugLogger.d(TAG, "Attempting Tuya login with email: " + email);
            
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (btnLogin != null) {
                btnLogin.setEnabled(false);
            }
            
            // Login with Tuya SDK
            loginWithTuyaSDK(email, password);
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Login error", e);
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resetUI();
        }
    }
    
    private void loginWithTuyaSDK(String email, String password) {
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
            
            DebugLogger.d(TAG, "✅ Got ThingHomeSdk user instance");
            
            // Create callback using reflection
            Class<?> iLoginCallbackClass = Class.forName("com.thingclips.smart.android.user.api.ILoginCallback");
            
            Object loginCallback = java.lang.reflect.Proxy.newProxyInstance(
                getClassLoader(),
                new Class[]{iLoginCallbackClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    DebugLogger.d(TAG, "Login callback: " + methodName);
                    
                    if ("onSuccess".equals(methodName)) {
                        // Login successful
                        Object user = args != null && args.length > 0 ? args[0] : null;
                        DebugLogger.d(TAG, "✅ Tuya login successful!");
                        
                        // Create home if needed
                        createHomeIfNeeded(email);
                        
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Welcome to PANDO!", Toast.LENGTH_SHORT).show();
                            
                            // Navigate to HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("user_email", email);
                            startActivity(intent);
                            finish();
                        });
                        
                    } else if ("onError".equals(methodName)) {
                        // Login failed
                        String errorCode = args != null && args.length > 0 ? String.valueOf(args[0]) : "UNKNOWN";
                        String errorMsg = args != null && args.length > 1 ? String.valueOf(args[1]) : "Unknown error";
                        
                        DebugLogger.e(TAG, "❌ Tuya login failed: " + errorCode + " - " + errorMsg);
                        
                        runOnUiThread(() -> {
                            String message = "Login failed: " + errorMsg;
                            
                            // Provide helpful error messages
                            if (errorCode.contains("USER_NOT_EXIST") || errorMsg.contains("not exist")) {
                                message = "Account not found. Please register first.";
                            } else if (errorCode.contains("PASSWORD_ERROR") || errorMsg.contains("password")) {
                                message = "Incorrect password. Please try again.";
                            } else if (errorCode.contains("NETWORK")) {
                                message = "Network error. Please check your connection.";
                            }
                            
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            resetUI();
                        });
                    }
                    
                    return null;
                }
            );
            
            // Call loginWithEmail(countryCode, email, password, callback)
            // Default country code: "1" for USA, "66" for Thailand
            String countryCode = "1"; // You can make this configurable
            
            Method loginWithEmailMethod = userInstance.getClass().getMethod(
                "loginWithEmail",
                String.class, // countryCode
                String.class, // email
                String.class, // password
                iLoginCallbackClass // callback
            );
            
            DebugLogger.d(TAG, "Calling Tuya loginWithEmail with country code: " + countryCode);
            loginWithEmailMethod.invoke(userInstance, countryCode, email, password, loginCallback);
            
            DebugLogger.d(TAG, "Login request sent to Tuya cloud");
            
        } catch (ClassNotFoundException e) {
            DebugLogger.e(TAG, "Tuya SDK classes not found", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Tuya SDK not available", Toast.LENGTH_SHORT).show();
                resetUI();
            });
        } catch (NoSuchMethodException e) {
            DebugLogger.e(TAG, "Tuya SDK method not found", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Tuya SDK version mismatch", Toast.LENGTH_SHORT).show();
                resetUI();
            });
        } catch (Exception e) {
            DebugLogger.e(TAG, "Unexpected error during Tuya login", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                resetUI();
            });
        }
    }
    
    private void createHomeIfNeeded(String userEmail) {
        try {
            DebugLogger.d(TAG, "Checking if home exists...");
            
            // Get home manager instance
            Class<?> thingHomeSdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
            Method getHomeManagerMethod = thingHomeSdkClass.getMethod("getHomeManagerInstance");
            Object homeManager = getHomeManagerMethod.invoke(null);
            
            // Query home list
            Class<?> iThingGetHomeListCallbackClass = Class.forName("com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback");
            
            Object callback = java.lang.reflect.Proxy.newProxyInstance(
                getClassLoader(),
                new Class[]{iThingGetHomeListCallbackClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    
                    if ("onSuccess".equals(methodName)) {
                        Object homeList = args != null && args.length > 0 ? args[0] : null;
                        
                        if (homeList != null && homeList instanceof java.util.List) {
                            java.util.List<?> homes = (java.util.List<?>) homeList;
                            
                            if (homes.isEmpty()) {
                                DebugLogger.d(TAG, "No homes found, creating default home...");
                                createDefaultHome();
                            } else {
                                DebugLogger.d(TAG, "✅ User already has " + homes.size() + " home(s)");
                                // Save first home ID
                                try {
                                    Object firstHome = homes.get(0);
                                    Method getHomeIdMethod = firstHome.getClass().getMethod("getHomeId");
                                    Long homeId = (Long) getHomeIdMethod.invoke(firstHome);
                                    
                                    if (homeId != null) {
                                        HomeIdManager manager = new HomeIdManager(LoginActivity.this);
                                        manager.setCurrentHome(homeId, "My Home");
                                        DebugLogger.d(TAG, "✅ Saved homeId: " + homeId);
                                    }
                                } catch (Exception e) {
                                    DebugLogger.e(TAG, "Error saving home ID", e);
                                }
                            }
                        } else {
                            DebugLogger.d(TAG, "No homes found, creating default home...");
                            createDefaultHome();
                        }
                    } else if ("onError".equals(methodName)) {
                        String errorCode = args != null && args.length > 0 ? String.valueOf(args[0]) : "UNKNOWN";
                        String errorMsg = args != null && args.length > 1 ? String.valueOf(args[1]) : "Unknown error";
                        DebugLogger.e(TAG, "❌ Failed to get home list: " + errorCode + " - " + errorMsg);
                        
                        // Create default home anyway
                        createDefaultHome();
                    }
                    
                    return null;
                }
            );
            
            // Call queryHomeList
            Method queryHomeListMethod = homeManager.getClass().getMethod("queryHomeList", iThingGetHomeListCallbackClass);
            queryHomeListMethod.invoke(homeManager, callback);
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error checking home", e);
            // Try to create home anyway
            createDefaultHome();
        }
    }
    
    private void createDefaultHome() {
        try {
            DebugLogger.d(TAG, "Creating default home: My Home");
            
            // Get home manager instance
            Class<?> thingHomeSdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
            Method getHomeManagerMethod = thingHomeSdkClass.getMethod("getHomeManagerInstance");
            Object homeManager = getHomeManagerMethod.invoke(null);
            
            // Create callback
            Class<?> iThingHomeResultCallbackClass = Class.forName("com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback");
            
            Object callback = java.lang.reflect.Proxy.newProxyInstance(
                getClassLoader(),
                new Class[]{iThingHomeResultCallbackClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    
                    if ("onSuccess".equals(methodName)) {
                        Object homeBean = args != null && args.length > 0 ? args[0] : null;
                        DebugLogger.d(TAG, "✅ Home created successfully!");
                        
                        // Save home ID
                        if (homeBean != null) {
                            try {
                                Method getHomeIdMethod = homeBean.getClass().getMethod("getHomeId");
                                Long homeId = (Long) getHomeIdMethod.invoke(homeBean);
                                
                                if (homeId != null) {
                                    HomeIdManager manager = new HomeIdManager(LoginActivity.this);
                                    manager.setCurrentHome(homeId, "My Home");
                                    DebugLogger.d(TAG, "✅ Saved homeId: " + homeId);
                                }
                            } catch (Exception e) {
                                DebugLogger.e(TAG, "Error saving home ID", e);
                            }
                        }
                    } else if ("onError".equals(methodName)) {
                        String errorCode = args != null && args.length > 0 ? String.valueOf(args[0]) : "UNKNOWN";
                        String errorMsg = args != null && args.length > 1 ? String.valueOf(args[1]) : "Unknown error";
                        DebugLogger.e(TAG, "❌ Failed to create home: " + errorCode + " - " + errorMsg);
                    }
                    
                    return null;
                }
            );
            
            // Call createHome(name, lon, lat, geoName, rooms, callback)
            String homeName = "My Home";
            double lon = 0;
            double lat = 0;
            String geoName = "";
            java.util.List<String> rooms = new java.util.ArrayList<>();
            
            Method createHomeMethod = homeManager.getClass().getMethod(
                "createHome",
                String.class,
                double.class,
                double.class,
                String.class,
                java.util.List.class,
                iThingHomeResultCallbackClass
            );
            
            createHomeMethod.invoke(homeManager, homeName, lon, lat, geoName, rooms, callback);
            DebugLogger.d(TAG, "Home creation request sent");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error creating home", e);
        }
    }
    
    private void resetUI() {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (btnLogin != null) {
                btnLogin.setEnabled(true);
            }
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
