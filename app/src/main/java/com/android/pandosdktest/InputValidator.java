package com.android.pandosdktest;

import android.util.Patterns;

/**
 * Utility class for validating user inputs
 * Prevents invalid data from being processed
 */
public class InputValidator {
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate password (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }
    
    /**
     * Get password strength rating
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Empty";
        }
        if (password.length() < 6) {
            return "Too short (min 6 characters)";
        }
        if (password.length() < 8) {
            return "Weak";
        }
        
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        int strength = 0;
        if (hasUpper) strength++;
        if (hasLower) strength++;
        if (hasDigit) strength++;
        if (hasSpecial) strength++;
        
        if (strength >= 3 && password.length() >= 10) {
            return "Strong";
        } else if (strength >= 2 && password.length() >= 8) {
            return "Medium";
        } else {
            return "Weak";
        }
    }
    
    /**
     * Validate WiFi SSID
     * SSID max length is 32 bytes
     */
    public static boolean isValidSSID(String ssid) {
        if (ssid == null || ssid.trim().isEmpty()) {
            return false;
        }
        if (ssid.length() > 32) {
            return false;
        }
        return true;
    }
    
    /**
     * Validate WiFi password
     * WPA/WPA2 password must be 8-63 characters
     */
    public static boolean isValidWiFiPassword(String password) {
        if (password == null) {
            return false;
        }
        // WPA/WPA2 password must be 8-63 characters
        return password.length() >= 8 && password.length() <= 63;
    }
    
    /**
     * Validate phone number (basic check)
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove common formatting characters
        String cleaned = phone.replaceAll("[\\s\\-\\(\\)\\+]", "");
        // Check if it's all digits and reasonable length
        return cleaned.matches("\\d{7,15}");
    }
    
    /**
     * Validate device name
     */
    public static boolean isValidDeviceName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (name.length() > 50) {
            return false;
        }
        return true;
    }
    
    /**
     * Get error message for invalid email
     */
    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format";
        }
        return null;
    }
    
    /**
     * Get error message for invalid password
     */
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }
    
    /**
     * Get error message for invalid SSID
     */
    public static String getSSIDError(String ssid) {
        if (ssid == null || ssid.trim().isEmpty()) {
            return "WiFi SSID is required";
        }
        if (ssid.length() > 32) {
            return "SSID too long (max 32 characters)";
        }
        return null;
    }
    
    /**
     * Get error message for invalid WiFi password
     */
    public static String getWiFiPasswordError(String password) {
        if (password == null) {
            return "WiFi password is required";
        }
        if (password.length() < 8) {
            return "WiFi password must be at least 8 characters";
        }
        if (password.length() > 63) {
            return "WiFi password too long (max 63 characters)";
        }
        return null;
    }
}
