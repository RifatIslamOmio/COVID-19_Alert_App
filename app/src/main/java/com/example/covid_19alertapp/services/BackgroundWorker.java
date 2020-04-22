package com.example.covid_19alertapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.covid_19alertapp.activities.TrackerSettingsActivity;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Notifications;

import static android.content.Context.MODE_PRIVATE;

public class BackgroundWorker extends Worker {

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //get tracker status from sharedPreferences
        SharedPreferences sharedPreferences =
                getApplicationContext().getSharedPreferences(Constants.LOCATION_SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE);
        boolean trackerState = sharedPreferences.getBoolean(Constants.location_tracker_state,false);

        if(!trackerState) {
            // tracker is off prompt notification

            Notifications.createNotificationChannel(getApplicationContext());
            Notifications.showNotification(
                    Constants.PromptTrackerNotification_ID,
                    getApplicationContext(),
                    TrackerSettingsActivity.class,
                    true
            );
        }


        //TODO: delete 7 days old locations from room db


        //TODO: match room db locations with firebase locations



        Log.d(LogTags.Worker_TAG, "doWork: worker WORKED!");

        return Result.success();
    }



    @Override
    public void onStopped() {
        super.onStopped();

        Log.d(LogTags.Worker_TAG, "onStopped: Worker stopped. why?");

        // set shared preference false
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Constants.WORKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.bg_worker_state, false);
        editor.apply();
    }
}
