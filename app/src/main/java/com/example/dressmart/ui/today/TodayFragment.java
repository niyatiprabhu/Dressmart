package com.example.dressmart.ui.today;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.dressmart.R;
import com.example.dressmart.adapters.GarmentAdapter;
import com.example.dressmart.models.WeatherCondition;
import com.example.dressmart.databinding.FragmentTodayBinding;
import com.example.dressmart.models.parse.Garment;
import com.example.dressmart.models.parse.OutfitPost;
import com.example.dressmart.models.parse.User;
import com.example.dressmart.ui.feed.FeedFragment;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;


public class TodayFragment extends Fragment {

    private FragmentTodayBinding binding;
    public static final String TAG = "Today Fragment";

    public static final String BASE_URL = "https://api.weatherbit.io/v2.0/forecast/daily?";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public File photoFile;
    public String photoFileName = "photo.jpg";

    FusedLocationProviderClient mFusedLocationClient;
    private double latitude;
    private double longitude;
    int PERMISSION_ID = 44;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */

    private LocationRequest mLocationRequest;


    WeatherCondition weatherCondition;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TodayViewModel dashboardViewModel =
                new ViewModelProvider(this).get(TodayViewModel.class);

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
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
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
                        //onLocationChanged(locationResult.getLastLocation());
                        Location location = locationResult.getLastLocation();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                },
                Looper.myLooper());
    }



    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
            }
        }
    }


    private void weatherFromJson() {
        String paramFahrenheit = "units=I&";
        String paramOneDay = "days=1&";
        String paramLatLong = "lat=" + latitude + "&" + "lon=" + longitude + "&";
        String secretKey = "key=" + getActivity().getString(R.string.api_key);

        Log.i(TAG, paramLatLong);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BASE_URL + paramFahrenheit + paramOneDay + paramLatLong + secretKey, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                parseJson(json);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Oops! onFailure. " + throwable.getMessage());
            }
        });
    }

    private void setWeatherIcon() {
        if (weatherCondition.getConditions().equals("Sunny")) {
            Glide.with(getActivity()).load(R.drawable.sun_icon).override(400, 400).into(binding.ivWeatherIconToday);
        } else if (weatherCondition.getConditions().equals("Partly Cloudy")) {
            Glide.with(getActivity()).load(R.drawable.partly_cloudy).override(400, 400).into(binding.ivWeatherIconToday);

        } else {
            Glide.with(getActivity()).load(R.drawable.cloudy_weather).override(400, 400).into(binding.ivWeatherIconToday);

        }
    }

    public void bind() {
        binding.tvConditionsToday.setText(weatherCondition.getConditions());
        setWeatherIcon();
        binding.tvTempToday.setText(String.valueOf((int) weatherCondition.getAvgTemp()));

        // set ivWeatherIconToday using the provided icon from API somehow
    }

    public void parseJson(JsonHttpResponseHandler.JSON json) {
        try {
            weatherCondition = WeatherCondition.weatherFromJson(json.jsonObject);
            bind();
            selectOutfit();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
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
                    getView().setVisibility(View.VISIBLE);


                    HashMap<String, ArrayList<Garment>> closet = new HashMap<>();
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
                    Garment top = closet.get("Top").get(0);
                    Garment bottoms = closet.get("Bottoms").get(0);
                    Garment outer = null;
                    Garment shoes = closet.get("Shoes").get(0);

                    for (Garment item : closet.get("Top")) {
                        if (weatherCondition.getAvgTemp() < 60) {
                            if (item.getSubtype().equals("Long-Sleeved")) {
                                top = item;
                            }
                        } else {
                            if (item.getSubtype().equals("Short-Sleeved")) {
                                top = item;
                            }
                        }
                    }
                    for (Garment item : closet.get("Bottoms")) {
                        if (weatherCondition.getAvgTemp() < 60) {
                            if (item.getSubtype().equals("Pants")) {
                                bottoms = item;
                            }
                        } else if (weatherCondition.getAvgTemp() >= 60) {
                            if (item.getSubtype().equals("Shorts")) {
                                bottoms = item;
                            }
                        }
                    }
                    for (Garment item : closet.get("Outer")) {
                        if (weatherCondition.getAvgTemp() < 40) {
                            if (item.getSubtype().equals("Coat")) {
                                outer = item;
                            }
                        } else if (weatherCondition.getAvgTemp() < 60) {
                            if (weatherCondition.getWindSpeed() > 15 || weatherCondition.getChanceOfPrecip() > 50) {
                                if (item.getSubtype().equals("Jacket")) {
                                    outer = item;
                                }
                            } else {
                                if (item.getSubtype().equals("Sweater")) {
                                    outer = item;
                                }
                            }
                        }
                    }
                    for (Garment item : closet.get("Shoes")) {
                        if (weatherCondition.getAvgTemp() < 40 || weatherCondition.getChanceOfPrecip() > 70) {
                            if (item.getSubtype().equals("Boots")) {
                                shoes = item;
                            }
                        } else if (weatherCondition.getAvgTemp() > 70 && !weatherCondition.getConditions().equals("Overcast")) {
                            if (item.getSubtype().equals("Sandals")) {
                                shoes = item;
                            }
                        } else {
                            if (item.getSubtype().equals("Sneakers")) {
                                shoes = item;
                            }
                        }
                    }

                    // put the chosen items at the top of their respective lists
                    closet.get("Top").remove(top);
                    closet.get("Top").add(0, top);
                    closet.get("Bottoms").remove(bottoms);
                    closet.get("Bottoms").add(0, bottoms);
                    closet.get("Outer").remove(outer);
                    closet.get("Outer").add(0, outer);
                    closet.get("Shoes").remove(shoes);
                    closet.get("Shoes").add(0, shoes);

                    // set the adapters for all 4 garment cards
                    binding.vpGarment1.setAdapter(new GarmentAdapter(closet.get("Top"), getContext()));
                    binding.vpGarment2.setAdapter(new GarmentAdapter(closet.get("Bottoms"), getContext()));
                    binding.vpGarment3.setAdapter(new GarmentAdapter(closet.get("Outer"), getContext()));
                    binding.vpGarment4.setAdapter(new GarmentAdapter(closet.get("Shoes"), getContext()));



                    // add the garments to a list to associate with the post that is created
                    List<Garment> outfitGarments = new ArrayList<>();
                    outfitGarments.add(top);
                    outfitGarments.add(bottoms);
                    outfitGarments.add(outer);
                    outfitGarments.add(shoes);

                    binding.btnSubmitToday.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // prompt camera app to take outfit pic
                            onLaunchCamera(v);
                            OutfitPost post = new OutfitPost();
                            post.setParseTemperature((int)weatherCondition.getAvgTemp());
                            post.setParseConditions(weatherCondition.getConditions());
                            post.setParseAuthor((User)ParseUser.getCurrentUser());
                            post.setParseGarments(outfitGarments);
                            post.setParseWearingOutfitPicture(new ParseFile(photoFile));
                            post.setParseLikedBy(new ArrayList<>());
                            post.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "Issue with saving post", e);
                                        //Toast.makeText(getActivity(), "Error while saving!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    user.addParseOutfit(post);
                                    user.saveInBackground();
                                    Log.i(TAG, "Post save was successful!");
                                    // navigate back to the feed fragment after successful post
//                                    Fragment fragment = new FeedFragment();
//                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, fragment);
//                                    fragmentTransaction.addToBackStack(null);
//                                    fragmentTransaction.commit();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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