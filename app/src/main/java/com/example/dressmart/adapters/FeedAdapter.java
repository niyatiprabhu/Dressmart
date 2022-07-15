package com.example.dressmart.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.R;
import com.example.dressmart.models.parse.OutfitPost;
import com.example.dressmart.util.TimeUtil;
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

        private TextView tvWeather;
        private ImageView ivImage;
        private TextView tvDisplayName;
        private TextView tvTimestamp;
        private ImageView ivProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeather = itemView.findViewById(R.id.tvWeather);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
        }

        public void bind(OutfitPost post) {
            // Bind the post data to the view elements
            tvDisplayName.setText(post.getAuthor().getDisplayName());
            tvWeather.setText(post.getTemperature() + " and " + post.getConditions());
            Date dateCreated = post.getCreatedAt();
            tvTimestamp.setText(TimeUtil.calculateTimeAgo(dateCreated));
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

}
