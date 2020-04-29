package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.AddressReceiver;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.FetchAddress;
import com.example.covid_19alertapp.extras.Internet;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Notifications;
import com.example.covid_19alertapp.models.MatchedLocation;
import com.example.covid_19alertapp.roomdatabase.LocalDBContainer;
import com.example.covid_19alertapp.roomdatabase.VisitedLocations;
import com.example.covid_19alertapp.roomdatabase.VisitedLocationsDao;
import com.example.covid_19alertapp.roomdatabase.VisitedLocationsDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowMatchedLocationsActivity extends AppCompatActivity implements AddressReceiver.AddressView {

    // matched locations model (for recycler-view)
    List<MatchedLocation> matchedLocations = new ArrayList<>();
    int matchedLocationPosition = 0;

    // matched home locations model (for another(?) recycler-view)
    List<MatchedLocation> matchedHomeLocations = new ArrayList<>();

    // firebase
    private DatabaseReference firebaseReference;

    // local db
    private VisitedLocationsDatabase roomDatabase;
    private VisitedLocationsDao visitedLocationsDao;

    // retrieved data from local db
    private List<VisitedLocations> retrievedDatas = new ArrayList<>();
    private int dataSize;

    // Address Fetch
    AddressReceiver addressReceiver = new AddressReceiver(new Handler(), this);

    // UI stuff
    ProgressBar progressBar;
    TextView progressBarText;
    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_matched_locations);

        setUI();

        Notifications.removeNotification(Constants.DangerNotification_ID, this);

        // set local db configs
        roomDatabase = VisitedLocationsDatabase.getDatabase(getApplicationContext());
        visitedLocationsDao = roomDatabase.visitedLocationsDao();

        // firebase
        firebaseReference = FirebaseDatabase.getInstance().getReference();

        findHomeMatchedLocations();
        findMatchedLocations();

    }


    private void setUI() {

        progressBar = findViewById(R.id.progressBar);
        progressBarText = findViewById(R.id.progressText);
        retryButton = findViewById(R.id.retry_btn);

    }

    private void findHomeMatchedLocations() {

        UserInfoFormActivity.userInfo = getApplicationContext().getSharedPreferences(Constants.USER_INFO_SHARED_PREFERENCES,MODE_PRIVATE);

        List<String> queryKeys;
        final String homeLatLng = UserInfoFormActivity.userInfo.getString(Constants.user_home_address_preference, "");
        if(homeLatLng.equals("")){
            Log.d(LogTags.Worker_TAG, "queryHomeAddress: why the hell is home null");
            return;
        }

        final String[] latLng = homeLatLng.split(",");

        queryKeys = LocalDBContainer.calculateContainer(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]), "Bangladesh");

        for (String query: queryKeys) {

            // need '@' instead of '.'
            query = query.replaceAll("\\.","@");

            firebaseReference.child("infectedHomes").child(query)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue()!=null){

                                MatchedLocation homeLocation = new MatchedLocation(
                                        Double.parseDouble(latLng[0]),
                                        Double.parseDouble(latLng[1]),
                                        "NEAR YOUR HOME!",
                                        (long)dataSnapshot.getValue()
                                );

                                matchedHomeLocations.add(homeLocation);

                                Log.d(LogTags.MatchFound_TAG, "onDataChange: home location matched: "+homeLocation.toString());

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            internetDisconncetedUI();

                            Log.d(LogTags.MatchFound_TAG, "onCancelled: home location query failed "+databaseError.getMessage());

                        }
                    });

        }

    }

    private void findMatchedLocations() {

        matchedLocationPosition = 0;
        retryButton.setEnabled(false);
        retryButton.setVisibility(View.GONE);

        matchedLocations.clear();

        roomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                // let home location query finish
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.d(LogTags.MatchFound_TAG, "run: letting home location finish first failed "+e.getMessage());
                }


                // fetch from local db and query firebase
                retrievedDatas = visitedLocationsDao.fetchAll();

                dataSize = retrievedDatas.size();

                if(dataSize==0){
                    // local database empty

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                           localDbEmptyUI();

                        }
                    });

                    return;

                }


                for (VisitedLocations currentEntry: retrievedDatas)
                {
                    // format = "latLon_dateTime"
                    String[] splitter = currentEntry.splitPrimaryKey();

                    // firebase query values
                    final String key = currentEntry.getATencodedlatlon();
                    final String dateTime = splitter[1];

                    Log.d(LogTags.MatchFound_TAG, "run: query key = "+key +" date time = "+dateTime);

                    if(!Internet.isInternetAvailable(getApplicationContext())){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                internetDisconncetedUI();

                            }
                        });

                        return;
                    }

                    // query in firebase
                    firebaseReference = FirebaseDatabase.getInstance().getReference().child("infectedLocations").child(key).child(dateTime);
                    firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null){
                                // INFECTED LOCATION MATCH FOUND!

                                // TODO: add to list/recycler view

                                String latLon = key;
                                long count = (long) dataSnapshot.child("count").getValue();

                                MatchedLocation matchedLocation = new MatchedLocation(latLon, dateTime, count);
                                matchedLocations.add(matchedLocation);

                                // start address fetch service
                                addressReceiver.startAddressFetchService(
                                        ShowMatchedLocationsActivity.this,
                                        matchedLocation.getLatitude(),
                                        matchedLocation.getLongitude(),
                                        matchedLocationPosition
                                );

                                matchedLocationPosition++;

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            // internet connection lost

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    internetDisconncetedUI();
                                }
                            });

                        }
                    });

                }

                // let the value listener and address fetch service catch up
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.d(LogTags.MatchFound_TAG, "run: thread not tired");
                }

                if(matchedLocations.isEmpty()){
                    // no locations match

                    if(matchedHomeLocations.isEmpty()) {
                        // no home locations match either
                        // show no match found

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                noMatchFoundUI();

                            }
                        });
                    }

                    else {
                        // no location match
                        // but home location matched show finish UI

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                dataFetchFinishedUI();

                            }
                        });

                    }

                }

            }
        });

    }

    private void internetDisconncetedUI() {

        progressBar.setVisibility(View.INVISIBLE);
        progressBarText.setText(getText(R.string.internet_disconnected_text));

        retryButton.setEnabled(true);
        retryButton.setVisibility(View.VISIBLE);

        Toast.makeText(this, getText(R.string.no_internet_toast), Toast.LENGTH_LONG)
                .show();

    }

    private void dataFetchFinishedUI(){

        retryButton.setEnabled(false);
        progressBarText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

        Toast.makeText(this, getText(R.string.finished_progressbar_text), Toast.LENGTH_LONG)
                .show();

    }

    private void noMatchFoundUI(){

        progressBar.setVisibility(View.INVISIBLE);
        retryButton.setVisibility(View.GONE);
        retryButton.setEnabled(false);
        progressBarText.setVisibility(View.VISIBLE);
        progressBarText.setText(getText(R.string.no_match_found_text));
    }

    private void localDbEmptyUI(){

        progressBar.setVisibility(View.INVISIBLE);
        retryButton.setVisibility(View.GONE);
        retryButton.setEnabled(false);
        progressBarText.setVisibility(View.VISIBLE);
        progressBarText.setText(getText(R.string.local_db_empty_text));

    }

    public void retryClicked(View view) {

        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setVisibility(View.VISIBLE);
        progressBarText.setText(getText(R.string.loading_progressbar_text));

        findHomeMatchedLocations();
        findMatchedLocations();

    }

    private int updateCount = 0;
    @Override
    public void updateAddress(String address, int listPosition) {
        /*
        address received here
         */

        matchedLocations.get(listPosition).setAddress(address);
        Log.d(LogTags.MatchFound_TAG, "updateAddress: address = "+matchedLocations.get(listPosition).toString());


        updateCount++;
        if(updateCount>=matchedLocations.size()){

            updateCount = 0;

            dataFetchFinishedUI();


        }

    }
}
