package com.example.covid_19alertapp.activities;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.dataStorage.UserInfoData;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.LogTags;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.covid_19alertapp.activities.SignUpActivity.PHONE_NUMBER;

public class UserInfoFormActivity extends AppCompatActivity {


    EditText dobText,userName,workAddress,homeAddress;
    Button save_profile;
    UserInfoData userInfoData;

    FirebaseDatabase database;
    DatabaseReference userInfoRef;
    String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    String path="UserInfo";

    public static SharedPreferences userInfo;


    // home and work address result keys
    private static final int HOME_ADDRESS_PICKER = 629;
    private static final int WORK_ADDRESS_PICKER = 919;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_form);

        dobText=  findViewById(R.id.dateOfBirth);
        userName = findViewById(R.id.userName);
        workAddress = findViewById(R.id.workAdress);
        homeAddress = findViewById(R.id.homeAddress);
        save_profile = findViewById(R.id.SaveProfButton);

        userInfo = getSharedPreferences(Constants.USER_INFO_SHARED_PREFERENCES,MODE_PRIVATE);

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(RequiredEditText(userName) && RequiredEditText(homeAddress) && RequiredEditText(dobText)) {

                    final String name, home, workPlace, dateOfBirth;
                    name = userName.getText().toString();
                    home = homeAddress.getText().toString();
                    workPlace = workAddress.getText().toString();
                    dateOfBirth = dobText.getText().toString();

                    if (TextUtils.isEmpty(workPlace)) {
                        userInfoData = new UserInfoData(name, dateOfBirth, home, PHONE_NUMBER);

                        userInfo.edit().putString(Constants.user_work_address_preference, null).apply();
                    } else {
                        userInfoData = new UserInfoData(name, dateOfBirth, workPlace, home, PHONE_NUMBER);
                        userInfo.edit().putString(Constants.user_work_address_preference, workPlace).apply();
                    }
                    //applying values to the info names Shared Preference

                    userInfo.edit().putString(Constants.username_preference, name).apply();
                    userInfo.edit().putString(Constants.user_dob_preference, dateOfBirth).apply();
                    userInfo.edit().putString(Constants.user_home_address_preference, home).apply();
                    userInfo.edit().putString(Constants.uid_preference, uid).apply();
                    userInfo.edit().putString(Constants.user_phone_no_preference, PHONE_NUMBER).apply();
                    userInfo.edit().putBoolean(Constants.user_exists_preference, true).apply();

                    database = FirebaseDatabase.getInstance();
                    userInfoRef = database.getReference(path).child(uid);
                    userInfoRef.setValue(userInfoData);


                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    //Write save profile function

                }

            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case (HOME_ADDRESS_PICKER):
                if(resultCode == RESULT_OK){

                    // home address
                    String latLon = data.getStringExtra("latitude-longitude");
                    Log.d(LogTags.Map_TAG, "onActivityResult: home address fetched = "+latLon);

                }

                break;

            case (WORK_ADDRESS_PICKER):
                if(resultCode == RESULT_OK){

                    //work address
                    String latLon = data.getStringExtra("latitude-longitude");
                    Log.d(LogTags.Map_TAG, "onActivityResult: work address fetched = "+latLon);

                }

                break;
        }

    }

    private void addressOnClicks(){

        // home address click
        Intent homeIntent = new Intent(this, AddressPickerMapsActivity.class);
        startActivityForResult(homeIntent, HOME_ADDRESS_PICKER);

        // work address click
        Intent workIntent = new Intent(this, AddressPickerMapsActivity.class);
        startActivityForResult(workIntent, WORK_ADDRESS_PICKER);

    }

    private boolean RequiredEditText(EditText e)
    {
        if(e.getText().toString().length()==0)
        {
            e.setError("Required");
            return false;
        }

        return true;
    }

}
