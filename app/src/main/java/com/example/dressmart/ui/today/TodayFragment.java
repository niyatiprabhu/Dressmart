package com.example.dressmart.ui.today;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.dressmart.R;
import com.example.dressmart.WeatherCond;
import com.example.dressmart.databinding.FragmentTodayBinding;
import com.example.dressmart.models.Garment;
import com.example.dressmart.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.Headers;


public class TodayFragment extends Fragment {

    private FragmentTodayBinding binding;
    public static final String TAG = "Today Fragment";

    public static final String BASE_URL = "https://api.weatherbit.io/v2.0/forecast/daily?";

    WeatherCond weather;



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
            weather = new WeatherCond();
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

    private void setConditionsAndIcon() {
        if (weather.getCloudCoveragePercentage() < 25) {
            binding.tvConditionsToday.setText("Sunny");
            Glide.with(getContext()).load(R.drawable.sun_icon).override(400, 400).into(binding.ivWeatherIconToday);
        } else if (weather.getCloudCoveragePercentage() < 60) {
            binding.tvConditionsToday.setText("Partly Cloudy");
            Glide.with(getContext()).load(R.drawable.partly_cloudy).override(400, 400).into(binding.ivWeatherIconToday);

        } else {
            binding.tvConditionsToday.setText("Overcast");
            Glide.with(getContext()).load(R.drawable.cloudy_weather).override(400, 400).into(binding.ivWeatherIconToday);

        }
    }

    public void bind() {
        setConditionsAndIcon();
        binding.tvTempToday.setText(String.valueOf((int)weather.getAvgTemp()));

        // set ivWeatherIconToday using the provided icon from API somehow

    }

    public void parseJson(JsonHttpResponseHandler.JSON json) {
        JSONObject jsonObject = json.jsonObject;
        try {
            weather.populateWeather(jsonObject);
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
        Glide.with(getContext()).load(top.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivTopToday);
        Glide.with(getContext()).load(bottoms.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivBottomsToday);
        Glide.with(getContext()).load(outer.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivOuterToday);
        Glide.with(getContext()).load(shoes.getGarmentPicture().getUrl()).override(450, 350).into(binding.ivShoesToday);
        binding.tvTopDescriptionToday.setText(top.getDescription());
        binding.tvBottomsDescriptionToday.setText(bottoms.getDescription());
        binding.tvOuterDescriptionToday.setText(outer.getDescription());
        binding.tvShoesDescriptionToday.setText(shoes.getDescription());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    [
    {
        "__type": "Pointer",
            "className": "Garment",
            "objectId": "91A38r8nVl"
    },
    {
        "__type": "Pointer",
            "className": "Garment",
            "objectId": "87C9hC0AuW"
    },
    {
        "__type": "Pointer",
            "className": "Garment",
            "objectId": "GwCRqChAv4"
    },
    {
        "__type": "Pointer",
            "className": "Garment",
            "objectId": "F7xCP4zmjY"
    }
]
}