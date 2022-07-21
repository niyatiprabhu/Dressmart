package com.example.dressmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.LoginActivity;
import com.example.dressmart.R;
import com.example.dressmart.adapters.ProfileAdapter;
import com.example.dressmart.adapters.SearchAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";

    private FragmentProfileBinding binding;

    private RecyclerView rvPostsProfile;
    private ProfileAdapter profileAdapter;
    private List<OutfitPost> posts;

    private RecyclerView rvSearchResults;
    private SearchAdapter searchAdapter;
    private List<OutfitPost> searchResults;


    private User user = (User) ParseUser.getCurrentUser();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPostsProfile = binding.rvPostsProfile;
        rvSearchResults = binding.rvSearchResults;
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.tvNumResultsSearch.setVisibility(View.VISIBLE);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);

        // initialize the array that will hold posts and create a ProfileAdapter
        posts = new ArrayList<>();
        profileAdapter = new ProfileAdapter(getActivity(), posts);
        // set the adapter on the recycler view
        rvPostsProfile.setAdapter(profileAdapter);
        // set the layout manager on the recycler view
        rvPostsProfile.setLayoutManager(glm);

        // same for the search feature
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(getActivity(), searchResults);

        rvSearchResults.setAdapter(searchAdapter);
        rvSearchResults.setLayoutManager(llm);


        binding.svFindOutfits.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rvPostsProfile.setVisibility(View.GONE);
                rvSearchResults.setVisibility(View.VISIBLE);
                binding.tvNumResultsSearch.setVisibility(View.VISIBLE);
                searchAdapter.clear();
                querySearchResults(query);
                binding.svFindOutfits.clearFocus(); // so that setOnQueryTextListener only runs once
                int numResults = searchAdapter.getItemCount();
                String resultsSuffix = numResults == 1 ? " Result" : " Results";
                binding.tvNumResultsSearch.setText(numResults + resultsSuffix);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rvSearchResults.setVisibility(View.VISIBLE);
                querySearchResults(newText);
                return false;
            }
        });

        binding.svFindOutfits.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvPostsProfile.setVisibility(View.GONE);
            }
        });

        binding.svFindOutfits.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchAdapter.clear();
                rvPostsProfile.setVisibility(View.VISIBLE);
                rvSearchResults.setVisibility(View.GONE);
                binding.tvNumResultsSearch.setVisibility(View.GONE);
                return false;
            }
        });


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
                binding.tvNumOutfitsProfile.setText(getNumOutfits(user));
            }
        });
        queryPosts(0);

    }

    public String getNumOutfits(User user) {
        int numOutfits = user.getOutfits().size();
        return numOutfits == 1 ? numOutfits + getString(R.string.heading_outfit) : numOutfits + getString(R.string.heading_outfits_plural);
    }


    protected void queryPosts(int skip) {
        // specify what type of data we want to query - Post.class
        ParseQuery<OutfitPost> query = ParseQuery.getQuery(OutfitPost.class);
        // include data referred by user key
        query.include(OutfitPost.KEY_AUTHOR);
        query.include(OutfitPost.KEY_TOP);
        query.include(OutfitPost.KEY_BOTTOMS);
        query.include(OutfitPost.KEY_OUTER);
        query.include(OutfitPost.KEY_SHOES);
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
                profileAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void querySearchResults(String description) {

        // query that matches any garment whose description contains the search term
        ParseQuery<Garment> matchingGarmentQuery = ParseQuery.getQuery(Garment.class);
        matchingGarmentQuery.whereContains(Garment.KEY_DESCRIPTION, description);

        // outfit posts where the top matches the garment query
        ParseQuery<OutfitPost> topQuery = ParseQuery.getQuery(OutfitPost.class);
        topQuery.whereMatchesQuery(OutfitPost.KEY_TOP, matchingGarmentQuery);

        // outfit posts where the bottoms matches the garment query
        ParseQuery<OutfitPost> bottomQuery = ParseQuery.getQuery(OutfitPost.class);
        bottomQuery.whereMatchesQuery(OutfitPost.KEY_BOTTOMS, matchingGarmentQuery);

        // outfit posts where the outerwear matches the garment query
        ParseQuery<OutfitPost> outerQuery = ParseQuery.getQuery(OutfitPost.class);
        outerQuery.whereMatchesQuery(OutfitPost.KEY_OUTER, matchingGarmentQuery);

        // outfit posts where the shoes matches the garment query
        ParseQuery<OutfitPost> shoeQuery = ParseQuery.getQuery(OutfitPost.class);
        shoeQuery.whereMatchesQuery(OutfitPost.KEY_SHOES, matchingGarmentQuery);

        List<ParseQuery<OutfitPost>> allMatchingPostQueries = new ArrayList<>();
        allMatchingPostQueries.add(topQuery);
        allMatchingPostQueries.add(bottomQuery);
        allMatchingPostQueries.add(outerQuery);
        allMatchingPostQueries.add(shoeQuery);

        // `OR` all of our outfit post queries, so results are any posts
        // where any garment's description matches the search term.
        ParseQuery<OutfitPost> matchingPostQuery = ParseQuery.or(allMatchingPostQueries);

        // Posts by the user should only have items from the user's closet so I think checking this once is enough
        matchingPostQuery.whereEqualTo(OutfitPost.KEY_AUTHOR, user);
        matchingPostQuery.setLimit(5);

        matchingPostQuery.findInBackground(new FindCallback<OutfitPost>() {
            // find callback here to save the result list of OutfitPosts
            @Override
            public void done(List<OutfitPost> objects, ParseException e) {

                if(e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                objects.removeAll(Collections.singletonList(null));
                searchResults.addAll(objects);
                searchAdapter.notifyDataSetChanged();
            }
        });
    }
}