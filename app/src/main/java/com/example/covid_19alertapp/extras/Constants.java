package com.example.covid_19alertapp.extras;

public abstract class Constants {

    /*
    store all static constants used in app.
    for example: notification_ids, channel_ids, permission_codes etc.
     */

    public static final String NOTIFICATION_CHANNEL_ID = "all_notifications";
    public static final int PromptTrackerNotification_ID = 274;
    public static final int TrackingLocationNotification_ID = 905;
    public static final int DangerNotification_ID = 540;

    public static final String SHARED_PREFERENCES="sharedPrefs";
    public static final String notification_switch_pref = "switch_1";

    public static final String trackerPrompt_WorkerTag = "switch_1";

    public static final int LOCATION_CHECK_CODE = 101;
    public static final int PERMISSION_CODE = 102;

}
