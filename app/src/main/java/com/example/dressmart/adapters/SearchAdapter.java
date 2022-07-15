package com.example.dressmart.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.R;
import com.example.dressmart.models.parse.OutfitPost;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

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

        try {
            holder.bind(post);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        public void bind(OutfitPost post) throws ParseException {
            // Bind the post data to the view elements

            Log.i("Search adapter", "in the search adapter");
            tvConditionsSearch.setText("sunny and 75");
            tvTopSearch.setText("This is the top");
            tvBottomsSearch.setText("This is the bottoms");
            tvOuterSearch.setText("This is the outer");
            tvShoesSearch.setText("This is the shoes");






//            tvConditionsSearch.setText(post.getTemperature() + " and " + post.getConditions());
//            try {
//                tvTopSearch.setText("Top: " + post.getTop().getDescription());
//            } catch (ParseException ex) {
//                ex.printStackTrace();
//            }
//            try {
//                tvBottomsSearch.setText("Bottoms: " + post.getBottoms().getDescription());
//            } catch (ParseException ex) {
//                ex.printStackTrace();
//            }
//            if (post.getOuter() != null) {
//                try {
//                    tvOuterSearch.setText("Outer: " + post.getOuter().getDescription());
//                } catch (ParseException ex) {
//                    ex.printStackTrace();
//                }
//            } else {
//                tvOuterSearch.setVisibility(View.GONE);
//            }
//            try {
//                tvShoesSearch.setText("Shoes: " + post.getShoes().getDescription());
//            } catch (ParseException ex) {
//                ex.printStackTrace();
//            }
//            ParseFile image = post.getWearingOutfitPicture();
//            if (image != null) {
//                Glide.with(context).load(image.getUrl()).centerCrop().into(ivWearingOutfitPicSearch);
//            }
        }

    }

}
