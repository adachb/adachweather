package com.example.adach.adachweather.service;

import com.example.adach.adachweather.data.Weather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Adach on 2017-12-13.
 */

public interface ApiClient {
    @GET
    Observable<Weather> getWeather(@Url String url);

}
