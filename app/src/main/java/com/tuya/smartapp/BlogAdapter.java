package com.tuya.smartapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    
    private Context context;
    private List<BlogActivity.BlogPost> blogPosts;
    
    public BlogAdapter(Context context, List<BlogActivity.BlogPost> blogPosts) {
        this.context = context;
        this.blogPosts = blogPosts;
    }
    
    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blog_post, parent, false);
        return new BlogViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogActivity.BlogPost post = blogPosts.get(position);
        
        holder.tvTitle.setText(post.title);
        holder.tvDescription.setText(post.description);
        holder.tvDate.setText(post.date);
        
        holder.itemView.setOnClickListener(v -> {
            // Open blog URL in browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(post.url));
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return blogPosts != null ? blogPosts.size() : 0;
    }
    
    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBlogTitle);
            tvDescription = itemView.findViewById(R.id.tvBlogDescription);
            tvDate = itemView.findViewById(R.id.tvBlogDate);
        }
    }
}
