package com.example.dressmart.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressmart.LoginActivity;
import com.example.dressmart.MainActivity;
import com.example.dressmart.adapters.ProfileAdapter;
import com.example.dressmart.databinding.FragmentProfileBinding;
import com.example.dressmart.models.OutfitPost;
import com.example.dressmart.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    RecyclerView rvPostsProfile;
    ProfileAdapter adapter;
    List<OutfitPost> posts;
    TextView tvUsernameProfile;
    ImageView ivProfilePicProfile;
    TextView tvDisplayNameProfile;
    TextView tvNumOutfitsProfile;
    Button btnLogout;

    User user = (User) ParseUser.getCurrentUser();


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

        rvPostsProfile = binding.rvPostsProfile;

        int numberOfColumns = 3;
        GridLayoutManager glm = new GridLayoutManager(getContext(), numberOfColumns);

        // initialize the array that will hold posts and create a PostsAdapter
        posts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), posts);
        // set the adapter on the recycler view
        rvPostsProfile.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPostsProfile.setLayoutManager(glm);

        tvUsernameProfile = binding.tvUsernameProfile;
        tvDisplayNameProfile = binding.tvDisplayNameProfile;
        ivProfilePicProfile = binding.ivProfilePicProfile;
        tvNumOutfitsProfile = binding.tvNumOutfitsProfile;
        btnLogout = binding.btnLogout;

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                user = (User) object;
                Glide.with(ProfileFragment.this).load(user.getProfilePicture().getUrl()).circleCrop().into(ivProfilePicProfile);
                tvUsernameProfile.setText("@" + user.getUsername());
                tvDisplayNameProfile.setText(user.getDisplayName());
                tvNumOutfitsProfile.setText(getNumOutfits());
            }
        });

    }

    private String getNumOutfits() {
        int numOutfits = user.getOutfits().size();
        String displayVal = String.valueOf(numOutfits);
        if (numOutfits == 1) {
            displayVal += " Outfit";
        } else {
            displayVal += " Outfits";
        }
        return displayVal;
    }
}