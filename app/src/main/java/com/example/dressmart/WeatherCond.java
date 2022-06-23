package com.example.dressmart;


import android.content.Context;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class WeatherCond {


    public static final String TAG = "WeatherCond";

    Context context;


    private double avgTemp;
    private double chanceOfPrecip;
    private double windSpeed;
    private double cloudCoveragePercentage;

    public WeatherCond(Context context){
        this.context = context;
    }

    public void populateWeather(JSONObject jsonObject) throws JSONException {
        double maxTemp = jsonObject.getDouble("max_temp");
        double minTemp = jsonObject.getDouble("min_temp");
        avgTemp = (maxTemp + minTemp) / 2;
        chanceOfPrecip = jsonObject.getDouble("pop");
        windSpeed = jsonObject.getDouble("wind_spd");
        cloudCoveragePercentage = jsonObject.getDouble("clouds");
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
