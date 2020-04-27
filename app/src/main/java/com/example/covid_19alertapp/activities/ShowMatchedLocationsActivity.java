package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Notifications;
import com.example.covid_19alertapp.models.MatchedLocation;
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

    // firebase
    private DatabaseReference firebaseReference;

    // local db
    private VisitedLocationsDatabase roomDatabase;
    private VisitedLocationsDao visitedLocationsDao;

    // retrieved data from local db
    private List<VisitedLocations> retrievedDatas = new ArrayList<>();

    // retrieve and upload progress level
    private double currProgress = 0;
    private int dataCount=0,dataSize;

    // Address Fetch
    AddressReceiver addressReceiver = new AddressReceiver(new Handler(), this, this);

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

        // TODO:[Check] fetch locations and match

        // set local db configs
        roomDatabase = VisitedLocationsDatabase.getDatabase(getApplicationContext());
        visitedLocationsDao = roomDatabase.visitedLocationsDao();

        // firebase
        firebaseReference = FirebaseDatabase.getInstance().getReference();

        findMatchedLocations();

    }

    private void setUI() {

        progressBar = findViewById(R.id.progressBar);
        progressBarText = findViewById(R.id.progressBar_text);
        retryButton = findViewById(R.id.retry_btn);

    }

    private void findMatchedLocations() {

        roomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                // fetch from local db and query firebase
                retrievedDatas = visitedLocationsDao.fetchAll();

                // retrieval from localDB done (50%)
                currProgress = 50;
                progressBar.setProgress((int) currProgress);

                dataSize = retrievedDatas.size();

                for (VisitedLocations currentEntry: retrievedDatas)
                {
                    // format = "latLon_dateTime"
                    String[] splitter = currentEntry.splitPrimaryKey();

                    // firebase query values
                    final String key = currentEntry.getATencodedlatlon();
                    final String dateTime = splitter[1];

                    // query in firebase
                    firebaseReference = FirebaseDatabase.getInstance().getReference().child("infectedLocations").child(key).child(dateTime);
                    firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                // INFECTED LOCATION MATCH FOUND!

                                // TODO: add to list/recycler view

                                String latLon = key;
                                long count = (long) dataSnapshot.child("count").getValue();

                                MatchedLocation matchedLocation = new MatchedLocation(latLon, dateTime, count);
                                matchedLocations.add(matchedLocation);

                                // start address fetch service
                                addressReceiver.startAddressFetchService(
                                        matchedLocation.getLatitude(),
                                        matchedLocation.getLongitude(),
                                        matchedLocationPosition
                                );

                                matchedLocationPosition++;

                                Log.d(LogTags.MatchFound_TAG, "onDataChange: matched data = "+ matchedLocation.toString());

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            //TODO: add a refresh button, in case internet connection goes off midway

                            Toast.makeText(getApplicationContext()
                                    , getApplicationContext().getString(R.string.no_internet_toast),
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    });


                    // keep track of upload progress (50%-100%)
                    currProgress += (double) 50/dataSize;
                    if(currProgress<=100)
                        progressBar.setProgress((int) currProgress);

                    dataCount++;
                    if(dataCount==dataSize){
                        // remove progressbar


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                retryButton.setEnabled(true);
                                progressBarText.setText(getText(R.string.finished_progressbar_text));
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }

                }

            }
        });

    }

    public void retryClicked(View view) {

        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setText(getText(R.string.loading_progressbar_text));
        findMatchedLocations();

    }

    @Override
    public void updateAddress(String address, int listPosition) {

        /*
        address received here
         */

        matchedLocations.get(listPosition).setAddress(address);


    }
}
