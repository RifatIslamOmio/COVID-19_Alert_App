package com.example.covid_19alertapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.models.Post;
import com.example.covid_19alertapp.extras.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button homeBtnPost, postBtn;
    EditText postText;
    RadioGroup radioGroup;
    String postType= "QUERY";
    int reliefbtnID,radioID;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        homeBtnPost = findViewById(R.id.home_button_new_post_page);
        postBtn = findViewById(R.id.btn_post);
        postText = findViewById(R.id.editText_post);
        radioGroup = findViewById(R.id.radioGroup);
        reliefbtnID = R.id.radBtnRelief;
        homeBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post_body = postText.getText().toString();
                if(post_body.length()==0)
                {
                    postText.setError("Post body can not be empty!");
                }
                else
                {
                    //Post It

                    radioID = radioGroup.getCheckedRadioButtonId();
                    if(radioID==reliefbtnID) { postType = "RELIEF"; }

                    sharedPreferences = getSharedPreferences(Constants.USER_INFO_SHARED_PREFERENCES,MODE_PRIVATE);
                    String post_ID = reference.push().getKey();
                    String user_name = sharedPreferences.getString(Constants.username_preference,null);
                    String user_ID = sharedPreferences.getString(Constants.uid_preference,null);
                    //post_body

                    Calendar cal=Calendar.getInstance();
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    String post_date = dateFormat.format(cal.getTime())+" "+monthFormat.format(cal.getTime());
                    String post_time = timeFormat.format(cal.getTime());

                    String contact_NO = sharedPreferences.getString(Constants.user_phone_no_preference,null);
                    String post_type = postType;

                    Post post = new Post(post_ID,user_name,user_ID,post_body,post_date,post_time,contact_NO,post_type);
                    reference.child(post_ID).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(getApplicationContext(),"Posted to Feed!",Toast.LENGTH_SHORT).show();
                            showCustomAlert();
                            finish();
                        }
                    });
                }
            }
        });

    }


    public void showCustomAlert()
    {
        Context context = getApplicationContext();
        LayoutInflater inflater = getLayoutInflater();
        View toastView = inflater.inflate(R.layout.custom_toast, null);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
