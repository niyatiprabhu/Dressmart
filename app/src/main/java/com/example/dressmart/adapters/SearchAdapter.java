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
import com.parse.ParseFile;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<OutfitPost> results;

    public SearchAdapter(Context context, List<OutfitPost> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OutfitPost post = results.get(position);
        holder.bind(post);
    }

    // Clean all elements of the recycler
    public void clear() {
        results.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<OutfitPost> list) {
        results.addAll(list);
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivWearingOutfitPicSearch;
        private TextView tvConditionsSearch;
        private TextView tvTopSearch;
        private TextView tvBottomsSearch;
        private TextView tvOuterSearch;
        private TextView tvShoesSearch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWearingOutfitPicSearch = itemView.findViewById(R.id.ivWearingOutfitPicSearch);
            tvConditionsSearch = itemView.findViewById(R.id.tvConditionsSearch);
            tvTopSearch = itemView.findViewById(R.id.tvTopSearch);
            tvBottomsSearch = itemView.findViewById(R.id.tvBottomsSearch);
            tvOuterSearch = itemView.findViewById(R.id.tvOuterSearch);
            tvShoesSearch = itemView.findViewById(R.id.tvShoesSearch);
        }

        public void bind(OutfitPost post) {
            // Bind the post data to the view elements
            tvConditionsSearch.setText(post.getTemperature() + " and " + post.getConditions());
            tvTopSearch.setText(post.getTop().getDescription());
            tvBottomsSearch.setText(post.getBottoms().getDescription());
            tvOuterSearch.setText(post.getOuter().getDescription());
            tvShoesSearch.setText(post.getShoes().getDescription());
            ParseFile image = post.getWearingOutfitPicture();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivWearingOutfitPicSearch);
            }

        }


    }

}
