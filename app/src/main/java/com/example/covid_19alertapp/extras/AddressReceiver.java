package com.example.covid_19alertapp.extras;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class AddressReceiver extends ResultReceiver {

    private Activity activity;

    private static final String GEO_LOCATION = "geo_location";
    private static final String GEO_ADDRESS = "geo_address";

    public AddressReceiver(Handler handler, Activity activity) {

        super(handler);
        this.activity = activity;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultData==null){
            Log.d(LogTags.Address_TAG, "onReceiveResult: null resultData");
            return;
        }

        // receive data from the FetchAddress
        String addressOutput = resultData.getString(GEO_ADDRESS);
        if(addressOutput==null)
            addressOutput = "no accurate address received";

        Log.d(LogTags.Address_TAG,"onReceiveResult: address received = "+addressOutput);

        // do something with the 'addressOutput'



    }

}