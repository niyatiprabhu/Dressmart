package com.example.dressmart.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.R;
import com.example.dressmart.models.parse.Garment;
import com.example.dressmart.models.parse.User;
import com.github.islamkhsh.CardSliderAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GarmentAdapter extends CardSliderAdapter<GarmentAdapter.ViewHolder> {
    // this will have to be one of the "values" in the hashmap which corresponds to a list of a specific garment type
    private List<Garment> garments;

    private Context context;

    public GarmentAdapter(List<Garment> garments, Context context){
        this.context = context;
        this.garments = garments;
    }

    @Override
    public int getItemCount(){
        return garments.size();
    }

    @Override
    public GarmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_garment_card, parent, false);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new GarmentAdapter.ViewHolder(view);
    }


    @Override
    public void bindVH(@NonNull GarmentAdapter.ViewHolder viewHolder, int i) {
        // makes sure that when a certain garment is chosen, it will appear on top.
        Garment chosenItem = garments.get(i);
        Log.i("Garments Adapter", "size of garments: " + garments.size());
        if (chosenItem != null) {
            Glide.with(context).load(chosenItem.getGarmentPicture().getUrl()).into(viewHolder.ivGarmentPic);
            try {
                viewHolder.tvGarmentDescription.setText(chosenItem.getDescription());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.tvGarmentDescription.setText("None");
        }
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivGarmentPic;
        TextView tvGarmentDescription;

        public ViewHolder(View view){
            super(view);
            ivGarmentPic = view.findViewById(R.id.ivGarmentPic);
            tvGarmentDescription = view.findViewById(R.id.tvGarmentDescription);
        }

    }
}
