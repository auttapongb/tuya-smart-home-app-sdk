package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_home);
            
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
            
            TextView tvWelcome = findViewById(R.id.tv_welcome);
            if (tvWelcome != null) {
                tvWelcome.setText("Welcome to PANDO!\nYour smart pet care hub");
            }
            
            FloatingActionButton fabAddDevice = findViewById(R.id.fab_add_device);
            if (fabAddDevice != null) {
                fabAddDevice.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(this, DeviceListActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Device list coming soon!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading home screen", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.home_menu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_logout) {
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
