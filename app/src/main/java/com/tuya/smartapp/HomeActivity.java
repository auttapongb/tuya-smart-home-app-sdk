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
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class HomeActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        
        // Check if user is logged in
        if (ThingHomeSdk.getUserInstance().isLogin()) {
            tvWelcome.setText("Welcome to PANDO!\nYour smart pet care hub");
        } else {
            tvWelcome.setText("Please login to continue");
        }
        
        FloatingActionButton fabAddDevice = findViewById(R.id.fab_add_device);
        fabAddDevice.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Logout
            ThingHomeSdk.getUserInstance().logout(new com.thingclips.smart.android.user.api.ILogoutCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }
                
                @Override
                public void onError(String code, String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(HomeActivity.this, "Logout failed: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
