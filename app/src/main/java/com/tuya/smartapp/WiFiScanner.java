package com.tuya.smartapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WiFiScanner {
    private static final String TAG = "WiFiScanner";
    
    private Context context;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WiFiScanCallback callback;
    private BroadcastReceiver wifiScanReceiver;
    
    public interface WiFiScanCallback {
        void onScanComplete(List<ScanResult> results);
        void onScanFailed();
    }
    
    public WiFiScanner(Context context) {
        this.context = context.getApplicationContext();
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.wifiList = new ArrayList<>();
    }
    
    /**
     * Get current connected WiFi SSID
     */
    public String getCurrentSSID() {
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            
            // Remove quotes if present
            if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            
            DebugLogger.d(TAG, "Current SSID: " + ssid);
            return ssid;
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error getting current SSID", e);
            return null;
        }
    }
    
    /**
     * Scan for available WiFi networks
     */
    public void scanWiFiNetworks(WiFiScanCallback callback) {
        this.callback = callback;
        
        try {
            // Unregister previous receiver if exists
            if (wifiScanReceiver != null) {
                try {
                    context.unregisterReceiver(wifiScanReceiver);
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            // Create new receiver
            wifiScanReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                    
                    if (success) {
                        DebugLogger.d(TAG, "WiFi scan successful");
                        wifiList = wifiManager.getScanResults();
                        processScanResults();
                    } else {
                        DebugLogger.d(TAG, "WiFi scan failed, using cached results");
                        wifiList = wifiManager.getScanResults();
                        processScanResults();
                    }
                }
            };
            
            // Register receiver
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(wifiScanReceiver, intentFilter);
            
            // Start scan
            boolean success = wifiManager.startScan();
            DebugLogger.d(TAG, "WiFi scan started: " + success);
            
            if (!success) {
                // Scan failed, use cached results
                wifiList = wifiManager.getScanResults();
                processScanResults();
            }
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error scanning WiFi networks", e);
            if (callback != null) {
                callback.onScanFailed();
            }
        }
    }
    
    /**
     * Process and filter scan results
     */
    private void processScanResults() {
        try {
            // Remove duplicates and empty SSIDs
            List<ScanResult> filteredList = new ArrayList<>();
            List<String> seenSSIDs = new ArrayList<>();
            
            for (ScanResult result : wifiList) {
                if (result.SSID != null && !result.SSID.isEmpty() && 
                    !seenSSIDs.contains(result.SSID)) {
                    filteredList.add(result);
                    seenSSIDs.add(result.SSID);
                }
            }
            
            // Sort by signal strength (strongest first)
            Collections.sort(filteredList, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult r1, ScanResult r2) {
                    return Integer.compare(r2.level, r1.level);
                }
            });
            
            DebugLogger.d(TAG, "Found " + filteredList.size() + " WiFi networks");
            
            if (callback != null) {
                callback.onScanComplete(filteredList);
            }
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error processing scan results", e);
            if (callback != null) {
                callback.onScanFailed();
            }
        }
    }
    
    /**
     * Get signal strength level (0-4)
     */
    public static int getSignalLevel(ScanResult result) {
        return WifiManager.calculateSignalLevel(result.level, 5);
    }
    
    /**
     * Check if network is secured
     */
    public static boolean isSecured(ScanResult result) {
        return !result.capabilities.contains("OPEN");
    }
    
    /**
     * Get frequency band (2.4GHz or 5GHz)
     */
    public static String getFrequencyBand(ScanResult result) {
        int frequency = result.frequency;
        if (frequency >= 2400 && frequency < 2500) {
            return "2.4GHz";
        } else if (frequency >= 5000 && frequency < 6000) {
            return "5GHz";
        }
        return "Unknown";
    }
    
    /**
     * Get signal strength description
     */
    public static String getSignalStrength(ScanResult result) {
        int level = getSignalLevel(result);
        switch (level) {
            case 4: return "Excellent";
            case 3: return "Good";
            case 2: return "Fair";
            case 1: return "Weak";
            default: return "Very Weak";
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        try {
            if (wifiScanReceiver != null) {
                context.unregisterReceiver(wifiScanReceiver);
                wifiScanReceiver = null;
            }
        } catch (Exception e) {
            DebugLogger.e(TAG, "Error cleaning up WiFiScanner", e);
        }
    }
}
