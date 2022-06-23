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

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.dressmart.R;
import com.example.dressmart.WeatherCond;
import com.example.dressmart.databinding.FragmentTodayBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.Headers;


public class TodayFragment extends Fragment {

    private FragmentTodayBinding binding;
    public static final String TAG = "Today Fragment";

    public static final String BASE_URL = "https://api.weatherbit.io/v2.0/forecast/daily?";

    ImageView ivWeatherIconToday;
    TextView tvTempToday;
    TextView tvConditionsToday;
    ImageView ivTopToday;
    ImageView ivBottomsToday;
    ImageView ivOuterToday;
    ImageView ivShoesToday;
    TextView tvTopDescriptionToday;
    TextView tvBottomsDescriptionToday;
    TextView tvOuterDescriptionToday;
    TextView tvShoesDescriptionToday;
    Button btnSubmitToday;



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
        WeatherCond weather = new WeatherCond(getContext());
        weatherFromJson(weather);

        ivWeatherIconToday = binding.ivWeatherIconToday;
        tvTempToday = binding.tvTempToday;
        tvConditionsToday = binding.tvConditionsToday;
        ivTopToday = binding.ivTopToday;
        ivBottomsToday = binding.ivBottomsToday;
        ivOuterToday = binding.ivOuterToday;
        ivShoesToday = binding.ivShoesToday;
        tvTopDescriptionToday = binding.tvTopDescriptionToday;
        tvBottomsDescriptionToday = binding.tvBottomsDescriptionToday;
        tvOuterDescriptionToday = binding.tvOuterDescriptionToday;
        tvShoesDescriptionToday = binding.tvShoesDescriptionToday;
        btnSubmitToday = binding.btnSubmitToday;


        // ******** CHECK WHY API NOT WORKING CORRECTLY
        setConditions(weather);
        tvTempToday.setText(String.valueOf((int)weather.getAvgTemp()));
        // set ivWeatherIconToday using the provided icon from API somehow


    }

    private void weatherFromJson(WeatherCond weather) {
        String paramFahrenheit = "units=I&";
        String paramOneDay = "days=1&";
        String paramCity = "city=Seattle,WA&";
        String secretKey = getActivity().getString(R.string.api_key);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BASE_URL + paramFahrenheit + paramOneDay + paramCity + secretKey, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    weather.populateWeather(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Oops! onFailure");
            }
        });
    }

    private void setConditions(WeatherCond weather) {
        if (weather.getCloudCoveragePercentage() < 25) {
            tvConditionsToday.setText("Sunny");
        } else if (weather.getCloudCoveragePercentage() < 60) {
            tvConditionsToday.setText("Partly Cloudy");
        } else {
            tvConditionsToday.setText("Overcast");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}