package com.example.covid_19alertapp.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.dataStorage.UserInfoData;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_form);

        dobText=  findViewById(R.id.dateOfBirth);
        userName = findViewById(R.id.userName);
        workAddress = findViewById(R.id.workAdress);
        homeAddress = findViewById(R.id.homeAddress);
        save_profile = findViewById(R.id.SaveProfButton);
        userInfo = getSharedPreferences("info",MODE_PRIVATE);

        

        final Drawable checkedIcon = getApplicationContext().getResources().getDrawable(R.drawable.ic_check_black_24dp);
        homeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //onSuccess
                homeAddress.setCompoundDrawables(null,null,checkedIcon,null);
            }
        });

        workAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //onSuccess
                workAddress.setCompoundDrawables(null,null,checkedIcon,null);

            }
        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequiredEditText(userName);
                RequiredEditText(homeAddress);
                RequiredEditText(dobText);
                final String name,home,workPlace,day,month,year,dateOfBirth,contactNumber;
                name=userName.getText().toString();
                home=homeAddress.getText().toString();
                workPlace=workAddress.getText().toString();
                dateOfBirth=dobText.getText().toString();


                if(TextUtils.isEmpty(workPlace)){
                    userInfoData=new UserInfoData(name,dateOfBirth,home,PHONE_NUMBER);

                }
                else {
                    userInfoData = new UserInfoData(name, dateOfBirth, workPlace, home, PHONE_NUMBER);
                    userInfo.edit().putString("workAddress",workPlace).apply();
                }
                //applying values to the info names Shared Preference

                userInfo.edit().putString("name",name).apply();
                userInfo.edit().putString("dob",dateOfBirth).apply();
                userInfo.edit().putString("home",home).apply();
                userInfo.edit().putString("uid",uid).apply();
                userInfo.edit().putString("contactNumber",PHONE_NUMBER).apply();
                userInfo.edit().putBoolean("Userinfo",true).apply();

                database = FirebaseDatabase.getInstance();
                userInfoRef = database.getReference(path).child(uid);
                userInfoRef.setValue(userInfoData);


                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

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

    //Home Address field's onCLick function
    public void setHomeAddress(View v){
        homeAddress.setText(getAddress());
    }
    //Work ADDress field's onlick funcion
    public void setWorkAddress(View v){
        workAddress.setText(getAddress());
    }

    public String getAddress(){
        //this method takes longitude and latitude and sends the latlong to the requered field
        Double lat,lon;
        String latlong;
        lat=24.29;
        lon=89.37;
        latlong=lat+","+lon;
        return latlong;
    }

}