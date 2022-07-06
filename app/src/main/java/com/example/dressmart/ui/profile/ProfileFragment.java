package com.example.dressmart.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.dressmart.LoginActivity;
import com.example.dressmart.adapters.ProfileAdapter;
import com.example.dressmart.databinding.FragmentProfileBinding;
import com.example.dressmart.models.parse.Garment;
import com.example.dressmart.models.parse.OutfitPost;
import com.example.dressmart.models.parse.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";

    private FragmentProfileBinding binding;

    private RecyclerView rvPostsProfile;
    private ProfileAdapter adapter;
    private List<OutfitPost> posts;


    private User user = (User) ParseUser.getCurrentUser();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Log.i(TAG, user.getNumOutfits());

        // ********************************** TESTING ADDING A NEW OUTFIT POST AND SEEING IF IT SHOWS UP ON GRIDVIEW
//        Garment top = new Garment("White tee", "Top", "Short-Sleeved");
//        top.saveInBackground();
//        Garment bottom = new Garment("Corduroy Pants", "Bottoms", "Pants");
//        bottom.saveInBackground();
//        Garment outer = new Garment("Green Sweater", "Outer", "Sweater");
//        outer.saveInBackground();
//        Garment shoes = new Garment("Black Converse", "Shoes", "Sneakers");
//        shoes.saveInBackground();
//        List<Garment> garments = new ArrayList<>();
//        garments.add(top);
//        garments.add(bottom);
//        garments.add(outer);
//        garments.add(shoes);
//        user.setCloset(garments);
//        OutfitPost newPost = new OutfitPost((User)ParseUser.getCurrentUser(), new ArrayList<>(), garments, 55, "Partly Cloudy");
//        newPost.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, e.getMessage(), e);
//                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Log.i(TAG, "Post save was successful!");
//                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
//            }
//        });






        rvPostsProfile = binding.rvPostsProfile;


        int numberOfColumns = 2;
        GridLayoutManager glm = new GridLayoutManager(getContext(), numberOfColumns);

        // initialize the array that will hold posts and create a ProfileAdapter
        posts = new ArrayList<>();
        adapter = new ProfileAdapter(getActivity(), posts);
        // set the adapter on the recycler view
        rvPostsProfile.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPostsProfile.setLayoutManager(glm);


        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                });

            }
        });

        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                user = (User) object;
                Glide.with(getContext()).load(user.getProfilePicture().getUrl()).circleCrop().into(binding.ivProfilePicProfile);
                binding.tvUsernameProfile.setText("@" + user.getUsername());
                binding.tvDisplayNameProfile.setText(user.getDisplayName());
                Log.i(TAG, "Number of fits: " + user.getNumOutfits());
                binding.tvNumOutfitsProfile.setText(user.getNumOutfits());
            }
        });
        Log.i(TAG, "Number of posts: " + adapter.getItemCount());
        queryPosts(0);

    }


    protected void queryPosts(int skip) {
        // specify what type of data we want to query - Post.class
        Log.i(TAG, "in queryPost");
        ParseQuery<OutfitPost> query = ParseQuery.getQuery(OutfitPost.class);
        // include data referred by user key
        query.include(OutfitPost.KEY_AUTHOR);
        query.include(OutfitPost.KEY_GARMENTS);
        query.include(OutfitPost.KEY_LIKED_BY);
        query.whereEqualTo(OutfitPost.KEY_AUTHOR, user);
        // limit query to latest 20 items
        query.setLimit(20);
        query.setSkip(skip);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<OutfitPost>() {
            @Override
            public void done(List<OutfitPost> fetchedPosts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                posts.addAll(fetchedPosts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}