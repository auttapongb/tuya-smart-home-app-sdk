package com.tuya.smartapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        TextInputEditText etEmail = findViewById(R.id.et_email);
        Button btnSendCode = findViewById(R.id.btn_send_code);
        TextView tvBackToLogin = findViewById(R.id.tv_back_to_login);
        
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registration feature coming in Phase 2!", Toast.LENGTH_SHORT).show();
            }
        });
        
        tvBackToLogin.setOnClickListener(v -> finish());
    }
}
