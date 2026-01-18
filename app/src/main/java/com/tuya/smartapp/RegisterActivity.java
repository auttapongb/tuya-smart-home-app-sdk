package com.tuya.smartapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword, etConfirmPassword, etVerificationCode;
    private Button btnRegister, btnGetCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etVerificationCode = findViewById(R.id.et_verification_code);
        btnGetCode = findViewById(R.id.btn_send_code);
        btnRegister = findViewById(R.id.btn_register);
        
        btnGetCode.setOnClickListener(v -> sendVerificationCode());
        btnRegister.setOnClickListener(v -> register());
        
        findViewById(R.id.tv_back_to_login).setOnClickListener(v -> finish());
    }
    
    private void sendVerificationCode() {
        String email = etEmail.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Note: Registration requires proper Tuya SDK setup with correct API endpoints
        // For now, show instruction to user
        Toast.makeText(this, 
            "Please register at https://platform.tuya.com first, then use those credentials to login", 
            Toast.LENGTH_LONG).show();
    }
    
    private void register() {
        Toast.makeText(this, 
            "Please register at https://platform.tuya.com first, then use those credentials to login", 
            Toast.LENGTH_LONG).show();
    }
}
