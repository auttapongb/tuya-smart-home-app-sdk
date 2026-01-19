package com.tuya.smartapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manager class for handling home ID storage and retrieval
 * Prevents hard-coded homeId bugs
 */
public class HomeIdManager {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_HOME_ID = "current_home_id";
    private static final String KEY_HOME_NAME = "current_home_name";
    private static final long DEFAULT_HOME_ID = 1; // Default for testing
    
    private final Context context;
    
    public HomeIdManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * Get the current home ID
     * Returns DEFAULT_HOME_ID (1) if no home is configured
     */
    public long getCurrentHomeId() {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        );
        long homeId = prefs.getLong(KEY_HOME_ID, -1);
        
        if (homeId == -1) {
            // No home configured, use default for testing
            DebugLogger.d("HomeIdManager", "No home configured, using default homeId = " + DEFAULT_HOME_ID);
            return DEFAULT_HOME_ID;
        }
        
        return homeId;
    }
    
    /**
     * Set the current home ID and name
     */
    public void setCurrentHome(long homeId, String homeName) {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        );
        prefs.edit()
            .putLong(KEY_HOME_ID, homeId)
            .putString(KEY_HOME_NAME, homeName)
            .apply();
        
        DebugLogger.d("HomeIdManager", "Home set: " + homeId + " - " + homeName);
    }
    
    /**
     * Get the current home name
     */
    public String getCurrentHomeName() {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        );
        return prefs.getString(KEY_HOME_NAME, "My Home");
    }
    
    /**
     * Check if a home is configured
     */
    public boolean hasHome() {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        );
        return prefs.getLong(KEY_HOME_ID, -1) != -1;
    }
    
    /**
     * Clear the current home
     */
    public void clearHome() {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        );
        prefs.edit()
            .remove(KEY_HOME_ID)
            .remove(KEY_HOME_NAME)
            .apply();
        
        DebugLogger.d("HomeIdManager", "Home cleared");
    }
}
