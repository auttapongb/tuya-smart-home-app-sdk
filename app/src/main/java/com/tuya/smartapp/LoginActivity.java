package com.tuya.smartapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "=== LoginActivity onCreate START ===");
        
        try {
            Log.d(TAG, "Setting content view");
            setContentView(R.layout.activity_login);
            Log.d(TAG, "Content view set successfully");
            
            etEmail = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            btnLogin = findViewById(R.id.btn_login);
            progressBar = findViewById(R.id.progress_bar);
            tvRegister = findViewById(R.id.tv_register);
            
            if (btnLogin != null) {
                btnLogin.setOnClickListener(v -> handleLogin());
            }
            
            if (tvRegister != null) {
                tvRegister.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(this, RegisterActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Login");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                Log.d(TAG, "Action bar configured");
            }
            
            Log.d(TAG, "=== LoginActivity onCreate SUCCESS ===");
        } catch (Exception e) {
            Log.e(TAG, "=== LoginActivity onCreate FAILED ===", e);
            Toast.makeText(this, "Error loading login screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "=== LoginActivity onStart ===");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=== LoginActivity onDestroy ===");
    }
    
    private void handleLogin() {
        try {
            if (etEmail == null || etPassword == null) {
                Toast.makeText(this, "Error: Form not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
        
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        
            // Simulate login
            Toast.makeText(this, "Login successful! (Demo)", Toast.LENGTH_SHORT).show();
            
            try {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Home screen coming soon!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
