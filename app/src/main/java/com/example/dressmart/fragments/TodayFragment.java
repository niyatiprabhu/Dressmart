package com.example.dressmart.fragments;

import static com.example.dressmart.Constants.BOTTOMS;
import static com.example.dressmart.Constants.OUTER;
import static com.example.dressmart.Constants.SHOES;
import static com.example.dressmart.Constants.TOP;
import static com.example.dressmart.models.WeatherCondition.PARTLY_CLOUDY;
import static com.example.dressmart.models.WeatherCondition.SUNNY;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.dressmart.R;
import com.example.dressmart.adapters.GarmentAdapter;
import com.example.dressmart.databinding.FragmentTodayBinding;
import com.example.dressmart.models.RecommendedOutfit;
import com.example.dressmart.models.WeatherCondition;
import com.example.dressmart.models.parse.Garment;
import com.example.dressmart.models.parse.OutfitPost;
import com.example.dressmart.models.parse.User;
import com.example.dressmart.util.RecommendationUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;


public class TodayFragment extends Fragment {

    private FragmentTodayBinding binding;
    private static final String TAG = "Today Fragment";

    private static final String BASE_URL = "https://api.weatherbit.io/v2.0/forecast/daily?";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    private FusedLocationProviderClient mFusedLocationClient;
    private double latitude;
    private double longitude;
    private int PERMISSION_ID = 44;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */

    private LocationRequest mLocationRequest;

    private static boolean hasSubmitted;

    // move to weather class
    WeatherCondition weatherCondition;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTodayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setVisibility(View.INVISIBLE);

        mFusedLocationClient = getFusedLocationProviderClient(getActivity());
        startLocationUpdates();
        weatherFromJson();

    }


    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        if (locationResult == null) {
                            Toast.makeText(getActivity(), "locationResult is null", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Location location = locationResult.getLastLocation();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                },
                Looper.myLooper());
    }


    private void weatherFromJson() {
        AsyncHttpClient client = new AsyncHttpClient();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.weatherbit.io")
                .appendPath("v2.0")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("units", "I")
                .appendQueryParameter("days", "1")
                .appendQueryParameter("lat", String.valueOf(latitude))
                .appendQueryParameter("lon", String.valueOf(longitude))
                .appendQueryParameter("key", getActivity().getString(R.string.api_key));
        String myUrl = builder.build().toString();
        client.get(myUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                try {
                    weatherCondition = WeatherCondition.weatherFromJson(json.jsonObject);
                    bind();
                    selectOutfit();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Oops! onFailure. " + throwable.getMessage());
            }
        });
    }

    private void setWeatherIcon() {
        @DrawableRes int icon;
        if (weatherCondition.getConditions().equals(SUNNY)) {
            icon = R.drawable.sunny;
        } else if (weatherCondition.getConditions().equals(PARTLY_CLOUDY)) {
            icon = R.drawable.partly_cloudy;
        } else {
            icon = R.drawable.cloudy;
        }
        Glide.with(getActivity()).load(icon).override(400, 400).into(binding.ivWeatherIconToday);
    }

    public void bind() {
        binding.tvConditionsToday.setText(weatherCondition.getConditions());
        setWeatherIcon();
        binding.tvTempToday.setText(String.valueOf((int) weatherCondition.getAvgTemp()));
        binding.rbMatchScore.setVisibility(View.GONE);
        binding.tvNumStars.setVisibility(View.GONE);
    }


    public void selectOutfit() throws ParseException {
        User user = (User) ParseUser.getCurrentUser();

        // Specify which class to query
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.include(User.KEY_CLOSET);
        query.include(User.KEY_OUTFITS);
        // Specify the object id
        query.getInBackground(user.getObjectId(), new GetCallback<User>() {
            public void done(User user, ParseException e) {
                if (e == null) {
                    if (getView() != null) {
                        getView().setVisibility(View.VISIBLE);
                    }

                    HashMap<String, List<Garment>> closet = createCloset(user);

                    // check if there are any clothes in the closet yet


                    RecommendedOutfit recommendedOutfit = RecommendationUtil.getRecommendation(weatherCondition, closet);
                    displayRecommendedOutfit(recommendedOutfit, closet);

                    binding.btnSubmitToday.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // prompt camera app to take outfit pic
                            onLaunchCamera(v);
                            OutfitPost post = new OutfitPost();
                            populatePost(post, closet);

                            // calculate the match score of the 4 garments put together and display it
                            // in the rating stars, the number of stars filled up corresponds to the value of calculateMatchScore
                            // set the visibility of the color match rating stars to VISIBLE and the submit button to GONE
                            try {
                                post.setColorMatchScore(RecommendationUtil.calculateMatchScore(post.getTop(), post.getBottoms(), post.getShoes()));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            post.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "Issue with saving post", e);
                                        return;
                                    }
                                    user.addParseOutfit(post);
                                    user.saveInBackground();
                                    Log.i(TAG, "Post save was successful!");
                                    updateUIAfterPosting(post);

                                    // set flag to true
                                    hasSubmitted = true;

                                    setLastWorn(post);
                                    doSubmitAnimation();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private HashMap<String, List<Garment>> createCloset(User user) {
        HashMap<String, List<Garment>> closet = new HashMap<>();
        for(Garment item : user.getCloset()) {
            if(closet.containsKey(item.getGarmentType())) {
                //Add to existing list
                closet.get(item.getGarmentType()).add(item);

            } else {
                //Create new list
                ArrayList<Garment> garments = new ArrayList<Garment>(1);
                garments.add(item);
                closet.put(item.getGarmentType(), garments);
            }
        }
        return closet;
    }

    private void displayRecommendedOutfit(RecommendedOutfit recommendedOutfit, HashMap<String, List<Garment>> closet) {
        // put the chosen items at the top of their respective lists
        closet.get(TOP).remove(recommendedOutfit.getTop());
        closet.get(TOP).add(0, recommendedOutfit.getTop());
        closet.get(BOTTOMS).remove(recommendedOutfit.getBottoms());
        closet.get(BOTTOMS).add(0, recommendedOutfit.getBottoms());
        closet.get(OUTER).remove(recommendedOutfit.getOuter());
        closet.get(OUTER).add(0, recommendedOutfit.getOuter());
        closet.get(SHOES).remove(recommendedOutfit.getShoes());
        closet.get(SHOES).add(0, recommendedOutfit.getShoes());

        // set the adapters for all 4 garment cards
        GarmentAdapter topAdapter = new GarmentAdapter(closet.get(TOP), getContext());
        GarmentAdapter bottomsAdapter = new GarmentAdapter(closet.get(BOTTOMS), getContext());
        GarmentAdapter outerAdapter = new GarmentAdapter(closet.get(OUTER), getContext());
        GarmentAdapter shoesAdapter = new GarmentAdapter(closet.get(SHOES), getContext());
        binding.vpGarment1.setAdapter(topAdapter);
        binding.vpGarment2.setAdapter(bottomsAdapter);
        binding.vpGarment3.setAdapter(outerAdapter);
        binding.vpGarment4.setAdapter(shoesAdapter);
    }

    private void setLastWorn(OutfitPost post) {
        // set items last worn date to today
        post.getTop().setDateLastWorn(Calendar.getInstance().getTime());
        post.getBottoms().setDateLastWorn(Calendar.getInstance().getTime());
        if (post.getOuter() != null) {
            post.getOuter().setDateLastWorn(Calendar.getInstance().getTime());
        }
        post.getShoes().setDateLastWorn(Calendar.getInstance().getTime());
    }

    private void doSubmitAnimation() {
        // fade out the gridview and fade in the imageview
        Animation animFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        binding.glGarments.startAnimation(animFadeOut);
        Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        binding.ivWearingOutfitPicToday.startAnimation(animFadeIn);
    }

    private void updateUIAfterPosting(OutfitPost post) {
        // display the match score and remove the submit button
        binding.btnSubmitToday.setVisibility(View.GONE);
        binding.tvNumStars.setText("Match Score: " + String.valueOf(post.getColorMatchScore()) + "!");
        binding.rbMatchScore.setRating((float) post.getColorMatchScore());
        binding.tvNumStars.setVisibility(View.VISIBLE);
        binding.rbMatchScore.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(post.getWearingOutfitPicture().getUrl()).into(binding.ivWearingOutfitPicToday);
        binding.tvOurPicks.setText("Your Outfit");

    }

    private void populatePost(OutfitPost post, HashMap<String,List<Garment>> closet) {
        post.setParseTemperature((int)weatherCondition.getAvgTemp());
        post.setParseConditions(weatherCondition.getConditions());
        post.setParseAuthor((User)ParseUser.getCurrentUser());
        post.setParseWearingOutfitPicture(new ParseFile(photoFile));

        // set the chosen garments to the actual cards the user chose
        post.setParseTop(closet.get(TOP).get(binding.vpGarment1.getCurrentItem()));
        post.setParseBottoms(closet.get(BOTTOMS).get(binding.vpGarment2.getCurrentItem()));
        post.setParseOuter(closet.get(OUTER).get(binding.vpGarment3.getCurrentItem()));
        post.setParseShoes(closet.get(SHOES).get(binding.vpGarment4.getCurrentItem()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSubmitted) {
            binding.btnSubmitToday.setVisibility(View.GONE);
            binding.rbMatchScore.setVisibility(View.VISIBLE);
            binding.tvNumStars.setVisibility(View.VISIBLE);
            binding.glGarments.setVisibility(View.INVISIBLE);
            binding.ivWearingOutfitPicToday.setVisibility(View.VISIBLE);
        }
    }



    protected void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider.dressmart", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

}