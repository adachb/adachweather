package com.example.adach.adachweather.service;

import com.example.adach.adachweather.data.Weather;

/**
 * Created by Adach on 2017-12-01.
 */

public interface WeatherServiceCallback {
    void serviceSuccess(Weather weather);
    void serviceFailure(Exception exception);
}
