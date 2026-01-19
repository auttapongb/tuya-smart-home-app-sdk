package com.tuya.smartapp;

import android.app.Application;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

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
        DebugLogger.d(TAG, "###   Version: 3.12.2-LogShare    ###");
        DebugLogger.d(TAG, "################################################");
        DebugLogger.d(TAG, "TuyaApplication.onCreate() called");
        
        // Initialize Tuya SDK (AppKey and AppSecret are in AndroidManifest.xml)
        try {
            DebugLogger.d(TAG, "Initializing Tuya SDK...");
            
            // Direct SDK initialization (no reflection needed!)
            ThingHomeSdk.init(this);
            DebugLogger.d(TAG, "✅ ThingHomeSdk.init() called successfully!");
            
            // Enable debug mode
            ThingHomeSdk.setDebugMode(true);
            DebugLogger.d(TAG, "✅ Debug mode enabled");
            
            DebugLogger.d(TAG, "✅ Tuya SDK initialized successfully!");
            DebugLogger.d(TAG, "AppKey and AppSecret loaded from AndroidManifest.xml");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "❌ Tuya SDK initialization failed: " + e.getClass().getName());
            DebugLogger.e(TAG, "Error message: " + e.getMessage());
            if (e.getCause() != null) {
                DebugLogger.e(TAG, "Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        }
        
        DebugLogger.d(TAG, "TuyaApplication.onCreate() completed");
        DebugLogger.d(TAG, "PANDO App started successfully");
        DebugLogger.d(TAG, "################################################\n");
    }
}
