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
        DebugLogger.d(TAG, "###   Version: 3.13.0-SDK5      ###");
        DebugLogger.d(TAG, "################################################");
        DebugLogger.d(TAG, "TuyaApplication.onCreate() called");
        
        // Initialize Tuya SDK (AppKey and AppSecret are in AndroidManifest.xml)
        // Using reflection to avoid crash if SDK not available
        try {
            DebugLogger.d(TAG, "Initializing Tuya SDK...");
            
            // Try to load SDK class
            Class<?> sdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
            DebugLogger.d(TAG, "✅ ThingHomeSdk class found");
            
            // Get init method
            java.lang.reflect.Method initMethod = sdkClass.getMethod("init", Application.class);
            initMethod.invoke(null, this);
            DebugLogger.d(TAG, "✅ ThingHomeSdk.init() called successfully!");
            
            // Enable debug mode
            java.lang.reflect.Method debugMethod = sdkClass.getMethod("setDebugMode", boolean.class);
            debugMethod.invoke(null, true);
            DebugLogger.d(TAG, "✅ Debug mode enabled");
            
            DebugLogger.d(TAG, "✅ Tuya SDK initialized successfully!");
            DebugLogger.d(TAG, "AppKey and AppSecret loaded from AndroidManifest.xml");
            
        } catch (ClassNotFoundException e) {
            DebugLogger.e(TAG, "❌ Tuya SDK class not found: " + e.getMessage());
            DebugLogger.e(TAG, "SDK may not be included in build. App will continue without SDK.");
        } catch (NoSuchMethodException e) {
            DebugLogger.e(TAG, "❌ Tuya SDK method not found: " + e.getMessage());
            DebugLogger.e(TAG, "SDK version mismatch. App will continue without SDK.");
        } catch (Exception e) {
            DebugLogger.e(TAG, "❌ Tuya SDK initialization failed: " + e.getClass().getName());
            DebugLogger.e(TAG, "Error message: " + e.getMessage());
            if (e.getCause() != null) {
                DebugLogger.e(TAG, "Cause: " + e.getCause().getMessage());
            }
            DebugLogger.e(TAG, "App will continue without SDK.");
            e.printStackTrace();
        }
        
        DebugLogger.d(TAG, "TuyaApplication.onCreate() completed");
        DebugLogger.d(TAG, "PANDO App started successfully");
        DebugLogger.d(TAG, "################################################\n");
    }
}
