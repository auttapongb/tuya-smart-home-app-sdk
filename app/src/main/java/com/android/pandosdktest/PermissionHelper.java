package com.android.pandosdktest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Helper class for handling runtime permissions
 * Handles Android version differences (especially Android 12+)
 */
public class PermissionHelper {
    private final Activity activity;
    
    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }
    
    /**
     * Check if WiFi scanning permissions are granted
     * Android 12+ requires NEARBY_WIFI_DEVICES
     * Android 11 and below require ACCESS_FINE_LOCATION
     */
    public boolean hasWiFiPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+
            return activity.checkSelfPermission(
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 11 and below
            return activity.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * Request WiFi scanning permissions
     */
    public void requestWiFiPermissions(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.requestPermissions(
                new String[]{Manifest.permission.NEARBY_WIFI_DEVICES},
                requestCode
            );
        } else {
            activity.requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode
            );
        }
    }
    
    /**
     * Check if Bluetooth permissions are granted
     * Android 12+ requires BLUETOOTH_SCAN and BLUETOOTH_CONNECT
     */
    public boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return activity.checkSelfPermission(
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
            activity.checkSelfPermission(
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No special permissions needed before Android 12
    }
    
    /**
     * Request Bluetooth permissions
     */
    public void requestBluetoothPermissions(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.requestPermissions(
                new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                },
                requestCode
            );
        }
    }
    
    /**
     * Check if location permissions are granted
     */
    public boolean hasLocationPermissions() {
        return activity.checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request location permissions
     */
    public void requestLocationPermissions(int requestCode) {
        activity.requestPermissions(
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            requestCode
        );
    }
    
    /**
     * Check if camera permission is granted
     */
    public boolean hasCameraPermission() {
        return activity.checkSelfPermission(
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request camera permission
     */
    public void requestCameraPermission(int requestCode) {
        activity.requestPermissions(
            new String[]{Manifest.permission.CAMERA},
            requestCode
        );
    }
}
