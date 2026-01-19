package com.tuya.smartapp;

import android.app.Application;
import android.util.Log;

/**
 * Application class for PANDO Smart Home App
 * Handles app-wide initialization
 */
public class TuyaApplication extends Application {
    private static final String TAG = "TuyaApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "PANDO App starting...");
        
        // Initialize Tuya SDK in a separate thread to prevent blocking
        new Thread(() -> {
            try {
                Log.d(TAG, "Initializing Tuya SDK in background...");
                
                // Import Tuya SDK classes
                Class<?> sdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
                
                // Get init method
                java.lang.reflect.Method initMethod = sdkClass.getMethod("init", 
                    android.content.Context.class, String.class, String.class);
                
                // Call init
                initMethod.invoke(null, getApplicationContext(), 
                    "sdagrafgqhdpyp9f7uca", 
                    "ekfv97dpaafjf8pxdt4xt98awtp3cyvf");
                
                // Enable debug mode
                java.lang.reflect.Method debugMethod = sdkClass.getMethod("setDebugMode", boolean.class);
                debugMethod.invoke(null, true);
                
                Log.d(TAG, "Tuya SDK initialized successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Tuya SDK initialization failed (non-critical): " + e.getMessage());
                // App will continue to work without SDK features
            }
        }).start();
        
        Log.d(TAG, "PANDO App started successfully");
    }
}
