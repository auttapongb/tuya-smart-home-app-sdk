package com.android.pandosdktest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WiFiSelectionActivity extends AppCompatActivity {
    private static final String TAG = "WiFiSelectionActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private ListView lvWiFiNetworks;
    private ProgressBar progressBar;
    private Button btnManualEntry;
    private Button btnRescan;
    private TextView tvCurrentNetwork;
    
    private WiFiScanner wifiScanner;
    private WiFiListAdapter adapter;
    private List<ScanResult> wifiList;
    private String currentSSID;
    private String pairingMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== WiFiSelectionActivity onCreate START ===");
            
            setContentView(R.layout.activity_wifi_selection);
            
            // Get pairing mode from intent
            pairingMode = getIntent().getStringExtra("pairing_mode");
            if (pairingMode == null) {
                pairingMode = "qr_code"; // Default
            }
            
            DebugLogger.d(TAG, "Pairing mode: " + pairingMode);
            
            // Initialize views
            lvWiFiNetworks = findViewById(R.id.lvWiFiNetworks);
            progressBar = findViewById(R.id.progressBar);
            btnManualEntry = findViewById(R.id.btnManualEntry);
            btnRescan = findViewById(R.id.btnRescan);
            tvCurrentNetwork = findViewById(R.id.tvCurrentNetwork);
            
            // Initialize WiFi scanner
            wifiScanner = new WiFiScanner(this);
            wifiList = new ArrayList<>();
            
            // Get current network
            currentSSID = wifiScanner.getCurrentSSID();
            if (currentSSID != null && !currentSSID.isEmpty()) {
                tvCurrentNetwork.setText("Current: " + currentSSID);
                tvCurrentNetwork.setVisibility(View.VISIBLE);
                DebugLogger.d(TAG, "Current network: " + currentSSID);
            }
            
            // Setup list view
            adapter = new WiFiListAdapter();
            lvWiFiNetworks.setAdapter(adapter);
            
            // Check and request location permission
            if (checkLocationPermission()) {
                DebugLogger.d(TAG, "Location permission granted, scanning WiFi");
                scanWiFiNetworks();
            } else {
                DebugLogger.d(TAG, "Requesting location permission");
                requestLocationPermission();
            }
            
            lvWiFiNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ScanResult selectedNetwork = wifiList.get(position);
                    onNetworkSelected(selectedNetwork);
                }
            });
            
            // Setup buttons
            btnManualEntry.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Manual entry clicked");
                openManualEntry();
            });
            
            btnRescan.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Rescan clicked");
                scanWiFiNetworks();
            });
            
            // Start scanning
            scanWiFiNetworks();
            
            DebugLogger.d(TAG, "=== WiFiSelectionActivity onCreate SUCCESS ===");
            
        } catch (Exception e) {
            DebugLogger.e(TAG, "=== WiFiSelectionActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void scanWiFiNetworks() {
        DebugLogger.d(TAG, "Starting WiFi scan...");
        progressBar.setVisibility(View.VISIBLE);
        btnRescan.setEnabled(false);
        
        wifiScanner.scanWiFiNetworks(new WiFiScanner.WiFiScanCallback() {
            @Override
            public void onScanComplete(List<ScanResult> results) {
                runOnUiThread(() -> {
                    DebugLogger.d(TAG, "Scan complete: " + results.size() + " networks found");
                    wifiList.clear();
                    wifiList.addAll(results);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    btnRescan.setEnabled(true);
                    
                    if (wifiList.isEmpty()) {
                        Toast.makeText(WiFiSelectionActivity.this, 
                            "No WiFi networks found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onScanFailed() {
                runOnUiThread(() -> {
                    DebugLogger.d(TAG, "Scan failed");
                    progressBar.setVisibility(View.GONE);
                    btnRescan.setEnabled(true);
                    Toast.makeText(WiFiSelectionActivity.this, 
                        "Failed to scan WiFi networks", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void onNetworkSelected(ScanResult network) {
        DebugLogger.d(TAG, "Network selected: " + network.SSID);
        
        // Return selected network to calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("ssid", network.SSID);
        resultIntent.putExtra("frequency", WiFiScanner.getFrequencyBand(network));
        resultIntent.putExtra("is_secured", WiFiScanner.isSecured(network));
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    private void openManualEntry() {
        // Return empty result to indicate manual entry
        Intent resultIntent = new Intent();
        resultIntent.putExtra("manual_entry", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
    
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DebugLogger.d(TAG, "Requesting ACCESS_FINE_LOCATION permission");
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DebugLogger.d(TAG, "Location permission granted by user");
                Toast.makeText(this, "Permission granted! Scanning WiFi networks...", Toast.LENGTH_SHORT).show();
                scanWiFiNetworks();
            } else {
                DebugLogger.d(TAG, "Location permission denied by user");
                Toast.makeText(this, "Location permission is required to scan WiFi networks", Toast.LENGTH_LONG).show();
                // Show manual entry option
                if (btnManualEntry != null) {
                    btnManualEntry.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiScanner != null) {
            wifiScanner.cleanup();
        }
        DebugLogger.d(TAG, "=== WiFiSelectionActivity onDestroy ===");
    }
    
    // WiFi List Adapter
    private class WiFiListAdapter extends ArrayAdapter<ScanResult> {
        
        public WiFiListAdapter() {
            super(WiFiSelectionActivity.this, R.layout.item_wifi_network, wifiList);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_wifi_network, parent, false);
            }
            
            ScanResult result = wifiList.get(position);
            
            TextView tvSignalIcon = convertView.findViewById(R.id.tvSignalIcon);
            TextView tvSSID = convertView.findViewById(R.id.tvSSID);
            TextView tvDetails = convertView.findViewById(R.id.tvDetails);
            TextView tvCurrentIndicator = convertView.findViewById(R.id.tvCurrentIndicator);
            
            // Set SSID
            tvSSID.setText(result.SSID);
            
            // Set signal strength icon
            int level = WiFiScanner.getSignalLevel(result);
            String signalIcon = getSignalIcon(level);
            tvSignalIcon.setText(signalIcon);
            
            // Set details (security + frequency)
            String security = WiFiScanner.isSecured(result) ? "Secured" : "Open";
            String frequency = WiFiScanner.getFrequencyBand(result);
            String strength = WiFiScanner.getSignalStrength(result);
            tvDetails.setText(security + " • " + frequency + " • " + strength);
            
            // Show current network indicator
            if (currentSSID != null && currentSSID.equals(result.SSID)) {
                tvCurrentIndicator.setVisibility(View.VISIBLE);
            } else {
                tvCurrentIndicator.setVisibility(View.GONE);
            }
            
            return convertView;
        }
        
        private String getSignalIcon(int level) {
            switch (level) {
                case 4: return "📶📶📶📶";
                case 3: return "📶📶📶";
                case 2: return "📶📶";
                case 1: return "📶";
                default: return "📡";
            }
        }
    }
}
