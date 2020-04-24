package com.example.covid_19alertapp.oldActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.activities.MainActivity;
import com.example.covid_19alertapp.dataStorage.UserInfoData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoFormActivityOLD extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference userInfoRef;
    String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    String path="UserInfo";
    EditText nameText,homeText,workText,dayText,monthText,yearText;
    UserInfoData userInfoData;
    SignUpActivityOLD signUpActivityOLD =new SignUpActivityOLD();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_form_old);
        nameText=findViewById(R.id.name);
        homeText=findViewById(R.id.homeAddress);
        workText=findViewById(R.id.workAdress);
        dayText=findViewById(R.id.day);
        monthText=findViewById(R.id.month);
        yearText=findViewById(R.id.year);
         database = FirebaseDatabase.getInstance();


    }

    public void done(View v){
        final String name,home,workAddress,day,month,year,dob,contactNumber;
        name=nameText.getText().toString();
        home=homeText.getText().toString();
        workAddress=workText.getText().toString();
        day=dayText.getText().toString();
        month=monthText.getText().toString();
        year=yearText.getText().toString();
        contactNumber= signUpActivityOLD.getPhoneNumber();


        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(home) || TextUtils.isEmpty(day)|| TextUtils.isEmpty(month)||TextUtils.isEmpty(year)){

            Toast.makeText(UserInfoFormActivityOLD.this, "Please fill the form properly", Toast.LENGTH_SHORT).show();
            return;
        }
        dob=day+"-"+month+"-"+year;
        //Since Workaddress is an optional field
        if(TextUtils.isEmpty(workAddress)){
            userInfoData=new UserInfoData(name,dob,home,contactNumber);
        }
        else
            userInfoData=new UserInfoData(name,dob,workAddress,home,contactNumber);

        database = FirebaseDatabase.getInstance();
        userInfoRef = database.getReference(path).child(uid);
        userInfoRef.setValue(userInfoData);

        startActivity(new Intent(getApplicationContext(), MainActivity.class));



    }
    //Home Address field's onCLick function
    public void setHomeAddress(View v){
        homeText.setText(getAddress());
    }
    //Work ADDress field's onlick funcion
    public void setWorkAddress(View v){
        workText.setText(getAddress());
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
