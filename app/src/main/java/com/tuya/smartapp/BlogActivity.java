package com.tuya.smartapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BlogActivity extends AppCompatActivity {
    private static final String TAG = "BlogActivity";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private Button btnBack;
    private BlogAdapter adapter;
    private List<BlogPost> blogPosts;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            DebugLogger.d(TAG, "=== BlogActivity onCreate START ===");
            setContentView(R.layout.activity_blog);
            
            initializeViews();
            loadBlogPosts();
            
            DebugLogger.d(TAG, "=== BlogActivity onCreate SUCCESS ===");
        } catch (Exception e) {
            DebugLogger.d(TAG, "=== BlogActivity onCreate FAILED ===");
            DebugLogger.d(TAG, "Error: " + e.getMessage());
            Log.e(TAG, "onCreate error", e);
            Toast.makeText(this, "Error loading blog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        DebugLogger.d(TAG, "Initializing views");
        
        recyclerView = findViewById(R.id.recyclerViewBlog);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);
        
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            DebugLogger.d(TAG, "RecyclerView initialized");
        }
        
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                DebugLogger.d(TAG, "Back button clicked");
                finish();
            });
        }
    }
    
    private void loadBlogPosts() {
        DebugLogger.d(TAG, "Loading blog posts");
        
        // Show loading
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        
        // Create sample blog posts from PANDO website
        blogPosts = new ArrayList<>();
        
        blogPosts.add(new BlogPost(
            "PANDO ร่วมมือ JohnJud",
            "มอบสุขอนามัยที่ดีแก่การเริ่มต้นชีวิตใหม่ของน้องแมว",
            "December 22, 2025",
            "https://rabbitrewards-pando.myshopify.com/blogs/news"
        ));
        
        blogPosts.add(new BlogPost(
            "PANDO เปิดจุดให้บริการ",
            "ยืมรถเข็นสัตว์เลี้ยงในงาน King Power Giftival 18-28 ธ.ค. 68",
            "December 18, 2025",
            "https://rabbitrewards-pando.myshopify.com/blogs/news"
        ));
        
        blogPosts.add(new BlogPost(
            "PANDO ร่วมบรรเทาทุกข์ภัยพิบัติ",
            "ส่งมอบอุปกรณ์สัตว์เลี้ยงสู่พื้นที่น้ำท่วมหาดใหญ่",
            "November 28, 2025",
            "https://rabbitrewards-pando.myshopify.com/blogs/news"
        ));
        
        blogPosts.add(new BlogPost(
            "PANDO ร่วมเวที World Pup Expo 2025",
            "จับมือ M Pet Club พร้อมมอบสิทธิพิเศษเฉพาะสมาชิก",
            "August 31, 2025",
            "https://rabbitrewards-pando.myshopify.com/blogs/news"
        ));
        
        blogPosts.add(new BlogPost(
            "PANDO เปิดตัว Gadget",
            "ในงาน Thailand International Dog Show 2025 ผ่านกิจกรรม 'ขนสวย สุขภาพดี เริ่มต้นได้ที่บ้าน'",
            "June 30, 2025",
            "https://rabbitrewards-pando.myshopify.com/blogs/news"
        ));
        
        // Hide loading, show content
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        
        if (blogPosts.isEmpty()) {
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            DebugLogger.d(TAG, "No blog posts found");
        } else {
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new BlogAdapter(this, blogPosts);
                recyclerView.setAdapter(adapter);
                DebugLogger.d(TAG, "Loaded " + blogPosts.size() + " blog posts");
            }
        }
    }
    
    // BlogPost data class
    public static class BlogPost {
        public String title;
        public String description;
        public String date;
        public String url;
        
        public BlogPost(String title, String description, String date, String url) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.url = url;
        }
    }
}
