package com.android.pandosdktest;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class WiFiPairingActivity extends AppCompatActivity {
    private TextInputEditText etSsid, etPassword;
    private Button btnStartPairing;
    private ProgressBar progressBar;
    private TextView tvStatus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_pairing);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Wi-Fi Pairing");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        etSsid = findViewById(R.id.et_ssid);
        etPassword = findViewById(R.id.et_password);
        btnStartPairing = findViewById(R.id.btn_start_pairing);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);
        
        etSsid.setText("MyWiFi-2.4GHz");
        
        btnStartPairing.setOnClickListener(v -> startPairing());
    }
    
    private void startPairing() {
        String ssid = etSsid.getText().toString();
        String password = etPassword.getText().toString();
        
        if (ssid.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Wi-Fi credentials", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnStartPairing.setEnabled(false);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvStatus.setText("Connecting to device...");
        
        new Handler().postDelayed(() -> {
            tvStatus.setText("Device paired successfully!");
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Device added! (Demo)", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(() -> finish(), 1500);
        }, 3000);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
