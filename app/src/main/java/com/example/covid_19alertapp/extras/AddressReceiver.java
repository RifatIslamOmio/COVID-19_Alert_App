package com.example.covid_19alertapp.extras;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class AddressReceiver extends ResultReceiver {

    private Activity activity;

    // for interface
    private AddressView view;


    private static final String GEO_ADDRESS = "geo_address";
    private static final String GEO_LOCATION = "geo_location";
    private static final String GEO_RECEIVER = "geo_receiver";
    private static final String LIST_POSITION = "position@list";

    public AddressReceiver(Handler handler, Activity activity, AddressView view) {

        super(handler);
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultData==null){
            Log.d(LogTags.Address_TAG, "onReceiveResult: null resultData");
            return;
        }

        // receive data from the FetchAddress
        String addressOutput = resultData.getString(GEO_ADDRESS);
        int listPosition = resultData.getInt(LIST_POSITION);

        if(addressOutput==null || resultCode==FetchAddress.getGeoFailure())
            addressOutput = "no address available (tap to see in map)";

        Log.d(LogTags.Address_TAG,"onReceiveResult: address received = "+addressOutput);

        // do something with the 'addressOutput'

        view.updateAddress(addressOutput, listPosition);


    }

    public void startAddressFetchService(double latitude, double longitude, int listPosition){

        Location location = null;
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Intent intent = new Intent(activity, FetchAddress.class);
        intent.putExtra(GEO_LOCATION, location);
        intent.putExtra(GEO_RECEIVER, this);
        intent.putExtra(LIST_POSITION, listPosition);

        Log.d(LogTags.Address_TAG, "startAddressFetchService: starting address service for position = "+listPosition);

        activity.startService(intent);
    }

    public interface AddressView{

        void updateAddress(String address, int listPosition);

    }

}