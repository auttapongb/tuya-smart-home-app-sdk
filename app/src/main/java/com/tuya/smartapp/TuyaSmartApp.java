package com.tuya.smartapp;

import android.app.Application;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import timber.log.Timber;

/**
 * Application class for Tuya Smart Home App
 * Initializes Tuya SDK and application-level configurations
 */
public class TuyaSmartApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        
        // Initialize Tuya SDK
        ThingHomeSdk.init(this);
        
        // Enable debug mode for development
        if (BuildConfig.DEBUG) {
            ThingHomeSdk.setDebugMode(true);
            Timber.d("Tuya SDK initialized in debug mode");
        } else {
            ThingHomeSdk.setDebugMode(false);
        }
        
        Timber.d("TuyaSmartApp initialized successfully");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Destroy cloud connection when app terminates
        ThingHomeSdk.onDestroy();
        Timber.d("Tuya SDK destroyed");
    }
}
