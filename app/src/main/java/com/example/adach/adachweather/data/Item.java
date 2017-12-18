package com.example.adach.adachweather.data;

/**
 * Created by Adach on 2017-12-01.
 */

public class Item{
    private Condition condition;
    private Condition[] forecast;

    public Condition getCondition() {
        return condition;
    }

    public Condition[] getForecast() {
        return forecast;
    }
}
