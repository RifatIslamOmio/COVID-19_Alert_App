package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Permissions;
import com.example.covid_19alertapp.services.BackgroundWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    /*
    starter activity to test and get the permissions + all time running start worker
    overwrite or edit this later, keeping the permission codes
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initializing the info named shared preference
        UserInfoFormActivity.userInfo = getSharedPreferences(Constants.USER_INFO_SHARED_PREFERENCES,MODE_PRIVATE);

        // start background worker for always
        startWorker();

    }

    private void startWorker() {

        final SharedPreferences preferences =
                getSharedPreferences(Constants.WORKER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean workerState = preferences.getBoolean(Constants.bg_worker_state, false);

        if(!workerState){

            Constraints constraints = new Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresCharging(false)
                    .build();

            PeriodicWorkRequest promptNotificationWork =
                    new PeriodicWorkRequest.Builder(BackgroundWorker.class, 1, TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .addTag(Constants.background_WorkerTag)
                            .build();

            WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(promptNotificationWork.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState() == WorkInfo.State.ENQUEUED) {
                                Log.d(LogTags.Worker_TAG, "onChanged: worker is enqueued");

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(Constants.bg_worker_state, true);
                                editor.apply();
                            }

                            if (workInfo != null && workInfo.getState() == WorkInfo.State.CANCELLED) {
                                Log.d(LogTags.Worker_TAG, "onChanged: worker was stopped. why?");

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(Constants.bg_worker_state, false);
                                editor.apply();
                            }
                        }
                    });


            WorkManager.getInstance(getApplicationContext())
                    .enqueue(promptNotificationWork);

        }

    }

    public void removeThisButton(View view) {
        Intent intent = new Intent(this, TrackerSettingsActivity.class);
        startActivity(intent);
    }

    public void uploadClick(View view) {
        Intent intent = new Intent(this, UploadLocationsActivity.class);
        startActivity(intent);
    }

    public void removethisOnClick(View view) {

        Intent intent = new Intent(getApplicationContext(), ShowMatchedLocationsActivity.class);
        startActivity(intent);

    }

    public void startNewsFeed(View view)
    {
        startActivity(new Intent(getApplicationContext(),NewsFeedActivity.class));
    }
}
