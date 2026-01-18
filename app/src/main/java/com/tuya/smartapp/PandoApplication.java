package com.tuya.smartapp;

import android.app.Application;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class PandoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Tuya SDK
        ThingHomeSdk.init(this);
        
        // Enable debug mode for development
        ThingHomeSdk.setDebugMode(true);
    }
}
