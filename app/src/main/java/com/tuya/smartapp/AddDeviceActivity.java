package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AddDeviceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Device");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        CardView cardWifi = findViewById(R.id.card_wifi);
        CardView cardBluetooth = findViewById(R.id.card_bluetooth);
        CardView cardQr = findViewById(R.id.card_qr);
        
        cardWifi.setOnClickListener(v -> {
            Intent intent = new Intent(this, WiFiPairingActivity.class);
            startActivity(intent);
        });
        
        cardBluetooth.setOnClickListener(v -> 
            Toast.makeText(this, "Bluetooth pairing - Coming in full version", Toast.LENGTH_SHORT).show()
        );
        
        cardQr.setOnClickListener(v -> 
            Toast.makeText(this, "QR code pairing - Coming in full version", Toast.LENGTH_SHORT).show()
        );
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
