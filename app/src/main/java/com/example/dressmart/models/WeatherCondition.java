package com.example.dressmart.models;


import com.bumptech.glide.Glide;
import com.example.dressmart.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to model today's weather based on weatherbit API data
 */
public class WeatherCondition {


    private static final String TAG = "WeatherCond";


    private double avgTemp;
    private double chanceOfPrecip;
    private double windSpeed;
    private double cloudCoveragePercentage;

    public static WeatherCondition weatherFromJson(JSONObject jsonObject) throws JSONException {
        WeatherCondition weatherCond = new WeatherCondition();
        JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
        JSONObject weather = data.getJSONObject("weather");
        double highTemp = data.getDouble("high_temp");
        double lowTemp = data.getDouble("low_temp");
        weatherCond.avgTemp = (highTemp + lowTemp) / 2;
        weatherCond.chanceOfPrecip = data.getDouble("pop");
        weatherCond.windSpeed = data.getDouble("wind_spd");
        weatherCond.cloudCoveragePercentage = data.getDouble("clouds");
        return weatherCond;
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

    public String getConditions() {
        if (getCloudCoveragePercentage() < 25) {
            return "Sunny";
        } else if (getCloudCoveragePercentage() < 60) {
            return "Partly Cloudy";
        } else {
            return "Overcast";
        }
    }


}
