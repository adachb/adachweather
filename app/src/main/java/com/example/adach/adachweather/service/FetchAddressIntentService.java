package com.example.adach.adachweather.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.example.adach.adachweather.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adach on 2017-12-07.
 */

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddresIS";

    // receiver where results are forwarder from service
    private ResultReceiver mReceiver;

    // constructor
    public FetchAddressIntentService(){
        super(TAG);
    }

    // try to get the location address usign Geocoder
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // check if receiver was properly registered
        if (mReceiver == null){
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results");
            return;
        }

        // get the location passed to this service through an extra
        android.location.Location locatione = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        // check if location was sent through an extra
        if (locatione == null){
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }


    // get location using geocoder

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    // Address found using the Geocoder
    List<Address> addresses = null;

    try{
        // getFromLocation() returns an array of addressess for the area surrounding the
        // given latitutude and longitude. Results are not guaranteed to be accurate

        addresses = geocoder.getFromLocation(
                locatione.getLatitude(),
                locatione.getLongitude(),
                // get 1 address
                1);
    } catch (IOException ioException){
        // catch network or IO problems
        errorMessage = getString(R.string.service_not_available);
        Log.e(TAG, errorMessage, ioException);
    } catch (IllegalArgumentException illegalArgumentException) {
        // catch invalid latitude or longitude values
        errorMessage = getString(R.string.invalid_lat_long_used);
        Log.e(TAG, errorMessage + ". " + "Latitude = " + locatione.getLatitude()
                + ", Longitude = " + locatione.getLongitude(), illegalArgumentException);
    }

    // cases wher no address is found
        if (addresses == null || addresses.size() == 0) {
        if (errorMessage.isEmpty()){
            errorMessage = getString(R.string.no_address_found);
            Log.e(TAG, errorMessage);
        }
        deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine(), other options
            // getLocality() (" Mountain View")
            // getAdminArea() ("CA")
            // getPostalCode() ("94043")
            // getCountryCode() ("US")
            // getCountryName() ("United States")

            /*
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
                addressFragments.add(address.getAddressLine(i));
            }
            */

            String postalCode = address.getPostalCode();
            String city = address.getLocality();
            String adminArea = address.getAdminArea();
            String countryName = address.getCountryName();

            if (postalCode != null){
                addressFragments.add(postalCode + ", " + countryName);
            } else if (adminArea != null && postalCode == null){
                addressFragments.add(adminArea + ", " + countryName);
            } else if (adminArea == null && postalCode == null){
                addressFragments.add(city + ", " + countryName);
            }


            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }

    }
    // send resultCode and message to receiver
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
