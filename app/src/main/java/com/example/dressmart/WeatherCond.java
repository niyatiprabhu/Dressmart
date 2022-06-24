package com.example.dressmart;


import android.content.Context;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class WeatherCond {


    public static final String TAG = "WeatherCond";



    private double avgTemp;
    private double chanceOfPrecip;
    private double windSpeed;
    private double cloudCoveragePercentage;

    public WeatherCond(){}

    public void populateWeather(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
        JSONObject weather = data.getJSONObject("weather");
        double highTemp = data.getDouble("high_temp");
        double lowTemp = data.getDouble("low_temp");
        avgTemp = (highTemp + lowTemp) / 2;
        chanceOfPrecip = data.getDouble("pop");
        windSpeed = data.getDouble("wind_spd");
        cloudCoveragePercentage = data.getDouble("clouds");
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public double getChanceOfPrecip() {
        return chanceOfPrecip;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getCloudCoveragePercentage() {
        return cloudCoveragePercentage;
    }


}
