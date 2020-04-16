package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.LogTags;
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

public class UploadLocationsActivity extends AppCompatActivity {
/*
upload locations from local db to firebase
implement verification by medical report photo here
 */

    // firebase
    //private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firbaseReference;

    // local db
    private VisitedLocationsDatabase roomDatabase;
    private VisitedLocationsDao visitedLocationsDao;

    // retrieved data from local db
    private List<VisitedLocations> retrievedDatas = new ArrayList<>();

    // retrieve and upload progress level
    private int dataSize, currProgress = 0;

    // models to store in firebase
    private MutableLiveData<InfectedLocations> currentInfectedLocation = new MutableLiveData<>();
    final Observer<InfectedLocations> newEntryObserver = new Observer<InfectedLocations>() {
        @Override
        public void onChanged(final InfectedLocations infectedLocations) {

            if(!infectedLocations.allFieldsSet()) {
                // exit if all values not set
                Log.d(LogTags.Upload_TAG, "onChanged: all fields not set");
                return;
            }

            // upload to firebase
            // update if already exists
            firbaseReference.child("infectedLocations").child(infectedLocations.getKey()).child(infectedLocations.getDateTime())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!= null){
                                // entry already exists
                                // update count
                                long count = (long) dataSnapshot.child("count").getValue();
                                Log.d(LogTags.Upload_TAG, "onDataChange: data already exists. count = "+count);

                                count+=infectedLocations.getCount();

                                firbaseReference.child("infectedLocations").child(infectedLocations.getKey()).child(infectedLocations.getDateTime())
                                        .child("count").setValue(count);
                                Log.d(LogTags.Upload_TAG, "onDataChange: count updated = "+count);
                            }
                            else{
                                // no such entry exists

                                firbaseReference.child("infectedLocations").child(infectedLocations.getKey()).child(infectedLocations.getDateTime())
                                        .setValue(infectedLocations);

                                Log.d(LogTags.Upload_TAG, "onDataChange: no data exists. " +
                                        "new data inserted to firebase.");
                            }

                            // keep track of upload progress (50%-100%)
                            currProgress += Math.ceil(50.00f/(float) dataSize);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Log.d(LogTags.Upload_TAG, "onCancelled: data read and update failed! Error:"+databaseError.getMessage());

                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_locations);

        // set firebase database offline capability, set firebase reference
        if(firbaseReference == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            firbaseReference  = database.getReference();
        }

        // set local db configs
        roomDatabase = VisitedLocationsDatabase.getDatabase(getApplicationContext());
        visitedLocationsDao = roomDatabase.visitedLocationsDao();

        // set InfectedLocation Live Data observer
        currentInfectedLocation.observe(this, newEntryObserver);

    }

    private void retrieveUploadDelete() {
        /*
        retrive from local database,
        upload to firebase,
        delete from local databse
         */

        roomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                // fetch all from localDB
                retrievedDatas = visitedLocationsDao.fetchAll();
                Log.d(LogTags.Upload_TAG, "onCreate: local database retrieved");

                // retrieval from localDB done (50%)
                currProgress = 50;
                dataSize = retrievedDatas.size();


                if(dataSize==0)
                    // notify on UI thread no data found locally
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadLocationsActivity.this, "No locations recorded", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });


                for(VisitedLocations roomEntry: retrievedDatas){

                    // splitData[0] = lat,lon
                    // splitData[1] = dateTime
                    String[] splitData = roomEntry.splitPrimaryKey(roomEntry.getConatainerDateTimeComposite());

                    Log.d(LogTags.Upload_TAG, "run: current retrieved data = "
                            +splitData[0]+", "+roomEntry.getCount()+", "+splitData[1]);

                    // set the LiveData object
                    currentInfectedLocation.postValue(new InfectedLocations(splitData[0], roomEntry.getCount(), splitData[1]));

                    // delete current entry from local database
                    visitedLocationsDao.deleteLocation(roomEntry);
                    Log.d(LogTags.Upload_TAG, "onCreate: deleting room entry = "
                            +roomEntry.getConatainerDateTimeComposite());


                    // sleep, give time to upload properly?
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(LogTags.Upload_TAG, "run: thread just had coffee and isn't tired rn");
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    public void uploadClicked(View view) {
        /*
        upload button click
         */

        retrieveUploadDelete();
    }
}
