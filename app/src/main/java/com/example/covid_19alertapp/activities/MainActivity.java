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
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

import static com.example.covid_19alertapp.activities.UserInfoFormActivity.userInfo;

public class MainActivity extends AppCompatActivity {
    /*
    starter activity to test and get the permissions + all time running start worker
    overwrite or edit this later, keeping the permission codes
     */


    private Permissions permissions;
    private static final String[] permissionStrings = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ask for permissions start of app
        promptPermissions();


        //initializing the info named shared preference
        userInfo = getSharedPreferences("info",MODE_PRIVATE);


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
                    new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15, TimeUnit.MINUTES)
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

    private void promptPermissions() {

        permissions = new Permissions(this, permissionStrings, Constants.PERMISSION_CODE);

        if(!permissions.checkPermissions())
            permissions.askPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //resolve unresolved permissions

        switch (requestCode){

            case Constants.PERMISSION_CODE:

                try {
                    this.permissions.resolvePermissions(permissions, grantResults);
                }catch (Exception e){
                    Log.d(LogTags.Permissions_TAG, "onRequestPermissionsResult: "+e.getMessage());
                }

                break;

        }

    }

    public void removeThisButton(View view) {
        Intent intent = new Intent(this, TrackerSettingsActivity.class);
        startActivity(intent);
    }

    public void pickHomeClick(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void uploadClick(View view) {
        Intent intent = new Intent(this, UploadLocationsActivity.class);
        startActivity(intent);
    }

    public void testUIClick(View view) {

        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));

    }
}
