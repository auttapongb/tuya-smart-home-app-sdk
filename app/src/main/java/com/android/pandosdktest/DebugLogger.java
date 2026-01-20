package com.android.pandosdktest;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * In-app debug logger that captures all logs for display on screen
 * NOW WITH PERSISTENT FILE STORAGE!
 */
public class DebugLogger {
    private static final String TAG = "DebugLogger";
    private static final int MAX_LOGS = 500; // Increased for longer sessions
    private static final String LOG_FILE_NAME = "pando_debug_logs.txt";
    
    private static final List<String> logs = new ArrayList<>();
    private static LogUpdateListener listener;
    private static final SimpleDateFormat timeFormat = 
        new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
    private static Context appContext;
    private static File logFile;
    
    public interface LogUpdateListener {
        void onLogUpdate();
    }
    
    /**
     * Initialize with application context (call from Application.onCreate)
     */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
        logFile = new File(appContext.getFilesDir(), LOG_FILE_NAME);
        loadLogsFromFile();
    }
    
    public static void setListener(LogUpdateListener listener) {
        DebugLogger.listener = listener;
    }
    
    // Alias for compatibility
    public static void setLogUpdateListener(LogUpdateListener listener) {
        setListener(listener);
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
        
        // Save to file asynchronously
        saveLogToFile(logEntry);
        
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
    
    private static void saveLogToFile(String logEntry) {
        if (logFile == null) return;
        
        new Thread(() -> {
            try {
                FileWriter writer = new FileWriter(logFile, true); // Append mode
                writer.write(logEntry + "\n");
                writer.close();
            } catch (Exception e) {
                Log.e(TAG, "Error saving log to file", e);
            }
        }).start();
    }
    
    private static void loadLogsFromFile() {
        if (logFile == null || !logFile.exists()) return;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            List<String> fileLogs = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                fileLogs.add(line);
            }
            reader.close();
            
            // Keep only last MAX_LOGS entries
            synchronized (logs) {
                logs.clear();
                int start = Math.max(0, fileLogs.size() - MAX_LOGS);
                logs.addAll(fileLogs.subList(start, fileLogs.size()));
            }
            
            Log.d(TAG, "Loaded " + logs.size() + " logs from file");
        } catch (Exception e) {
            Log.e(TAG, "Error loading logs from file", e);
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
    
    // Alias for compatibility
    public static String getAllLogs() {
        return getLogsAsString();
    }
    
    public static void clear() {
        synchronized (logs) {
            logs.clear();
        }
        
        // Clear file
        if (logFile != null && logFile.exists()) {
            logFile.delete();
        }
        
        if (listener != null) {
            listener.onLogUpdate();
        }
    }
    
    // Alias for compatibility
    public static void clearLogs() {
        clear();
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
