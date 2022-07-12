package com.example.dressmart.ui.today;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
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
import androidx.palette.graphics.Palette;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private static boolean hasSubmitted;

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
        binding.rbMatchScore.setVisibility(View.GONE);
        binding.tvNumStars.setVisibility(View.GONE);
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


                    Comparator<Garment> dateComparator = new Comparator<Garment>() {
                        @Override
                        public int compare(Garment g1, Garment g2) {
                            return g1.getDateLastWorn().compareTo(g2.getDateLastWorn());
                        }
                    };
                    // sort each list by ascending date last worn
                    Collections.sort(closet.get("Top"), dateComparator);
                    Collections.sort(closet.get("Bottoms"), dateComparator);
                    Collections.sort(closet.get("Outer"), dateComparator);
                    Collections.sort(closet.get("Shoes"), dateComparator);

                    Garment top = closet.get("Top").get(0);
                    Garment bottoms = closet.get("Bottoms").get(0);
                    Garment outer = null;
                    Garment shoes = closet.get("Shoes").get(0);

                    // each list is sorted from least recently worn to most recently worn,
                    // so the items that will be recommended won't be the most recent items
                    for (Garment item : closet.get("Top")) {
                        if (weatherCondition.getAvgTemp() < 60) {
                            if (item.getSubtype().equals("Long-Sleeved")) {
                                top = item;
                                break;
                            }
                        } else {
                            if (item.getSubtype().equals("Short-Sleeved")) {
                                top = item;
                                break;
                            }
                        }
                    }
                    for (Garment item : closet.get("Bottoms")) {
                        if (weatherCondition.getAvgTemp() < 60) {
                            if (item.getSubtype().equals("Pants")) {
                                bottoms = item;
                                break;
                            }
                        } else if (weatherCondition.getAvgTemp() >= 60) {
                            if (item.getSubtype().equals("Shorts")) {
                                bottoms = item;
                                break;
                            }
                        }
                    }
                    for (Garment item : closet.get("Outer")) {
                        if (weatherCondition.getAvgTemp() < 40) {
                            if (item.getSubtype().equals("Coat")) {
                                outer = item;
                                break;
                            }
                        } else if (weatherCondition.getAvgTemp() < 60) {
                            if (weatherCondition.getWindSpeed() > 15 || weatherCondition.getChanceOfPrecip() > 50) {
                                if (item.getSubtype().equals("Jacket")) {
                                    outer = item;
                                    break;
                                }
                            } else {
                                if (item.getSubtype().equals("Sweater")) {
                                    outer = item;
                                    break;
                                }
                            }
                        }
                    }
                    for (Garment item : closet.get("Shoes")) {
                        if (weatherCondition.getAvgTemp() < 40 || weatherCondition.getChanceOfPrecip() > 70) {
                            if (item.getSubtype().equals("Boots")) {
                                shoes = item;
                                break;
                            }
                        } else if (weatherCondition.getAvgTemp() > 70 && !weatherCondition.getConditions().equals("Overcast")) {
                            if (item.getSubtype().equals("Sandals")) {
                                shoes = item;
                                break;
                            }
                        } else {
                            if (item.getSubtype().equals("Sneakers")) {
                                shoes = item;
                                break;
                            }
                        }
                    }

                    // ************ decide the chosen outfit's "match score" based on each color
                    //binding.vpGarment1.addOn

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
                    GarmentAdapter topAdapter = new GarmentAdapter(closet.get("Top"), getContext());
                    GarmentAdapter bottomsAdapter = new GarmentAdapter(closet.get("Bottoms"), getContext());
                    GarmentAdapter outerAdapter = new GarmentAdapter(closet.get("Outer"), getContext());
                    GarmentAdapter shoesAdapter = new GarmentAdapter(closet.get("Shoes"), getContext());
                    binding.vpGarment1.setAdapter(topAdapter);
                    binding.vpGarment2.setAdapter(bottomsAdapter);
                    binding.vpGarment3.setAdapter(outerAdapter);
                    binding.vpGarment4.setAdapter(shoesAdapter);


                    binding.btnSubmitToday.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // prompt camera app to take outfit pic
                            onLaunchCamera(v);
                            OutfitPost post = new OutfitPost();
                            post.setParseTemperature((int)weatherCondition.getAvgTemp());
                            post.setParseConditions(weatherCondition.getConditions());
                            post.setParseAuthor((User)ParseUser.getCurrentUser());

                            post.setParseWearingOutfitPicture(new ParseFile(photoFile));
                            post.setParseLikedBy(new ArrayList<>());

                            // set the chosen garments to the actual cards the user chose
                            post.setParseTop(closet.get("Top").get(binding.vpGarment1.getCurrentItem()));
                            post.setParseBottoms(closet.get("Bottoms").get(binding.vpGarment2.getCurrentItem()));
                            post.setParseOuter(closet.get("Outer").get(binding.vpGarment3.getCurrentItem()));
                            post.setParseShoes(closet.get("Shoes").get(binding.vpGarment4.getCurrentItem()));

                            // calculate the match score of the 4 garments put together and display it
                            // in the rating stars, the number of stars filled up corresponds to the value of calculateMatchScore
                            // set the visibility of the color match rating stars to VISIBLE and the submit button to GONE
                            try {
                                post.setColorMatchScore(calculateMatchScore(post.getTop(), post.getBottoms(), post.getOuter(), post.getShoes()));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
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

                                    // display the match score and remove the submit button
                                    binding.btnSubmitToday.setVisibility(View.GONE);
                                    binding.tvNumStars.setText("Match Score: " + String.valueOf(post.getColorMatchScore()) + "!");
                                    binding.rbMatchScore.setRating((float) post.getColorMatchScore());
                                    binding.tvNumStars.setVisibility(View.VISIBLE);
                                    binding.rbMatchScore.setVisibility(View.VISIBLE);

                                    // set flag to true
                                    hasSubmitted = true;

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

    private double calculateMatchScore(Garment top, Garment bottoms, Garment outer, Garment shoes) throws ParseException {
        // use Palette library to determine the dominant color in each garment
        Bitmap bitmapTop = BitmapFactory.decodeFile(top.getGarmentPicture().getFile().getPath());
        bitmapTop = scaleCenterCrop(bitmapTop, getSquareCropDimension(bitmapTop), getSquareCropDimension(bitmapTop));
        Bitmap bitmapBottoms = BitmapFactory.decodeFile(bottoms.getGarmentPicture().getFile().getPath());
        bitmapBottoms = scaleCenterCrop(bitmapBottoms, getSquareCropDimension(bitmapBottoms), getSquareCropDimension(bitmapBottoms));
        Bitmap bitmapShoes = BitmapFactory.decodeFile(shoes.getGarmentPicture().getFile().getPath());
        bitmapShoes = scaleCenterCrop(bitmapShoes, getSquareCropDimension(bitmapShoes), getSquareCropDimension(bitmapShoes));

        Palette pTop = Palette.from(bitmapTop).generate();
        Palette pBottoms = Palette.from(bitmapBottoms).generate();
        Palette pShoes = Palette.from(bitmapShoes).generate();

        Palette.Swatch topSwatch = pTop.getVibrantSwatch() == null ? pTop.getMutedSwatch() : pTop.getVibrantSwatch();
        Palette.Swatch bottomsSwatch = pBottoms.getVibrantSwatch() == null ? pBottoms.getMutedSwatch() : pBottoms.getVibrantSwatch();
        Palette.Swatch shoesSwatch = pShoes.getVibrantSwatch() == null ? pShoes.getMutedSwatch() : pShoes.getVibrantSwatch();

        String topColor = getColorFromRgb(topSwatch.getHsl());
        String bottomsColor = getColorFromRgb(bottomsSwatch.getHsl());
        String shoesColor = getColorFromRgb(shoesSwatch.getHsl());

        Log.i(TAG, "top: " + topColor + " bottoms: " + bottomsColor + " shoes: " + shoesColor);
        Log.i(TAG, "bottoms hue: " + bottomsSwatch.getHsl()[0]);

        double score = 2.5;
        // reduce score if:
        // top and bottoms are same color family
        if (topColor.equals(bottomsColor)) {
            score -= 1;
        }
        // top and bottom are both bright colors (clash)
        if (isBright(topSwatch.getHsl()) && isBright(bottomsSwatch.getHsl())) {
            score -= 1;
        }
        // raise score if:
        // top and bottom have both light and dark (contrast)
        if (isLight(topSwatch.getHsl()) && isDark(bottomsSwatch.getHsl())
                || isDark(topSwatch.getHsl()) && isLight(bottomsSwatch.getHsl()))    {
            score += 0.5;
        }
        // top and bottom are complementary colors
        if (areComplementary(topColor, bottomsColor)) {
            score += 1;
        }
        // top matches color of shoes
        if (topColor.equals(shoesColor)) {
            score += 1;
        }
        return score;
    }

    private String getColorFromRgb(float[] hsl) {
        float hue = hsl[0];
        float lightness = hsl[2];
        float saturation = hsl[1];
        if (saturation <= 10) {
            return "gray";
        } else if (hue > 340 || hue <= 10) {
            return "red";
        } else if (hue > 10 && hue <= 40) {
            return "orange";
        } else if (hue > 40 && hue <= 70) {
            return "yellow";
        } else if (hue > 70 && hue <= 160) {
            return "green";
        } else if (hue > 160 && hue <= 260) {
            return "blue";
        } else if (hue > 260 && hue <= 290) {
            return "purple";
        } else if (hue > 290 && hue <= 340){
            return "pink";
        } else if (lightness <= 0.1) {
            return "black";
        } else if (lightness >= 0.95) {
            return "white";
        } else {
            return "";
        }
    }

    private boolean isBright(float[] hsl) {
        float saturation = hsl[1];
        return saturation >= 0.7;
    }

    private boolean isLight(float[] hsl) {
        float lightness = hsl[2];
        return lightness >= 0.75;
    }

    private boolean isDark(float[] hsl) {
        float lightness = hsl[2];
        return lightness <= 25;
    }

    private boolean areComplementary(String color1, String color2) {
        return (color1.equals("yellow") && color2.equals("purple")) || (color1.equals("purple") && color2.equals("yellow"))
                || (color1.equals("orange") && color2.equals("blue")) || (color1.equals("blue") && color2.equals("orange"))
                || (color1.equals("green") && color2.equals("red")) || (color1.equals("red") && color2.equals("green"))
                || (color1.equals("black") && color2.equals("white")) || (color1.equals("white") && color2.equals("black"));
    }

    private static Bitmap scaleCenterCrop(Bitmap source, int newHeight,
                                          int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top
                + scaledHeight);//from ww w  .j a va 2s. co m

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
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
        }
    }

    public int getSquareCropDimension(Bitmap bitmap)
    {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
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