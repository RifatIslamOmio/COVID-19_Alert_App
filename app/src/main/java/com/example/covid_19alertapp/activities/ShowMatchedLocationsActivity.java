package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Notifications;
import com.example.covid_19alertapp.models.InfectedLocations;
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

public class ShowMatchedLocationsActivity extends AppCompatActivity {

    // firebase
    private DatabaseReference firbaseReference;

    // local db
    private VisitedLocationsDatabase roomDatabase;
    private VisitedLocationsDao visitedLocationsDao;

    // retrieved data from local db
    private List<VisitedLocations> retrievedDatas = new ArrayList<>();

    // retrieve and upload progress level
    private double currProgress = 0;
    private int dataCount=0,dataSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_matched_locations);

        Notifications.removeNotification(Constants.DangerNotification_ID, this);

        // TODO:[Check] fetch locations and match

        // set local db configs
        roomDatabase = VisitedLocationsDatabase.getDatabase(getApplicationContext());
        visitedLocationsDao = roomDatabase.visitedLocationsDao();

        // firebase
        firbaseReference = FirebaseDatabase.getInstance().getReference();

        retrieveFromLocalDb();

    }

    private void retrieveFromLocalDb() {

        roomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                firbaseReference = FirebaseDatabase.getInstance().getReference();

                // fetch from local db and query firebase
                retrievedDatas = visitedLocationsDao.fetchAll();

                // retrieval from localDB done (50%)
                currProgress = 50;
                dataSize = retrievedDatas.size();

                for (VisitedLocations currentEntry: retrievedDatas)
                {
                    // format = "latLon_dateTime"
                    String[] splitter = currentEntry.splitPrimaryKey();

                    // firebase query values
                    final String key = currentEntry.getATencodedlatlon();
                    final String dateTime = splitter[1];

                    // query in firebase
                    firbaseReference = FirebaseDatabase.getInstance().getReference().child("infectedLocations").child(key).child(dateTime);
                    firbaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null){
                                // INFECTED LOCATION MATCH FOUND!

                                // TODO: add to list/recycler view
                                String latLon = key.replace('@', '.');
                                String date = dateTime;
                                long count = (long) dataSnapshot.child("count").getValue();
                                Log.d(LogTags.MatchFound_TAG, "onDataChange: matched data = location:"+latLon+"date: "+date+" count:"+count);

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

                    dataCount++;
                    if(dataCount==dataSize){
                        // remove progressbar

                    }

                }

            }
        });

    }
}
