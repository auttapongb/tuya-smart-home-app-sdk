package com.android.pandosdktest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Handles uncaught exceptions and generates crash reports
 */
public class CrashReportHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashReportHandler";
    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    
    public CrashReportHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            // Generate crash report
            String crashReport = generateCrashReport(throwable);
            
            // Save to local storage
            File crashFile = saveCrashReport(crashReport);
            
            // Log the crash
            DebugLogger.e(TAG, "=== APP CRASHED ===");
            DebugLogger.e(TAG, "Crash report saved to: " + (crashFile != null ? crashFile.getAbsolutePath() : "failed"));
            DebugLogger.e(TAG, crashReport);
            
            // Show crash dialog (on UI thread)
            showCrashDialog(crashFile, crashReport);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Call default handler
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        }
    }
    
    private String generateCrashReport(Throwable throwable) {
        StringBuilder report = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        
        report.append("=".repeat(60)).append("\n");
        report.append("PANDO APP CRASH REPORT\n");
        report.append("=".repeat(60)).append("\n\n");
        
        report.append("Time: ").append(dateFormat.format(new Date())).append("\n");
        report.append("App Version: 3.24.0-CrashReport\n\n");
        
        report.append("Exception:\n");
        report.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n\n");
        
        report.append("Stack Trace:\n");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        report.append(sw.toString()).append("\n");
        
        report.append("=".repeat(60)).append("\n");
        report.append("DEBUG LOGS:\n");
        report.append("=".repeat(60)).append("\n\n");
        report.append(DebugLogger.getAllLogs());
        
        return report.toString();
    }
    
    private File saveCrashReport(String crashReport) {
        try {
            // Save to app's external files directory (accessible without permissions)
            File crashDir = new File(context.getExternalFilesDir(null), "crash_reports");
            if (!crashDir.exists()) {
                crashDir.mkdirs();
            }
            
            SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
            String fileName = "crash_" + fileFormat.format(new Date()) + ".txt";
            File crashFile = new File(crashDir, fileName);
            
            FileWriter writer = new FileWriter(crashFile);
            writer.write(crashReport);
            writer.close();
            
            return crashFile;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void showCrashDialog(final File crashFile, final String crashReport) {
        try {
            // Post to UI thread
            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
            mainHandler.post(() -> {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("App Crashed");
                    builder.setMessage("PANDO has crashed. Would you like to save or share the crash report?");
                    
                    builder.setPositiveButton("Share Report", (dialog, which) -> {
                        shareCrashReport(crashFile, crashReport);
                    });
                    
                    builder.setNeutralButton("View Location", (dialog, which) -> {
                        if (crashFile != null) {
                            Toast.makeText(context, 
                                "Crash report saved to:\n" + crashFile.getAbsolutePath(), 
                                Toast.LENGTH_LONG).show();
                        }
                    });
                    
                    builder.setNegativeButton("Close", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    
                    builder.setCancelable(false);
                    
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    dialog.show();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            // Give time for dialog to show
            Thread.sleep(1000);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void shareCrashReport(File crashFile, String crashReport) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PANDO App Crash Report");
            shareIntent.putExtra(Intent.EXTRA_TEXT, crashReport);
            
            if (crashFile != null && crashFile.exists()) {
                android.net.Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    crashFile
                );
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType("text/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(shareIntent, "Share Crash Report").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error sharing crash report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
