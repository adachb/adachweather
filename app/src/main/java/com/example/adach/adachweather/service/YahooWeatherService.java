package com.example.adach.adachweather.service;

import android.util.Log;

import com.example.adach.adachweather.MainActivity;
import com.example.adach.adachweather.data.Weather;
import com.google.android.gms.common.api.Api;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Adach on 2017-12-01.
 */

public class YahooWeatherService {
    private WeatherServiceCallback callback;
    private String location;
    private Exception error;
    private static final String TAG = MainActivity.class.getSimpleName();

    public YahooWeatherService(WeatherServiceCallback callback) {
        this.callback = callback;
    }

    public String getLocation() {
        return location;
    }

    public void refreshWeather(final String location) {
        String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u=\"c\"", location);

        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .baseUrl("https://query.yahooapis.com/v1/public/")
                .build();

        ApiClient api = retrofit.create(ApiClient.class);
        //api.getWeather(String.format("yql?q=%s&format=json", YQL));

        final Observable<Weather> call = api.getWeather(String.format("yql?q=%s&format=json", YQL));
        Disposable disposable = call
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Weather>(){
                    @Override
                    public void onNext(Weather value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                        Weather a = call.lastElement().blockingGet();
                        if (a == null && error != null) {
                            callback.serviceFailure(error);
                        } else {
                            callback.serviceSuccess(a);
                        }
                    }
                });

    }

    public class LocationWeatherException extends Exception {
        public LocationWeatherException(String detailMessage){
            super(detailMessage);
        }
    }
}
