package com.example.covid_19alertapp.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.covid_19alertapp.R;

public class UserInfoFormActivity extends AppCompatActivity {


    EditText dobText,userName,workAddress,homeAddress;
    Button save_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_form);

        dobText=  findViewById(R.id.dateOfBirth);
        userName = findViewById(R.id.userName);
        workAddress = findViewById(R.id.workAdress);
        homeAddress = findViewById(R.id.homeAddress);
        save_profile = findViewById(R.id.SaveProfButton);


        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequiredEditText(userName);
                RequiredEditText(homeAddress);
                RequiredEditText(dobText);

                //Write save profile function
            }
        });





    }



    void RequiredEditText(EditText e)
    {
        if(e.getText().toString().length()==0)
        {
            e.setError("Required");
        }
    }
}
