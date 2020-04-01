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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class LocationFetch {

    private static Context context;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private static LocationCallback locationCallback;

    // for calculation of distance and time difference
    private static Location prevLocation=null;
    private static int prevMinute=-1;

    // for virtual container
    private static final double CONTAINER_RADIUS = 20.0f; // meters
    private static final int TIME_WINDOW = 15; // minutes

    public static List<String> getDiagonalRangePoints(Double lat, Double lon, String country)
    {
        Double latDevider=0.0,lonDevider=0.0,latX,lony;
        List<String> diagonalRangePoint =new ArrayList<String>();
        if( country=="Bangladesh"){
            latDevider=.0002000;
            lonDevider=.0002000;

        }
        //lat=lat*10000;
        //lon=lon*10000;
        latX=Math.floor(lat/latDevider)*latDevider;
        lony=Math.floor(lon/lonDevider)*lonDevider;
        //upper left            upper right
        Double boxA_X,boxA_Y,boxC_X,boxC_Y;                 //upper box
        boxA_X=latX;                                       //#### C(x,y)
        boxA_Y=lony;                     //left           // #  #   right box
        boxC_X=latX+latDevider;                          //  #  #
        boxC_Y=lony+lonDevider;                         //(A)####
        //    #  #   lower box
        diagonalRangePoint.add(Double.toString(boxA_X)+","+Double.toString(boxA_Y)+","+Double.toString(boxC_X)+","+Double.toString(boxC_Y));
        //System.out.println(boxC_X+" boxc "+latX+" lat x "+latDevider+" lat latDevider "+ latX+latDevider);
        if(lat- boxA_X<latDevider/2){
            //lower box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxC_X)+","+Double.toString(boxA_Y)+","+Double.toString(boxA_X)+","+Double.toString(boxA_Y- lonDevider));
            //diagonalRangePoint.put("Lower Box C X",boxC_X);
            //diagonalRangePoint.put("Lower Box C Y",boxA_Y);
            //diagonalRangePoint.put("Lower Box A X",boxA_X);
            //diagonalRangePoint.put("Lower Box A Y",boxA_Y- lonDevider);
        }
        else if(boxC_X-lat<latDevider/2){
            //Upper box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxC_X)+","+Double.toString(boxC_Y+lonDevider)+","+Double.toString(boxA_X)+","+Double.toString(boxC_Y));
            //diagonalRangePoint.put("Upper Box C X",boxC_X);
            //diagonalRangePoint.put("Upper Box C Y",boxC_Y+lonDevider);
            //diagonalRangePoint.put("Upper Box A X",boxA_X);
            //diagonalRangePoint.put("Upper Box A Y",boxC_Y);
        }
        if(lon- boxA_Y<=latDevider/2){
            //left box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxA_X- latDevider)+","+Double.toString(boxA_Y)+","+Double.toString(boxA_X)+","+Double.toString(boxC_Y));
            //diagonalRangePoint.put("Left Box A X",boxA_X- latDevider);
            //diagonalRangePoint.put("Left Box A Y",boxA_Y);
            //diagonalRangePoint.put("Left Box C X",boxA_X);
            //diagonalRangePoint.put("Left Box C Y",boxC_Y);
        }
        if(boxC_Y-lon<lonDevider/2){
            //Right box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxC_X)+","+Double.toString(boxA_Y)+","+Double.toString(boxC_X+latDevider)+","+Double.toString(boxC_Y));
            //diagonalRangePoint.put("Right Box A X",boxC_X);
            //diagonalRangePoint.put("Right Box A Y",boxA_Y);
            //diagonalRangePoint.put("Right Box C X",boxC_X+latDevider);
            //diagonalRangePoint.put("Right Box C Y",boxC_Y);
        }
        if(boxC_X-lat <latDevider/2 && boxC_Y-lon<lonDevider/2){
            //Upper Right  box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxC_X)+","+Double.toString(boxC_Y)+","+Double.toString(boxC_X+latDevider)+","+Double.toString(boxC_Y+lonDevider));
            //diagonalRangePoint.put("Upper right Box A X",boxC_X);
            //diagonalRangePoint.put("Upper right Box A Y",boxC_Y);
            //diagonalRangePoint.put("Upper right Box C X",boxC_X+latDevider);
            //diagonalRangePoint.put("Upper right Box C Y",boxC_Y+lonDevider);
        }
        else if(lat- boxA_X <latDevider/2 && lon- boxA_Y<lonDevider/2){
            //Lower left box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxA_X)+","+Double.toString(boxA_Y)+","+Double.toString(boxA_X-latDevider)+","+Double.toString(boxA_Y-lonDevider));
            //diagonalRangePoint.put("Lower left Box C X",boxA_X);
            //diagonalRangePoint.put("Lower left Box C Y",boxA_Y);
            //diagonalRangePoint.put("Lower left Box A X",boxA_X-latDevider);
            //diagonalRangePoint.put("Lower left Box A Y",boxA_Y-lonDevider);
        }
        else if(lat- boxA_X <latDevider/2 && lon- boxA_Y<lonDevider/2){

            //Upper Left  box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxA_X)+","+Double.toString(boxA_Y+2*lonDevider)+","+Double.toString(boxC_X-2*latDevider)+","+Double.toString(boxC_Y));
            //diagonalRangePoint.put("Upper left Box C X",boxA_X);
            //diagonalRangePoint.put("Upper left Box C Y",boxA_Y+2*lonDevider);
            //diagonalRangePoint.put("Upper left Box A X",boxC_X-2*latDevider);
            //diagonalRangePoint.put("Upper left Box A Y",boxC_Y);
        }
        else if(lat- boxA_X <latDevider/2 && lon- boxA_Y<lonDevider/2){
            //Lower Right  box's diagonal points are to be inserted
            diagonalRangePoint.add(Double.toString(boxA_X+2*lonDevider)+","+Double.toString(boxA_Y)+","+Double.toString(boxC_X)+","+Double.toString(boxC_Y-2*lonDevider));
            //diagonalRangePoint.put("Lower right Box C X",boxA_X+2*lonDevider);
            //diagonalRangePoint.put("Lower right Box C Y",boxA_Y);
            //diagonalRangePoint.put("Lower right Box A X",boxC_X);
            //diagonalRangePoint.put("Lower right Box A Y",boxC_Y-2*lonDevider);
        }



        return diagonalRangePoint;


    }


    //location request needs to be defined for checkNotificationsSettings() to work
    private static final int MINIMUM_ACCURACY = 30, UPDATE_INTERVAL_MILLIS = 10000;
    private static LocationRequest locationRequest = LocationRequest.create()
            .setInterval(UPDATE_INTERVAL_MILLIS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public static boolean isLocationEnabled = false;



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

                        if(significantDifference(location) || timeWindowExceeded(Calendar.getInstance())) {
                            // location is more than 50m away from 'prevLocation'
                            // or, time difference is more than 15 minutes

                            LocationFetch.prevLocation = location;

                            Calendar cal = Calendar.getInstance();
                            String dateTime = (cal.get(Calendar.MONTH)+1) +"-" // Calender.MONTH is 0 based -_- why tf?
                                    + cal.get(Calendar.DATE) +"-"
                                    + cal.get(Calendar.HOUR);

                            Log.d(LogTags.Location_TAG,
                                    "onLocationResult: location received at "
                                            + dateTime
                                            +" \n["+location.getLatitude()+", "+location.getLongitude()+"]");

                            //TODO:
                            // call the container method here
                            // method parameters: 'location.getLatitude()', 'location.getLongitude()'
                            // then store the return values and 'dateTime' in local database
                        }
                    }
                }

            }
        };

        prevLocation = null;
        prevMinute = -1;

    }

    private void storeInContainer(double latitude, double longitude){

        //TODO: calculate container here (tahmid tor container er part output log e print kore rekhe de)
        // then push to local database

    }

    private static boolean significantDifference(Location location) {
        //TODO: ekhane lekh
        /*
        returns true if distance between 'prevLocation' and 'location' is more than 20m
         */

        double distance, lat1, long1, lat2, long2;

        if(prevLocation==null)
            // first time
            return true;

        lat1 = Math.toRadians(prevLocation.getLatitude());
        long1 = Math.toRadians(prevLocation.getLongitude());
        lat2 = Math.toRadians(location.getLatitude());
        long2 = Math.toRadians(location.getLongitude());

        distance = Math.acos(
                        Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(long1-long2)
                    ) * 6371000.0f;

        distance = Math.abs(distance);

        Log.d(LogTags.Location_TAG, "significantDifference: distance = "+distance);

        if(distance>=CONTAINER_RADIUS)
            return true;

        return false;
    }

    private static boolean timeWindowExceeded(Calendar currTimeInstance){
        /*
        return true if more than 15 minutes passed
         */

        if(prevMinute==-1)
            // first time
            return true;

        int currMinute =  currTimeInstance.get(Calendar.MINUTE);
        Log.d(LogTags.Location_TAG, "timeWindowExceeded: curr minute = "+currMinute);

        if(currMinute<prevMinute)
            currMinute+=60;

        if(currMinute-prevMinute>=TIME_WINDOW) {
            prevMinute = currMinute;
            return true;
        }

        prevMinute = currMinute;
        return false;

    }


    public static void checkDeviceLocationSettings(final Activity activity) {
        /*
        check and prompt the user to enable required location settings
         */

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationFetch.isLocationEnabled = true;
                Log.d(LogTags.Location_TAG, "checkSettings onSuccess: location update requested");
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(LogTags.Location_TAG, "checkSettings onFailure: location settings failed");

                LocationFetch.isLocationEnabled = false;

                if(e instanceof ResolvableApiException){
                    try{

                        ResolvableApiException resolvable = (ResolvableApiException) e;

                        resolvable.startResolutionForResult(activity,
                                Constants.LOCATION_CHECK_CODE); //runs onActivityResult() callback of the associated Activity

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
