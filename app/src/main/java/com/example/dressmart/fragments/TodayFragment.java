package com.example.dressmart.fragments;

import static com.example.dressmart.Constants.BOTTOMS;
import static com.example.dressmart.Constants.OUTER;
import static com.example.dressmart.Constants.SHOES;
import static com.example.dressmart.Constants.TOP;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
import com.parse.FindCallback;
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
    private Location location;
    private int PERMISSION_ID = 44;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */

    private LocationRequest mLocationRequest;

    Context myContext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTodayBinding.inflate(inflater, container, false);
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.pbLoading.setVisibility(ProgressBar.VISIBLE);
        startLocationUpdates();
        //checkIfPostedToday();
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
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "inside of onLocationResult");
                if (locationResult == null) {
                    Toast.makeText(getActivity(), "locationResult is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                location = locationResult.getLastLocation();
                checkIfPostedToday();
            }
        };
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        if (location != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
        //Log.i(TAG, "lat: " + location.getLatitude() + " lon: " + location.getLongitude());
    }


    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.getLastLocation();
            }
        }
    }


    private void fetchWeather(OutfitPost todaysPost, boolean hasClothes) {
        AsyncHttpClient client = new AsyncHttpClient();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.weatherbit.io")
                .appendPath("v2.0")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("units", "I")
                .appendQueryParameter("days", "1")
                .appendQueryParameter("lat", String.valueOf(location.getLatitude()))
                .appendQueryParameter("lon", String.valueOf(location.getLongitude()))
//                .appendQueryParameter("city", "Seattle")
                .appendQueryParameter("key", myContext.getString(R.string.api_key));
        String myUrl = builder.build().toString();
        Log.i(TAG, "url: " + myUrl);
        client.get(myUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                WeatherCondition weatherCondition = null;
                try {
                    weatherCondition = WeatherCondition.weatherFromJson(myContext, json.jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bindWeatherInfo(weatherCondition);
                if(!hasClothes) {
                    bindEmptyUI();
                } else if (todaysPost == null){
                    fetchOutfit(weatherCondition);
                } else {
                    bindDisplayOutfitUI(todaysPost);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Oops! onFailure. " + throwable.getMessage() + response);
            }
        });
    }

    private void setWeatherIcon(WeatherCondition weatherCondition) {
        @DrawableRes int icon;
        if (weatherCondition.getConditions().equals(getString(R.string.condition_sunny))) {
            icon = R.drawable.sunny;
        } else if (weatherCondition.getConditions().equals(myContext.getString(R.string.condition_partly_cloudy))) {
            icon = R.drawable.partly_cloudy;
        } else {
            icon = R.drawable.cloudy;
        }
        Glide.with(getActivity()).load(icon).override(400, 400).into(binding.ivWeatherIconToday);
        binding.ivWeatherIconToday.setVisibility(View.VISIBLE);
    }

    public void bindWeatherInfo(WeatherCondition weatherCondition) {

        if (binding == null || getView() == null) {
            return;
        } else {
            getView().setVisibility(View.VISIBLE);

        }

        // set weather info at the top that will not change
        binding.tvConditionsToday.setVisibility(View.VISIBLE);
        binding.tvConditionsToday.setText(weatherCondition.getConditions());
        binding.tvToday.setVisibility(View.VISIBLE);
        setWeatherIcon(weatherCondition);
        binding.tvTempToday.setVisibility(View.VISIBLE);
        binding.tvTempToday.setText(String.valueOf((int) weatherCondition.getAvgTemp()));
    }

    public void bindRecommendationUI() {
        binding.tvOurPicks.setVisibility(View.VISIBLE);
        binding.tvOurPicks.setText(myContext.getString(R.string.header_our_picks));
        binding.glGarments.setVisibility(View.VISIBLE);
        binding.btnSubmitToday.setVisibility(View.VISIBLE);
    }


    public void fetchOutfit(WeatherCondition weatherCondition) {
        User user = (User) ParseUser.getCurrentUser();
        Log.i(TAG, "in select outfit");
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

                    HashMap<String, List<Garment>> closet = null;

                    closet = createCloset(user);

                    RecommendedOutfit recommendedOutfit = RecommendationUtil.getRecommendation(weatherCondition, closet);
                    attachRecommendedOutfit(recommendedOutfit, closet);
                    bindRecommendationUI();
                    binding.pbLoading.setVisibility(View.INVISIBLE);

                    HashMap<String, List<Garment>> finalCloset = closet;
                    binding.btnSubmitToday.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // prompt camera app to take outfit pic
                            onLaunchCamera(v);
                            OutfitPost post = new OutfitPost();
                            populatePost(weatherCondition, post, finalCloset);

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
                                    bindDisplayOutfitUI(post);

                                    setGarmentsLastWorn(post);
                                    doSubmitAnimation();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(myContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private HashMap<String, List<Garment>> createCloset(User user) {
        HashMap<String, List<Garment>> closet = new HashMap<>();
        for(Garment item : user.getCloset()) {
            String garmentType = "";
            try {
                garmentType = item.getGarmentType();
            } catch (ParseException e){
                e.printStackTrace();
            }
            if(closet.containsKey(garmentType)) {
                //Add to existing list
                closet.get(garmentType).add(item);

            } else {
                //Create new list
                ArrayList<Garment> garments = new ArrayList<Garment>(1);
                garments.add(item);
                closet.put(garmentType, garments);
            }
        }
        return closet;
    }

    private void attachRecommendedOutfit(RecommendedOutfit recommendedOutfit, HashMap<String, List<Garment>> closet) {
        // put the chosen items at the top of their respective lists
        closet.get(TOP).remove(recommendedOutfit.getTop());
        closet.get(TOP).add(0, recommendedOutfit.getTop());
        closet.get(BOTTOMS).remove(recommendedOutfit.getBottoms());
        closet.get(BOTTOMS).add(0, recommendedOutfit.getBottoms());
        if (closet.get(OUTER) != null) {
            closet.get(OUTER).remove(recommendedOutfit.getOuter());
            closet.get(OUTER).add(0, recommendedOutfit.getOuter());
        }
        closet.get(SHOES).remove(recommendedOutfit.getShoes());
        closet.get(SHOES).add(0, recommendedOutfit.getShoes());

        // set the adapters for all 4 garment cards
        GarmentAdapter topAdapter = new GarmentAdapter(closet.get(TOP), myContext);
        GarmentAdapter bottomsAdapter = new GarmentAdapter(closet.get(BOTTOMS), myContext);
        GarmentAdapter outerAdapter = new GarmentAdapter(closet.get(OUTER), myContext);
        GarmentAdapter shoesAdapter = new GarmentAdapter(closet.get(SHOES), myContext);
        binding.vpGarment1.setAdapter(topAdapter);
        binding.vpGarment2.setAdapter(bottomsAdapter);
        binding.vpGarment3.setAdapter(outerAdapter);
        binding.vpGarment4.setAdapter(shoesAdapter);
    }

    private void setGarmentsLastWorn(OutfitPost post) {
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
        Animation animFadeOut = AnimationUtils.loadAnimation(myContext, R.anim.fade_out);
        binding.glGarments.startAnimation(animFadeOut);
        Animation animFadeIn = AnimationUtils.loadAnimation(myContext, R.anim.fade_in);
        binding.ivWearingOutfitPicToday.startAnimation(animFadeIn);
        binding.glGarments.setVisibility(View.INVISIBLE);
        binding.btnSubmitToday.setVisibility(View.INVISIBLE);
        binding.ivWearingOutfitPicToday.setVisibility(View.VISIBLE);
    }

    private void bindDisplayOutfitUI(OutfitPost post) {
        // display the match score and remove the submit button
        binding.pbLoading.setVisibility(View.INVISIBLE);
        Log.i(TAG, "in update ui after postig");
        binding.tvNumStars.setText(myContext.getString(R.string.popup_match_score) + " " + String.valueOf(post.getColorMatchScore()) + "!");
        binding.rbMatchScore.setRating((float) post.getColorMatchScore());
        binding.tvNumStars.setVisibility(View.VISIBLE);
        binding.rbMatchScore.setVisibility(View.VISIBLE);
        binding.ivWearingOutfitPicToday.setVisibility(View.VISIBLE);
        Glide.with(myContext).load(post.getWearingOutfitPicture().getUrl()).transform(new RoundedCorners(50)).into(binding.ivWearingOutfitPicToday);
        binding.tvOurPicks.setText(myContext.getString(R.string.header_your_outfit));
        binding.tvOurPicks.setVisibility(View.VISIBLE);
        binding.btnSubmitToday.setVisibility(View.INVISIBLE);
    }

    private void populatePost(WeatherCondition weatherCondition, OutfitPost post, HashMap<String,List<Garment>> closet) {
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

    private void bindEmptyUI() {
        binding.pbLoading.setVisibility(View.INVISIBLE);
        Log.i(TAG, "in setEmptyUI");
        binding.tvNoItemsYet.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }

    private boolean hasClothes() {
        User user = (User) ParseUser.getCurrentUser();
        if (user == null) {
            return false;
        }
        int numTops = 0, numBottoms = 0, numShoes = 0;
        for (Garment item : user.getCloset()) {
            String garmentType = "";
            try {
                garmentType = item.getGarmentType();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (garmentType.equals(TOP)) {
                numTops++;
            } else if (garmentType.equals(BOTTOMS)) {
                numBottoms++;
            } else if (garmentType.equals(SHOES)) {
                numShoes++;
            }
        }
        return numTops >= 1 && numBottoms >= 1 && numShoes >= 1;
    }

    private void checkIfPostedToday() {
        // query posts and check if the most recent post matches today's date
        ParseQuery<OutfitPost> query = ParseQuery.getQuery(OutfitPost.class);
        query.include(OutfitPost.KEY_AUTHOR);
        query.whereEqualTo(OutfitPost.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<OutfitPost>() {
            @Override
            public void done(List<OutfitPost> fetchedPosts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                boolean hasClothes = hasClothes();
                // save received posts to list and notify adapter of new data
                if (!fetchedPosts.isEmpty() && DateUtils.isToday(fetchedPosts.get(0).getCreatedAt().getTime())) {
                    // already posted
                    fetchWeather(fetchedPosts.get(0), hasClothes);
                } else {
                    // has not posted
                    fetchWeather(null, hasClothes);
                }
            }
        });
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