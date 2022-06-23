package com.example.dressmart.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.R;
import com.example.dressmart.models.OutfitPost;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private Context context;
    private List<OutfitPost> posts;

    public FeedAdapter(Context context, List<OutfitPost> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OutfitPost post = posts.get(position);
        holder.bind(post);
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<OutfitPost> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDisplayName;
        private TextView tvTimestamp;
        private ImageView ivProfilePic;
        private TextView tvNumLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
        }

        public void bind(OutfitPost post) {
            // Bind the post data to the view elements
            tvDisplayName.setText(post.getAuthor().getDisplayName());
            tvUsername.setText("@" + post.getAuthor().getUsername());
            //tvNumLikes.setText(post.getLikesCount());
            Date dateCreated = post.getCreatedAt();
            tvTimestamp.setText(calculateTimeAgo(dateCreated));
            ParseFile image = post.getWearingOutfitPicture();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            ParseFile profilePic = post.getAuthor().getProfilePicture();
            if (profilePic != null) {
                Glide.with(context).load(profilePic.getUrl()).circleCrop().into(ivProfilePic);
            }
        }


    }
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
