package com.example.adach.adachweather.data;

import org.json.JSONObject;

/**
 * Created by Adach on 2017-12-01.
 */

public class Condition implements JSONPopulator{
    private int code;
    private int temperature;
    private String description;
    private String date;
    private String day;
    private int high;
    private int low;


    public int getCode() {
        return code;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public int getHigh() {
        return high;
    }

    public int getLow() {
        return low;
    }

    @Override
    public void populate(JSONObject data) {
        code = data.optInt("code");
        temperature = data.optInt("temp");
        description = data.optString("text");
        date = data.optString("date");
        day = data.optString("day");
        high = data.optInt("high");
        low = data.optInt("low");

    }
}
