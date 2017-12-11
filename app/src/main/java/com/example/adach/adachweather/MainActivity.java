package com.example.adach.adachweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adach.adachweather.data.Channel;
import com.example.adach.adachweather.data.Condition;
import com.example.adach.adachweather.data.Item;
import com.example.adach.adachweather.service.WeatherServiceCallback;
import com.example.adach.adachweather.service.YahooWeatherService;

public class MainActivity extends AppCompatActivity implements WeatherServiceCallback {
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private TextView test;
    private String loc; //= "Warsaw, Poland";
    private RelativeLayout myLayout;
    private final String LOC = "location";
    private final String LOC_FROM_YAHOO = "locationfromYahoo";
    private RelativeLayout mainLayout;
    private RelativeLayout forecastLayout;
    private TextView forecastTextView0;
    private ImageView forecastImageView0;
    private TextView forecastTextView1;
    private ImageView forecastImageView1;
    private TextView forecastTextView2;
    private ImageView forecastImageView2;
    private TextView forecastTextView3;
    private ImageView forecastImageView3;
    private TextView forecastTextView4;
    private ImageView forecastImageView4;
    private TextView forecastTextView5;
    private ImageView forecastImageView5;
    private TextView forecastTextView6;
    private ImageView forecastImageView6;
    private TextView forecastTextView7;
    private ImageView forecastImageView7;
    private TextView forecastTextView8;
    private ImageView forecastImageView8;
    private TextView forecastTextView9;
    private ImageView forecastImageView9;
    private String changedLocation;;

    private SharedPreferences prefs;
    private String sharedPrefsFile = "com.example.adach.adachweatherprefs";

    private YahooWeatherService service;
    private ProgressDialog dialog;

    // notification stuff
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    //private NotificationReceiver mReceiver = new NotificationReceiver();
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.adach.adachweather.ACTION_UPDATE_NOTIFICATION";
    private Channel notifyChannel;
    private static final String NOTIFICATION_BUTTON ="notification-button-status";
    private boolean notificationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherIconImageView = (ImageView) findViewById(R.id.today_image);
        temperatureTextView = (TextView) findViewById(R.id.today_temp);
        conditionTextView = (TextView) findViewById(R.id.today_conditions);
        locationTextView = (TextView) findViewById(R.id.location);
        test = (TextView) findViewById(R.id.test);
        myLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        forecastLayout = (RelativeLayout) findViewById(R.id.forecastLayout);
        forecastTextView0 = (TextView) findViewById(R.id.forecast_0);
        forecastImageView0 = (ImageView) findViewById(R.id.forecast_0i);
        forecastTextView1 = (TextView) findViewById(R.id.forecast_1);
        forecastImageView1 = (ImageView) findViewById(R.id.forecast_1i);
        forecastTextView2 = (TextView) findViewById(R.id.forecast_2);
        forecastImageView2 = (ImageView) findViewById(R.id.forecast_2i);
        forecastTextView3 = (TextView) findViewById(R.id.forecast_3);
        forecastImageView3 = (ImageView) findViewById(R.id.forecast_3i);
        forecastTextView4 = (TextView) findViewById(R.id.forecast_4);
        forecastImageView4 = (ImageView) findViewById(R.id.forecast_4i);
        forecastTextView5 = (TextView) findViewById(R.id.forecast_5);
        forecastImageView5 = (ImageView) findViewById(R.id.forecast_5i);
        forecastTextView6 = (TextView) findViewById(R.id.forecast_6);
        forecastImageView6 = (ImageView) findViewById(R.id.forecast_6i);
        forecastTextView7 = (TextView) findViewById(R.id.forecast_7);
        forecastImageView7 = (ImageView) findViewById(R.id.forecast_7i);
        forecastTextView8 = (TextView) findViewById(R.id.forecast_8);
        forecastImageView8 = (ImageView) findViewById(R.id.forecast_8i);
        forecastTextView9 = (TextView) findViewById(R.id.forecast_9);
        forecastImageView9 = (ImageView) findViewById(R.id.forecast_9i);

        prefs = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        loc = prefs.getString(LOC, "Warsaw, Poland");

        service = new YahooWeatherService(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationStatus = prefs.getBoolean(NOTIFICATION_BUTTON, true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        service.refreshWeather(loc);
    }


    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putString(LOC, loc);
        preferencesEditor.putString(LOC_FROM_YAHOO, locationTextView.getText().toString());
        preferencesEditor.apply();
    }

    @Override
    public void serviceSuccess(Channel channel) {
        // pass value to notification
        notifyChannel = channel;
        dialog.hide();

        TextView[] forecastTexts = new TextView[]{forecastTextView0, forecastTextView1,
                forecastTextView2, forecastTextView3, forecastTextView4, forecastTextView5,
                forecastTextView6, forecastTextView7, forecastTextView8, forecastTextView9};

        ImageView[] forecastImages = new ImageView[]{forecastImageView0, forecastImageView1,
                forecastImageView2, forecastImageView3, forecastImageView4, forecastImageView5,
                forecastImageView6, forecastImageView7, forecastImageView8, forecastImageView9};

        Item item = channel.getItem();
        int resourceId = getResources().getIdentifier("drawable/icon_"
                + item.getCondition().getCode(), null, getPackageName());
        Condition[] forecast = channel.getItem().getForecast();

        @SuppressWarnings("deprecation")
        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);

        weatherIconImageView.setImageDrawable(weatherIconDrawable);

        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0"
                + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(channel.getLocation().getCity() + ", "
                + channel.getLocation().getRegion());

        for (int i = 0; i < forecast.length; i++){
            //Condition cond = forecast[i];
            forecastTexts[i].append(forecast[i].getDay() + ", " +  forecast[i].getDate() + "\n"
            + forecast[i].getDescription() + ": " + forecast[i].getHigh() + "\u00B0" +
                    channel.getUnits().getTemperature() + " (" + forecast[i].getLow()  + "\u00B0"
                    + channel.getUnits().getTemperature() + ")" + "\n");

            int forecastResourceId = getResources().getIdentifier("drawable/icon_" +
                    forecast[i].getCode() + "_small", null, getPackageName());
            @SuppressWarnings("deprecation")
            Drawable forecastIconDrawable= getResources().getDrawable(forecastResourceId);
            forecastImages[i].setImageDrawable(forecastIconDrawable);
        }

        forecastLayout.setVisibility(View.VISIBLE);

        if (notificationStatus) {
            sendNotification();
        } else if (notificationStatus == false) {
            cancelNotification();
        }

    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        cancelNotification();
    }



    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void sendNotification(){
        // get weather data from channel
        Item item = notifyChannel.getItem();
        // get temperature and generate id for temperature icon
        int notifyTempResourceId = getResources().getIdentifier("drawable/icon_temp_0",
                null, getPackageName());
        if (item.getCondition().getTemperature() >= 0) {
            notifyTempResourceId = getResources().getIdentifier("drawable/icon_temp_" +
                    item.getCondition().getTemperature(), null, getPackageName());
        } else {
            notifyTempResourceId = getResources().getIdentifier("drawable/icon_temp_m" +
                    item.getCondition().getTemperature(), null, getPackageName());
        }
        // get code for conditions and generate id for weather icon
        int weatherIconResourceId = getResources().getIdentifier("drawable/icon_" +
                item.getCondition().getCode() + "_small", null, getPackageName());
        Bitmap icon2 = BitmapFactory.decodeResource(getResources(), weatherIconResourceId);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent to open app when user clicks on notification
        PendingIntent contentIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(notifyChannel.getLocation().getCity())
                .setContentText(item.getCondition().getTemperature() + "\u00B0"
                        + notifyChannel.getUnits().getTemperature() + " - "
                        + item.getCondition().getDescription())
                .setSmallIcon(notifyTempResourceId)
                .setLargeIcon(icon2)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);


        Notification myNotification = notifyBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, myNotification);
    }

    public void cancelNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
    }


}
