package com.example.dressmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.R;
import com.example.dressmart.models.parse.OutfitPost;
import com.parse.ParseFile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<OutfitPost> posts;

    public ProfileAdapter(Context context, List<OutfitPost> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_post, parent, false);
        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        OutfitPost post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivUserPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPost = itemView.findViewById(R.id.ivUserPost);
        }

        public void bind(OutfitPost post) {
            // Bind the post data to the view elements
            ParseFile image = post.getWearingOutfitPicture();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivUserPost);
            }
        }
    }
}
