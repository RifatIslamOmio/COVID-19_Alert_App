package com.example.covid_19alertapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.covid_19alertapp.R;


public class SignUpActivity extends AppCompatActivity {

    Button btnContinue,btnHomeSignup,btnForwardSignup;
    EditText phoneNumber;
    TextView textViewTermsCond;
    public static String PHONE_NUMBER;
    public static boolean ISRETURNEDFROMVERLAYOUT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumber = findViewById(R.id.editText_phoneNumber);
        btnContinue = findViewById(R.id.btn_continue);
        textViewTermsCond = findViewById(R.id.TextViewTerm);
        btnHomeSignup = findViewById(R.id.home_button_signup_page);
        btnForwardSignup = findViewById(R.id.forward_button_signup_page);

        if(ISRETURNEDFROMVERLAYOUT)
        {
            phoneNumber.setText(PHONE_NUMBER);
            ISRETURNEDFROMVERLAYOUT = false;
            btnHomeSignup.setVisibility(View.INVISIBLE);
            btnForwardSignup.setVisibility(View.VISIBLE);

            btnForwardSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), VerificationPageActivity.class));
                    finish();
                }
            });
        }

        phoneNumber.clearFocus();
        phoneNumber.setSelection(phoneNumber.getText().toString().length());
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            int countB=phoneNumber.getText().toString().length(),countA=0;
            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(phoneNumber.getText().toString().length()<5)
                {
                    phoneNumber.setText("+880 ");
                    phoneNumber.setSelection(phoneNumber.getText().toString().length());
                }

                countA = phoneNumber.getText().toString().length();

                if(phoneNumber.getText().toString().length()==9 && countA>countB)
                {
                    phoneNumber.setText(phoneNumber.getText().toString()+"-");
                    phoneNumber.setSelection(phoneNumber.getText().toString().length());
                }
                countB = countA;
                if(phoneNumber.getText().toString().length()==16)
                {
                    hideSoftInput();
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) phoneNumber.setCursorVisible(true);
                else phoneNumber.setCursorVisible(false);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().length()==16)  //Write a function to check phone number validity
                {
                    PHONE_NUMBER = phoneNumber.getText().toString();
                    startActivity(new Intent(getApplicationContext(), VerificationPageActivity.class));
                    finish();
                    //Write OTP Request Function
                }
                else
                {
                    phoneNumber.setError("Invalid Number!");
                }
            }
        });

        textViewTermsCond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Write Terms and Condition Page Function
                textViewTermsCond.setTextColor(getResources().getColor(R.color.colorInactive));
            }
        });


        btnHomeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void hideSoftInput() {
        View view1 = this.getCurrentFocus();
        if(view1!= null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }
}
