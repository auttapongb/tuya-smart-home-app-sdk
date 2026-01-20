package com.android.pandosdktest;

/**
 * Constants for Intent extras to ensure consistency across activities
 * Prevents key mismatch bugs
 */
public class IntentKeys {
    // User data
    public static final String USER_EMAIL = "user_email";
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    
    // Pairing data
    public static final String PAIRING_MODE = "pairing_mode";
    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_NAME = "device_name";
    
    // WiFi data
    public static final String WIFI_SSID = "ssid";
    public static final String WIFI_PASSWORD = "password";
    public static final String WIFI_SECURITY = "security";
    
    // Home data
    public static final String HOME_ID = "home_id";
    public static final String HOME_NAME = "home_name";
    
    // Product data
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_PRICE = "product_price";
    
    // Request codes
    public static final int REQUEST_WIFI_SELECTION = 1001;
    public static final int REQUEST_PERMISSIONS = 1002;
    public static final int REQUEST_BLUETOOTH = 1003;
    public static final int REQUEST_LOCATION = 1004;
    public static final int REQUEST_CAMERA = 1005;
    
    // Pairing modes
    public static final String MODE_QR_CODE = "qr_code";
    public static final String MODE_EZ = "ez_mode";
    public static final String MODE_AP = "ap_mode";
    public static final String MODE_BLUETOOTH = "bluetooth";
    
    private IntentKeys() {
        // Prevent instantiation
    }
}
