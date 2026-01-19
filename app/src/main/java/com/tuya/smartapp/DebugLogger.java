package com.tuya.smartapp;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * In-app debug logger that captures all logs for display on screen
 */
public class DebugLogger {
    private static final String TAG = "DebugLogger";
    private static final int MAX_LOGS = 200;
    
    private static final List<String> logs = new ArrayList<>();
    private static LogUpdateListener listener;
    private static final SimpleDateFormat timeFormat = 
        new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
    
    public interface LogUpdateListener {
        void onLogUpdate();
    }
    
    public static void setListener(LogUpdateListener listener) {
        DebugLogger.listener = listener;
    }
    
    public static void d(String tag, String message) {
        Log.d(tag, message);
        addLog("D", tag, message);
    }
    
    public static void e(String tag, String message) {
        Log.e(tag, message);
        addLog("E", tag, message);
    }
    
    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
        addLog("E", tag, message + "\n" + getStackTrace(throwable));
    }
    
    private static void addLog(String level, String tag, String message) {
        String timestamp = timeFormat.format(new Date());
        String logEntry = timestamp + " " + level + "/" + tag + ": " + message;
        
        synchronized (logs) {
            logs.add(logEntry);
            
            // Keep only last MAX_LOGS entries
            if (logs.size() > MAX_LOGS) {
                logs.remove(0);
            }
        }
        
        // Notify listener on main thread
        if (listener != null) {
            try {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onLogUpdate();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener", e);
            }
        }
    }
    
    public static List<String> getLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }
    
    public static String getLogsAsString() {
        StringBuilder sb = new StringBuilder();
        synchronized (logs) {
            for (String log : logs) {
                sb.append(log).append("\n");
            }
        }
        return sb.toString();
    }
    
    public static void clear() {
        synchronized (logs) {
            logs.clear();
        }
        if (listener != null) {
            listener.onLogUpdate();
        }
    }
    
    private static String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");
        
        StackTraceElement[] elements = throwable.getStackTrace();
        int count = Math.min(5, elements.length); // Show first 5 lines
        for (int i = 0; i < count; i++) {
            sb.append("  at ").append(elements[i].toString()).append("\n");
        }
        
        if (elements.length > 5) {
            sb.append("  ... ").append(elements.length - 5).append(" more\n");
        }
        
        return sb.toString();
    }
}
