package com.example.covid_19alertapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.models.InfectedLocations;
import com.example.covid_19alertapp.roomdatabase.VisitedLocations;
import com.example.covid_19alertapp.roomdatabase.VisitedLocationsDao;
import com.example.covid_19alertapp.roomdatabase.VisitedLocationsDatabase;

import java.util.ArrayList;
import java.util.List;

public class UploadLocationsActivity extends AppCompatActivity {
/*
upload locations from local db to firebase
implement verification by medical report photo here
 */

    // local db
    private VisitedLocationsDatabase database = VisitedLocationsDatabase.getDatabase(getApplicationContext());
    private VisitedLocationsDao visitedLocationsDao = database.visitedLocationsDao();

    // retrieved data from local db
    private List<VisitedLocations> retrievedDatas = new ArrayList<>();

    // retrieve and upload progress level
    private int dataSize, currProgress = 0;

    // models to store in firebase
    private MutableLiveData<InfectedLocations> currentInfectedLocation = new MutableLiveData<>();
    final Observer<InfectedLocations> newEntryObserver = new Observer<InfectedLocations>() {
        @Override
        public void onChanged(InfectedLocations infectedLocations) {

            if(!infectedLocations.allFieldsSet())
                // exit if all values not set
                return;

            //TODO: upload to firebase
            Log.d(LogTags.Upload_TAG, "onChanged: UPLOAD Key = "+infectedLocations.getKey()
                    +" dateTime = "+infectedLocations.getDateTime()
                    +" count = "+infectedLocations.getCount()
            );

            // keep track of upload progress (50%-100%)
            currProgress += Math.ceil(50.00f/(float) dataSize);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_locations);

        // set InfectedLocation Live Data observer
        currentInfectedLocation.observe(this, newEntryObserver);

        retrieveAndUpload();

    }

    private void retrieveAndUpload() {

        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                // fetch all from localDB
                retrievedDatas = visitedLocationsDao.fetchAll();

                // retrieval from localDB done (50%)
                currProgress = 50;
                dataSize = retrievedDatas.size();

                for(VisitedLocations roomEntry: retrievedDatas){

                    // splitData[0] = lat,lon
                    // splitData[0] = dateTime
                    String[] splitData = roomEntry.splitPrimaryKey(roomEntry.getConatainerDateTimeComposite());

                    // set the LiveData object
                    currentInfectedLocation.setValue(new InfectedLocations(splitData[0], roomEntry.getCount(), splitData[1]));

                }

            }
        });

    }

}
