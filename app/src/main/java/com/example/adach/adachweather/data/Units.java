package com.example.adach.adachweather.data;

import org.json.JSONObject;

/**
 * Created by Adach on 2017-12-01.
 */

public class Units implements JSONPopulator{
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
    }
}
