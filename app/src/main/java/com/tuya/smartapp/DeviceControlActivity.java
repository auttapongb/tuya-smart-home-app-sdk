package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class DeviceControlActivity extends AppCompatActivity {
    private String deviceName;
    private String deviceType;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        deviceName = getIntent().getStringExtra("deviceName");
        deviceType = getIntent().getStringExtra("deviceType");
        
        if ("fountain".equals(deviceType)) {
            setContentView(R.layout.activity_device_control_fountain);
            setupFountainControls();
        } else if ("feeder".equals(deviceType)) {
            setContentView(R.layout.activity_device_control_feeder);
            setupFeederControls();
        } else if ("camera".equals(deviceType)) {
            setContentView(R.layout.activity_device_control_fountain);
            setupCameraControls();
            Toast.makeText(this, "Camera SDK integration pending", Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.activity_device_control_fountain);
            setupFountainControls();
        }
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(deviceName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void setupFountainControls() {
        Switch switchPower = findViewById(R.id.switch_power);
        MaterialButtonToggleGroup toggleMode = findViewById(R.id.toggle_mode);
        SeekBar seekBarFlow = findViewById(R.id.seekbar_flow);
        TextView tvFlowLevel = findViewById(R.id.tv_flow_level);
        
        switchPower.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Power: " + (isChecked ? "ON" : "OFF") + " (Demo)", Toast.LENGTH_SHORT).show();
        });
        
        toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String mode = "Normal";
                if (checkedId == R.id.btn_mode_smart) mode = "Smart";
                else if (checkedId == R.id.btn_mode_sleep) mode = "Sleep";
                Toast.makeText(this, "Mode: " + mode + " (Demo)", Toast.LENGTH_SHORT).show();
            }
        });
        
        seekBarFlow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFlowLevel.setText("Flow Level: " + progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(DeviceControlActivity.this, "Flow set to " + seekBar.getProgress() + " (Demo)", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupFeederControls() {
        Switch switchPower = findViewById(R.id.switch_power);
        Button btnManualFeed = findViewById(R.id.btn_manual_feed);
        SeekBar seekBarPortion = findViewById(R.id.seekbar_portion);
        TextView tvPortionSize = findViewById(R.id.tv_portion_size);
        
        switchPower.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Power: " + (isChecked ? "ON" : "OFF") + " (Demo)", Toast.LENGTH_SHORT).show();
        });
        
        btnManualFeed.setOnClickListener(v -> {
            Toast.makeText(this, "Feeding now... (Demo)", Toast.LENGTH_SHORT).show();
        });
        
        seekBarPortion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPortionSize.setText(progress + " grams");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(DeviceControlActivity.this, "Portion set to " + seekBar.getProgress() + "g (Demo)", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupCameraControls() {
        Switch switchPower = findViewById(R.id.switch_power);
        Switch switchMotion = findViewById(R.id.switch_motion);
        Switch switchNightVision = findViewById(R.id.switch_night_vision);
        Button btnSnapshot = findViewById(R.id.btn_snapshot);
        
        switchPower.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Power: " + (isChecked ? "ON" : "OFF") + " (Demo)", Toast.LENGTH_SHORT).show();
        });
        
        switchMotion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Motion Detection: " + (isChecked ? "ON" : "OFF") + " (Demo)", Toast.LENGTH_SHORT).show();
        });
        
        switchNightVision.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Night Vision: " + (isChecked ? "ON" : "OFF") + " (Demo)", Toast.LENGTH_SHORT).show();
        });
        
        btnSnapshot.setOnClickListener(v -> {
            Toast.makeText(this, "Snapshot taken! (Demo)", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
