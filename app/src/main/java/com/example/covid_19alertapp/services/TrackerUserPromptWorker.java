package com.example.covid_19alertapp.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Notifications;

public class TrackerUserPromptWorker extends Worker {

    public TrackerUserPromptWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Notifications.createNotificationChannel(getApplicationContext());
        Notifications.showNotification(
                Constants.PromptTrackerNotification_ID,
                getApplicationContext(),
                true
        );

        Log.d(LogTags.Worker_TAG, "doWork: worker WORKED!");

        return Result.success();
    }
}
