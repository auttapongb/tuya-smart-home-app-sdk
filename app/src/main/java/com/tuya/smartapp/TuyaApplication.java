package com.tuya.smartapp;

import android.app.Application;

/**
 * Application class for PANDO Smart Home App
 * Handles app-wide initialization
 */
public class TuyaApplication extends Application {
    private static final String TAG = "TuyaApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        DebugLogger.d(TAG, "\n\n");
        DebugLogger.d(TAG, "################################################");
        DebugLogger.d(TAG, "###   PANDO APPLICATION STARTING   ###");
        DebugLogger.d(TAG, "###   Version: 3.8-MultiPair       ###");
        DebugLogger.d(TAG, "################################################");
        DebugLogger.d(TAG, "TuyaApplication.onCreate() called");
        
        // Initialize Tuya SDK in a separate thread to prevent blocking
        new Thread(() -> {
            try {
                DebugLogger.d(TAG, "Initializing Tuya SDK in background...");
                
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
                
                DebugLogger.d(TAG, "Tuya SDK initialized successfully");
                
            } catch (Exception e) {
                DebugLogger.e(TAG, "Tuya SDK initialization failed (non-critical): " + e.getMessage());
                // App will continue to work without SDK features
            }
        }).start();
        
        DebugLogger.d(TAG, "TuyaApplication.onCreate() completed");
        DebugLogger.d(TAG, "PANDO App started successfully");
        DebugLogger.d(TAG, "################################################\n");
    }
}
