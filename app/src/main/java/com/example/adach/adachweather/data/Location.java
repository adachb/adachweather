package com.example.adach.adachweather.data;

import org.json.JSONObject;

/**
 * Created by Adach on 2017-12-04.
 */

public class Location implements JSONPopulator {
    private String city;
    private String region;

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public void populate(JSONObject data) {
        city = data.optString("city");
        region = data.optString("region");
    }
}
