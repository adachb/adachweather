package com.example.adach.adachweather.data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Adach on 2017-12-01.
 */

public class Item implements JSONPopulator {
    private Condition condition;
    private Condition[] forecast;

    public Condition getCondition() {
        return condition;
    }

    public Condition[] getForecast() {
        return forecast;
    }

    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));

        JSONArray forecastArray = data.optJSONArray("forecast");

        forecast = new Condition[forecastArray.length()];

        for (int i = 0; i < forecastArray.length(); i++){
            forecast[i] = new Condition();
            try {
                forecast[i].populate(forecastArray.getJSONObject(i));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
