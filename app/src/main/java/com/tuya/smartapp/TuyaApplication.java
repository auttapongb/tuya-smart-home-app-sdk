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
        DebugLogger.d(TAG, "###   Version: 3.11.1-WiFiFix      ###");
        DebugLogger.d(TAG, "################################################");
        DebugLogger.d(TAG, "TuyaApplication.onCreate() called");
        
        // Initialize Tuya SDK on main thread (required for proper initialization)
        try {
            DebugLogger.d(TAG, "Initializing Tuya SDK...");
            
            // Import Tuya SDK classes
            Class<?> sdkClass = Class.forName("com.thingclips.smart.home.sdk.ThingHomeSdk");
            DebugLogger.d(TAG, "ThingHomeSdk class loaded");
            
            // Get init method
            java.lang.reflect.Method initMethod = sdkClass.getMethod("init", 
                android.content.Context.class, String.class, String.class);
            DebugLogger.d(TAG, "Init method found");
            
            // Call init with app key and secret
            Object result = initMethod.invoke(null, getApplicationContext(), 
                "sdagrafgqhdpyp9f7uca", 
                "ekfv97dpaafjf8pxdt4xt98awtp3cyvf");
            DebugLogger.d(TAG, "Init method called, result: " + result);
            
            // Enable debug mode
            java.lang.reflect.Method debugMethod = sdkClass.getMethod("setDebugMode", boolean.class);
            debugMethod.invoke(null, true);
            DebugLogger.d(TAG, "Debug mode enabled");
            
            DebugLogger.d(TAG, "✅ Tuya SDK initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            DebugLogger.e(TAG, "❌ Tuya SDK class not found: " + e.getMessage());
            DebugLogger.e(TAG, "Make sure Tuya SDK dependencies are included in build.gradle");
        } catch (NoSuchMethodException e) {
            DebugLogger.e(TAG, "❌ Tuya SDK method not found: " + e.getMessage());
            DebugLogger.e(TAG, "SDK version mismatch?");
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
