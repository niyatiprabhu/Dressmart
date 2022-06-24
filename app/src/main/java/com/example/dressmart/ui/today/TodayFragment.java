package com.example.dressmart.ui.today;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.dressmart.MainActivity;
import com.example.dressmart.R;
import com.example.dressmart.models.WeatherCondition;
import com.example.dressmart.databinding.FragmentTodayBinding;
import com.example.dressmart.models.parse.Garment;
import com.example.dressmart.models.parse.OutfitPost;
import com.example.dressmart.models.parse.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class TodayFragment extends Fragment {

    private FragmentTodayBinding binding;
    public static final String TAG = "Today Fragment";

    public static final String BASE_URL = "https://api.weatherbit.io/v2.0/forecast/daily?";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public File photoFile;
    public String photoFileName = "photo.jpg";


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
//        if (weather == null) {
        weatherFromJson();
//        }


    }

    private void weatherFromJson() {
        String paramFahrenheit = "units=I&";
        String paramOneDay = "days=1&";
        String paramCity = "city=Seattle,WA&";
        String secretKey = "key=" + getActivity().getString(R.string.api_key);

        Log.i(TAG, BASE_URL + paramFahrenheit + paramOneDay + paramCity + secretKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BASE_URL + paramFahrenheit + paramOneDay + paramCity + secretKey, new JsonHttpResponseHandler() {
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
            Glide.with(getContext()).load(R.drawable.sun_icon).override(400, 400).into(binding.ivWeatherIconToday);
        } else if (weatherCondition.getConditions().equals("Partly Cloudy")) {
            Glide.with(getContext()).load(R.drawable.partly_cloudy).override(400, 400).into(binding.ivWeatherIconToday);

        } else {
            Glide.with(getContext()).load(R.drawable.cloudy_weather).override(400, 400).into(binding.ivWeatherIconToday);

        }
    }

    public void bind() {
        binding.tvConditionsToday.setText(weatherCondition.getConditions());
        setWeatherIcon();
        binding.tvTempToday.setText(String.valueOf((int) weatherCondition.getAvgTemp()));

        // set ivWeatherIconToday using the provided icon from API somehow



    }

    public void parseJson(JsonHttpResponseHandler.JSON json) {
        JSONObject jsonObject = json.jsonObject;
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
        Garment top = null, bottoms = null, outer = null, shoes = null;
        for (Garment item : user.getCloset()) {
            if (item.getGarmentType().equals("Top")) {
                top = item;
            } else if (item.getGarmentType().equals("Bottoms")) {
                bottoms = item;
            } else if (item.getGarmentType().equals("Outer")) {
                outer = item;
            } else {
                shoes = item;
            }
        }

        // add the garments to a list to associate with the post that is created
        List<Garment> outfitGarments = new ArrayList<>();
        outfitGarments.add(top);
        outfitGarments.add(bottoms);
        outfitGarments.add(outer);
        outfitGarments.add(shoes);

        // bind the garments to the UI
        Glide.with(getContext()).load(top.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivTopToday);
        Glide.with(getContext()).load(bottoms.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivBottomsToday);
        Glide.with(getContext()).load(outer.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivOuterToday);
        Glide.with(getContext()).load(shoes.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivShoesToday);
        binding.tvTopDescriptionToday.setText(top.getDescription());
        binding.tvBottomsDescriptionToday.setText(bottoms.getDescription());
        binding.tvOuterDescriptionToday.setText(outer.getDescription());
        binding.tvShoesDescriptionToday.setText(shoes.getDescription());

        binding.btnSubmitToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prompt camera app to take outfit pic
                onLaunchCamera(v);
                OutfitPost post = new OutfitPost();
                post.setTemperature((int)weatherCondition.getAvgTemp());
                post.setConditions(weatherCondition.getConditions());
                post.setAuthor((User)ParseUser.getCurrentUser());
                post.setGarments(outfitGarments);
                post.setWearingOutfitPicture(new ParseFile(photoFile));
                post.setLikedBy(new ArrayList<>());
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with saving post", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i(TAG, "Post save was successful!");
                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        // ***** WANT TO GO BACK TO HOME FRAGMENT AFTER SUBMITTING PHOTO
                        //mainActivity.goToFeedFrag();
                    }
                });
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
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.dressmart", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


}