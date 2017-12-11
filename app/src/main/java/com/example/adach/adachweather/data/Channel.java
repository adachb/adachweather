package com.example.adach.adachweather.data;

import org.json.JSONObject;

/**
 * Created by Adach on 2017-12-01.
 */

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;
    private Location location;

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        units.populate(data.optJSONObject("units"));

        item = new Item();
        item.populate(data.optJSONObject("item"));

        location = new Location();
        location.populate(data.optJSONObject("location"));
    }
}
