package com.android.pandosdktest;

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
        
        try {
            setContentView(R.layout.activity_register);
            
            TextInputEditText etEmail = findViewById(R.id.et_email);
            Button btnSendCode = findViewById(R.id.btn_send_code);
            TextView tvBackToLogin = findViewById(R.id.tv_back_to_login);
            
            if (btnSendCode != null) {
                btnSendCode.setOnClickListener(v -> {
                    try {
                        if (etEmail != null) {
                            String email = etEmail.getText().toString().trim();
                            if (email.isEmpty()) {
                                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Registration feature coming soon!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (tvBackToLogin != null) {
                tvBackToLogin.setOnClickListener(v -> finish());
            }
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Register");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading registration", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
