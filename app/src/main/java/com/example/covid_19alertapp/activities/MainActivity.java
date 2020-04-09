package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LogTags;
import com.example.covid_19alertapp.extras.Permissions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    /*
    starter activity to test and get the permissions
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


        promptPermissions();
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
}
