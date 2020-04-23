package com.example.covid_19alertapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LocationFetch;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Notifications;
import com.example.covid_19alertapp.services.BackgroundLocationTracker;
import com.example.covid_19alertapp.services.TrackerUserPromptWorker;

import java.util.concurrent.TimeUnit;

public class TrackerSettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Switch notification_switch;
    private static boolean switch_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_settings);

        //setup and start the Worker
        startWorker();

        //start notification channel(do this is MainActivity
        Notifications.createNotificationChannel(this);

        notification_switch = findViewById(R.id.notification_switch);
        toolbar = findViewById(R.id.settings_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));

        notification_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

                save_preferences(notification_switch.isChecked());
                if(notification_switch.isChecked())
                {
                    try {

                        LocationFetch.checkDeviceLocationSettings(TrackerSettingsActivity.this);
                        if(LocationFetch.isLocationEnabled) {
                            // location is enabled
                            // start tracker service
                            Log.d(LogTags.Location_TAG, "onClick: location found enabled");

                            // start BackgroundLocationTracker
                            startTrackerService();
                        }

                        else{
                            // location is not enabled
                            Log.d(LogTags.Location_TAG, "onClick: location found disabled");

                            notification_switch.setChecked(false);
                            Toast.makeText(getApplicationContext(), "Turn on location or press again please", Toast.LENGTH_LONG)
                                    .show();
                            save_preferences(false);
                        }
                    }catch (Exception e){
                        Log.d(LogTags.TrackerSettings_TAG, "onClick: error occured!");
                    }
                }
                else
                {
                    try {
                        // stop location tracker
                        stopService(new Intent(getApplicationContext(),BackgroundLocationTracker.class));

                        // enqueue TrackerUserPromptWorker
                        startWorker();

                    }catch (Exception e){
                        Log.d(LogTags.TrackerSettings_TAG, "onClick: error occured!");
                    }
                }
            }
        });
        loadData();
        updateViews();
    }

    private void startWorker() {

        //get tracker status from sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        boolean trackerState = sharedPreferences.getBoolean(Constants.notification_switch_pref,false);

        if(!trackerState) {
            //Enqueue the worker

            Constraints constraints = new Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresCharging(false)
                    .build();

            PeriodicWorkRequest promptNotificationWork =
                    new PeriodicWorkRequest.Builder(TrackerUserPromptWorker.class, 1, TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .addTag(Constants.trackerPrompt_WorkerTag)
                            .build();

            WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(promptNotificationWork.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState() == WorkInfo.State.ENQUEUED) {
                                Log.d(LogTags.Worker_TAG, "onChanged: worker is enqueued");
                            }

                            if (workInfo != null && workInfo.getState() == WorkInfo.State.CANCELLED) {
                                Log.d(LogTags.Worker_TAG, "onChanged: worker was stopped");
                            }
                        }
                    });


            WorkManager.getInstance(getApplicationContext())
                    .enqueue(promptNotificationWork);
        }
    }

    private void startTrackerService(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), BackgroundLocationTracker.class));
            Log.d(LogTags.TrackerSettings_TAG, "onClick: newer version phones foreground service stared");
        } else
            startService(new Intent(getApplicationContext(), BackgroundLocationTracker.class));

    }


    public void save_preferences(boolean state)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.notification_switch_pref,state);
        editor.apply();
    }
    public void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        switch_status = sharedPreferences.getBoolean(Constants.notification_switch_pref,false);
        updateViews();
    }

    public void updateViews()
    {
        notification_switch.setChecked(switch_status);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case Constants.LOCATION_CHECK_CODE:
                // user input from the dialogbox showed after checkLocation()

                if(Activity.RESULT_OK == resultCode){
                    // user picked yes
                    Log.d(LogTags.Location_TAG, "onActivityResult: user picked yes. starting background location tracker");

                    startTrackerService();

                    // save settings preferences
                    save_preferences(true);
                    // set LocationFetch boolean
                    LocationFetch.isLocationEnabled = true;

                    //set the settings switch UI to true
                    notification_switch.setChecked(true);
                }

                else if(Activity.RESULT_CANCELED == resultCode){
                    // user picked no
                    Log.d(LogTags.Location_TAG, "onActivityResult: user picked no. setting boolean and preference to false");

                    save_preferences(false);
                    LocationFetch.isLocationEnabled = false;
                }


                break;

        }

    }

}
