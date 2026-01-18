package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        TextView tvFeatures = findViewById(R.id.tv_features);
        Button btnGetStarted = findViewById(R.id.btn_get_started);
        
        tvAppName.setText("PANDO");
        tvWelcome.setText("Welcome to PANDO Smart Home");
        tvFeatures.setText("Control your smart pet care devices\n\n" +
                "✓ Water Fountains\n" +
                "✓ Automatic Feeders\n" +
                "✓ Pet Cameras\n" +
                "✓ Smart Litter Boxes\n\n" +
                "Phase 1: Authentication UI ✓\n" +
                "Phase 2: Device Pairing (In Progress)\n" +
                "Phase 3: Device Control (In Progress)");
        
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
