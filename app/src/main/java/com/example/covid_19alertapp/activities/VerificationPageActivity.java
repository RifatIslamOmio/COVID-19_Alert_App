package com.example.covid_19alertapp.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.covid_19alertapp.R;

public class VerificationPageActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button homeButton,confirmButton,editNumberButton;
    TextView textViewResendOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);


        homeButton = findViewById(R.id.home_button_verification_page);
        toolbar = findViewById(R.id.verification_page_toolbar);
        setSupportActionBar(toolbar);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.ISRETURNEDFROMVERLAYOUT = true;
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });



        ((EditText)findViewById(R.id.editTextDigit1)).setCursorVisible(false);
        findViewById(R.id.editTextDigit1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) findViewById(R.id.editTextDigit1)).setCursorVisible(true); }});
        ((EditText)findViewById(R.id.editTextDigit1)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.editTextDigit1)).getText().toString().length()==1)
                {
                    findViewById(R.id.editTextDigit2).clearFocus();
                    findViewById(R.id.editTextDigit2).requestFocus();
                    ((EditText) findViewById(R.id.editTextDigit2)).setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        ((EditText)findViewById(R.id.editTextDigit2)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.editTextDigit2)).getText().toString().length()==1)
                {
                    findViewById(R.id.editTextDigit3).clearFocus();
                    findViewById(R.id.editTextDigit3).requestFocus();
                    ((EditText) findViewById(R.id.editTextDigit3)).setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        ((EditText)findViewById(R.id.editTextDigit3)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.editTextDigit3)).getText().toString().length()==1)
                {
                    findViewById(R.id.editTextDigit4).clearFocus();
                    findViewById(R.id.editTextDigit4).requestFocus();
                    ((EditText) findViewById(R.id.editTextDigit4)).setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        ((EditText)findViewById(R.id.editTextDigit4)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.editTextDigit4)).getText().toString().length()==1)
                {
                    findViewById(R.id.editTextDigit5).clearFocus();
                    findViewById(R.id.editTextDigit5).requestFocus();
                    ((EditText) findViewById(R.id.editTextDigit5)).setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        ((EditText)findViewById(R.id.editTextDigit5)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.editTextDigit5)).getText().toString().length()==1)
                {
                    findViewById(R.id.editTextDigit6).clearFocus();
                    findViewById(R.id.editTextDigit6).requestFocus();
                    ((EditText) findViewById(R.id.editTextDigit6)).setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        ((EditText)findViewById(R.id.editTextDigit6)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(((EditText)findViewById(R.id.editTextDigit6)).getText().toString().length()==1)
                {
                    ((EditText) findViewById(R.id.editTextDigit6)).setCursorVisible(false);
                    findViewById(R.id.btn_continue).clearFocus();
                    findViewById(R.id.btn_continue).requestFocus();
                    hideSoftInput();
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });



        textViewResendOTP = findViewById(R.id.TextViewResendOTP);
        textViewResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"RESENDING OTP",Toast.LENGTH_SHORT).show();
                textViewResendOTP.setEnabled(false);
                textViewResendOTP.setTextColor(getResources().getColor(R.color.colorInactive));
                ToggleResendTextView(textViewResendOTP);
                //Write ResendOTP Function Here

            }
        });

        ToggleResendTextView(textViewResendOTP);
        confirmButton = findViewById(R.id.btn_continue);
        editNumberButton = findViewById(R.id.btn_change_number);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Write OTP Submission Function Here
                finish();
                startActivity(new Intent(getApplicationContext(),UserInfoFormActivity.class));

            }
        });


        editNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
                SignUpActivity.ISRETURNEDFROMVERLAYOUT = true;

            }
        });

    }

    //Methods
    public void hideSoftInput() {
        View view1 = this.getCurrentFocus();
        if(view1!= null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }
    public void ToggleResendTextView(final TextView textView)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setEnabled(true);
                textView.setTextColor(getResources().getColor(R.color.colorActive));
            }
        }, 10000);
    }
}
