package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DeviceListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Devices");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        FloatingActionButton fabAddDevice = findViewById(R.id.fab_add_device);
        fabAddDevice.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddDeviceActivity.class);
            startActivity(intent);
        });
        
        // Demo devices
        CardView device1 = findViewById(R.id.device_card_1);
        CardView device2 = findViewById(R.id.device_card_2);
        CardView device3 = findViewById(R.id.device_card_3);
        
        device1.setOnClickListener(v -> openDeviceControl("Water Fountain", "fountain"));
        device2.setOnClickListener(v -> openDeviceControl("Auto Feeder", "feeder"));
        device3.setOnClickListener(v -> openDeviceControl("Pet Camera", "camera"));
    }
    
    private void openDeviceControl(String name, String type) {
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra("deviceName", name);
        intent.putExtra("deviceType", type);
        startActivity(intent);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
