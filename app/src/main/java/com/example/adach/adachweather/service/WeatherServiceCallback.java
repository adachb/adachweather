package com.example.adach.adachweather.service;

import com.example.adach.adachweather.data.Channel;

/**
 * Created by Adach on 2017-12-01.
 */

public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
