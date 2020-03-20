package com.example.covid_19alertapp.extras;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.concurrent.Executor;

public abstract class LocationFetch {

    private static Context context;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private static LocationCallback locationCallback;
    private static Location prevLocation;

    //location request needs to be defined for checkNotificationsSettings() to work
    private static final int MINIMUM_ACCURACY = 30, UPDATE_INTERVAL_MILLIS = 10000;
    private static LocationRequest locationRequest = LocationRequest.create()
            .setInterval(UPDATE_INTERVAL_MILLIS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);;

    public static boolean isLocationEnabled;

    public static void setup(Context context) {
        /*
        call from context(service) where location fetching is going to start
        setup the variables that require Context
         */

        LocationFetch.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        //location request callback
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                //called whenever new location is available
                super.onLocationAvailability(locationAvailability);
                Log.d(LogTags.Location_TAG, "onLocationAvailability: "+locationAvailability.toString());
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(locationResult == null){
                    //can be null(according to doc)
                    Log.d(LogTags.Location_TAG, "onLocationResult: null location");
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    // location received do stuff

                    if(location.getAccuracy() <= MINIMUM_ACCURACY){
                        // desired location received

                        if(significantDifference(location)) {
                            //location is more than 50m away from 'prevLocation'

                            LocationFetch.prevLocation = location;
                            Log.d(LogTags.Location_TAG,
                                    "onLocationResult: location received at "
                                            + Calendar.getInstance().getTime()
                                            +" \n["+location.getLatitude()+", "+location.getLongitude()+"]");
                        }

                        //TODO: stop location update?
                        //stopLocationUpdates();
                    }
                }

            }
        };

        isLocationEnabled = false;
    }

    private static boolean significantDifference(Location location) {
        //TODO: ekhane lekh
        /*
        returns true if distance between 'prevLocation' and 'location' is more than 50m
         */

        // use these:
        // prevLocation.getLatitude();
        // prevLocation.getLongitude();
        // location.getLatitude();
        // location.getLongitude();

        return true;
    }


    public static void checkDeviceLocationSettings(final Activity activity) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationFetch.isLocationEnabled = true;
                Log.d(LogTags.Location_TAG, "onSuccess: location update requested");
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                LocationFetch.isLocationEnabled = false;

                if(e instanceof ResolvableApiException){
                    try{

                        ResolvableApiException resolvable = (ResolvableApiException) e;

                        //TODO: override onActivityResult() from calling Activity
                        resolvable.startResolutionForResult(activity,
                                Constants.LOCATION_CHECK_CODE); //runs onActivityResult() callback

                    }catch (IntentSender.SendIntentException sendEx){
                        //ignore
                        Log.d(LogTags.Location_TAG, "onFailure: ignore?");
                    }
                }
            }
        });

    }

    public static void startLocationUpdates() {
        //start the location update
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        Log.d(LogTags.Location_TAG, "startLocationUpdates: location update started");
    }

    public static void stopLocationUpdates(){
        //remove location update
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        Log.d(LogTags.Location_TAG, "stopLocationUpdates: location update stopped");
    }


}
