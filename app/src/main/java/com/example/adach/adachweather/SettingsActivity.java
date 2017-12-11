package com.example.adach.adachweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adach.adachweather.service.FetchAddressIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class SettingsActivity extends AppCompatActivity {

    private boolean editLoc;
    private Button editLocationButton;
    private EditText changerEditText;
    private Button changerButton;
    private Button mChangerButtonGPS;
    private TextView changerTextView2;
    private TextView changerTextView3;
    // the formatted location address
    private String mAddressOutput;
    private static final int SUCCESS_RESULT = 0;
    // tracks whether the user has requested an address
    private boolean mAddressRequested;
    // provide access to the Fused Location Provider API
    private FusedLocationProviderClient mFusedLocationClient;
    // represent a geographical location
    private Location mLastLocation;
    // receiver registered with this activity to get the response from FetchAddressIntentService
    private AddressResultReceiver mResultReceiver;
    // Kicks off the request to fetch an address when pressed
    private Button mFetchAddressButton;
    // displays the location address
    private TextView gpsTextView;
    // visible while the address is being fetched
    private ProgressBar mProgressBar;



    // constants for manual location
    private static final int REQUEST_PERMISSION_REQUEST_CODE = 34;
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private final String LOC = "location";
    private final String LOC_FROM_YAHOO = "locationfromYahoo";

    //constants for gps location
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final String PACKAGE_NAME = "com.example.adach.adachweather";
    private static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    private static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    private static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    // buttons to change notification status
    private TextView notificationOnTextView;
    private TextView notificationOffTextView;
    private static final String NOTIFICATION_BUTTON ="notification-button-status";
    private boolean notificationStatus;

    // sharedPreferences to hold values after closeing app
    private SharedPreferences prefs;
    private String sharedPrefsFile = "com.example.adach.adachweatherprefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editLoc = false;
        changerTextView2 = (TextView) findViewById(R.id.changeTextView2);
        changerTextView3 = (TextView) findViewById(R.id.changeTextView3);

        // initialize stuff to change location manualy
        editLocationButton = (Button) findViewById(R.id.editlocationButton);
        changerEditText = (EditText) findViewById(R.id.changerEditText);
        changerButton = (Button) findViewById(R.id.changerButton);
        // initialize stuff to get localization from gps
        mResultReceiver = new AddressResultReceiver(new Handler());
        gpsTextView = (TextView) findViewById(R.id.gpsTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.settingsProgressBar);
        mFetchAddressButton = (Button) findViewById(R.id.gpsButton);
        mChangerButtonGPS = (Button) findViewById(R.id.changerButtonGPS);
        // stuff to change notifiaction status
        notificationOnTextView = (TextView) findViewById(R.id.notificationOn);
        notificationOffTextView = (TextView) findViewById(R.id.notificationOff);
        //notificationToggleButton = (ToggleButton) findViewById(R.id.notificationToggleButton);

        prefs = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);

        changerEditText.setHint(prefs.getString(LOC_FROM_YAHOO, "Warsaw, Poland"));

        // localization from gps - set defaults, then update using values stored in the Bundle
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        updateUIWidgets();
        mFetchAddressButton.setEnabled(false);


        if (prefs.getBoolean(NOTIFICATION_BUTTON, true)){
            notificationOnTextView.setEnabled(false);
            notificationOffTextView.setEnabled(true);
        } else {
            notificationOnTextView.setEnabled(true);
            notificationOffTextView.setEnabled(false);
        }


    }

    // onClick method to enable methods to change location
    public void editLocation(View view) {

        if (editLoc == false) {
            changerEditText.setEnabled(true);
            changerButton.setEnabled(true);
            changerTextView2.setTextColor(Color.parseColor("#000000"));
            changerTextView3.setTextColor(Color.parseColor("#000000"));
            //mFetchAddressButton.setEnabled(true);
            updateUIWidgets();

            if (!checkPermissions()) {
                requestPermissions();
            } else {
                getAddress();
            }
            editLoc = true;
        } else {
            changerEditText.setEnabled(false);
            changerButton.setEnabled(false);
            mChangerButtonGPS.setEnabled(false);
            changerTextView2.setTextColor(Color.parseColor("#808080"));
            changerTextView3.setTextColor(Color.parseColor("#808080"));
            mFetchAddressButton.setEnabled(false);
            editLoc = false;
        }
    }

    // update fields based on data stored in the bundle
    private void updateValuesFromBundle(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            // check savedInstanceState if the address was previously requested
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    // onClick method to manually change location
    public void changeLocation(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putString(LOC, changerEditText.getText().toString());
        preferencesEditor.apply();
        startActivity(intent);
        finish();

    }

    // onClick method to change location useing GPS sensor
    public void changeLocationGPS(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putString(LOC, gpsTextView.getText().toString());
        preferencesEditor.apply();
        startActivity(intent);
        finish();

    }

    // runs when user clicks the fetch address button
    @SuppressWarnings("unused")
    public void getGPSLocation(View view) {
        if (mLastLocation != null) {
            startIntentService();
            return;
        }
        // if we have not retrieved the user location yet, we process the user's request by setting
        // mAddressRequested to true, As far as the user is concerned, pressing the find my location
        // button immediately kicks off the process of getting the address
        mAddressRequested = true;
        updateUIWidgets();
    }

    // creates an intent, adds location data to it as an extra, and starts the intent service
    // for fetching an address
    private void startIntentService() {
        // create an intent for passing to the intent service responsible for fetching the address
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // pass the result receiver as an extra to the service
        intent.putExtra(RECEIVER, mResultReceiver);

        // pass the location data as an extra to the service
        intent.putExtra(LOCATION_DATA_EXTRA, mLastLocation);

        // start the service
        startService(intent);
    }

    // get the address for the last known location
    @SuppressWarnings("MissingPermission")
    private void getAddress(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }

                        mLastLocation = location;

                        // Determine whether a Geocoder is available
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // if the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location
                        if (mAddressRequested) {
                            startIntentService();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    // updates the address in the UI

    private void displayAddressOutput(){
        gpsTextView.setText(mAddressOutput);
    }

    // toggled visibility of the progress bar and enables or disables Fetch Adress button
    private void updateUIWidgets(){
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // save whether the address has been requested
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // save the address String
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    // receiver for data sent from FetchAddressIntentService

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        // receives data sent from FetchAddressIntentService and updates the UI

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the addres string or an error message sent from the intent service
            mAddressOutput = resultData.getString(RESULT_DATA_KEY);
            displayAddressOutput();

            // show a Toast message if an address was found
            if (resultCode == SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
                mChangerButtonGPS.setEnabled(true);
            }

            // Reset. Enable to Fetch Address button
            mAddressRequested = false;
            updateUIWidgets();
        }
    }


    // show Snackbar with text
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null){
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    // show Snackbar with parameters:
    // mainTextStringId - the id for string resource for Snackbar text
    // actionStringId - text of the action item
    // listener - listener asociated withe the Snackbar action


    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // provides an additional rationale to the user if user danied the request previously
        // but didn't check the "Don't ask again"

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // request permission
                    ActivityCompat.requestPermissions(SettingsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSION_REQUEST_CODE);
                }
            });
        } else {
            Log.i(TAG, "Requesting permission");
            // it's possible this can be auto answered if device policy sets the permission
            // in a given state or the danied the permission previously and checked
            // "never ask again"
            ActivityCompat.requestPermissions(SettingsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // if user information was interrupted, the permission request is cancelled and you
                // receive empty arrays
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                getAddress();
            } else {
                // permission danied

                // notify the user via a Snackbar that they have rejected a core permission
                // for the app, which makes the this funcion useless
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    // methods to change notification status on and off

    public void notificationOn(View view) {
        notificationStatus = true;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putBoolean(NOTIFICATION_BUTTON, notificationStatus);
        preferencesEditor.apply();
        startActivity(intent);
    }

    public void notificationOff(View view) {
        notificationStatus = false;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putBoolean(NOTIFICATION_BUTTON, notificationStatus);
        preferencesEditor.apply();
        startActivity(intent);
    }

    // onClick method to go back to weather activity
    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
