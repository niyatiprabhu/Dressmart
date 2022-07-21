package com.example.dressmart.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressmart.R;
import com.example.dressmart.adapters.FeedAdapter;
import com.example.dressmart.databinding.FragmentFeedBinding;
import com.example.dressmart.models.parse.OutfitPost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private static final String TAG = "Feed Fragment";

    private FeedAdapter adapter;
    private List<OutfitPost> allPosts;

    private RecyclerView rvPosts;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new FeedAdapter(getActivity(), allPosts);
        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvPosts.setLayoutManager(llm);

        queryPosts(0);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        
        // limit query to latest 20 items
        query.setLimit(20);
        query.setSkip(skip);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // maybe only display posts from today?? like BeReal
        query.whereEqualTo("createdAt", );
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<OutfitPost>() {
            @Override
            public void done(List<OutfitPost> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}