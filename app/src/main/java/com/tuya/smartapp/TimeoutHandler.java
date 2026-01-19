package com.tuya.smartapp;

import android.os.Handler;
import android.os.Looper;

/**
 * Helper class for handling operation timeouts
 * Prevents operations from hanging forever
 */
public class TimeoutHandler {
    private final Handler handler;
    private Runnable timeoutRunnable;
    
    public TimeoutHandler() {
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Start a timeout timer
     * @param timeoutMs Timeout in milliseconds
     * @param onTimeout Callback to run when timeout occurs
     */
    public void start(long timeoutMs, Runnable onTimeout) {
        cancel(); // Cancel any existing timeout
        timeoutRunnable = onTimeout;
        handler.postDelayed(timeoutRunnable, timeoutMs);
        DebugLogger.d("TimeoutHandler", "Timeout started: " + timeoutMs + "ms");
    }
    
    /**
     * Cancel the timeout timer
     * Call this when the operation completes successfully
     */
    public void cancel() {
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
            DebugLogger.d("TimeoutHandler", "Timeout cancelled");
        }
    }
    
    /**
     * Clean up resources
     * Call this in onDestroy()
     */
    public void cleanup() {
        cancel();
    }
}
