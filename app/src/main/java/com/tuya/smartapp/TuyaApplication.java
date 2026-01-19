package com.tuya.smartapp;

import android.app.Application;
import android.util.Log;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IThingDataCallback;

/**
 * Application class for PANDO Smart Home App
 * Initializes Tuya Smart Life SDK on app startup
 */
public class TuyaApplication extends Application {
    private static final String TAG = "TuyaApplication";
    
    // Tuya credentials
    private static final String APP_KEY = "sdagrafgqhdpyp9f7uca";
    private static final String APP_SECRET = "ekfv97dpaafjf8pxdt4xt98awtp3cyvf";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "Initializing Tuya SDK...");
        
        try {
            // Initialize Tuya SDK
            ThingHomeSdk.init(this, APP_KEY, APP_SECRET);
            
            // Enable debug mode for development
            ThingHomeSdk.setDebugMode(true);
            
            Log.d(TAG, "Tuya SDK initialized successfully");
            Log.d(TAG, "AppKey: " + APP_KEY);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Tuya SDK", e);
            // Don't crash the app, just log the error
            // The app can still function for product catalog and basic features
        }
    }
}
