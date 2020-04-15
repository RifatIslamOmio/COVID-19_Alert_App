package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText phoneNoEdit, enteredCodeEdit,countrCode;

    String verification,enterededCodeString,uid;
    private static String phoneNumberString ="";
    SharedPreferences sp; //sp is going to be used to keep users logged in

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        phoneNoEdit =findViewById(R.id.mobileNo);
        enteredCodeEdit =findViewById(R.id.verificationCode);
        countrCode=findViewById(R.id.countr_code);
        auth=FirebaseAuth.getInstance();
        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

               // Toast.makeText(getApplicationContext(),"Code ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                e.printStackTrace();
                System.out.println(e+" exceptions");
                Toast.makeText(getApplicationContext(),"Please try again later ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification=s;
                System.out.println(s +" verification "+ verification);

                Toast.makeText(getApplicationContext(),"Code Sent to the Number",Toast.LENGTH_SHORT).show();
            }
        };


    }
    //SendSms Button
    public void sendSms(View view){
        phoneNumberString="+880"+phoneNoEdit.getText().toString();
        if(TextUtils.isEmpty(phoneNumberString)){
            Toast.makeText(SignUpActivity.this, "Please enter your mobile number ", Toast.LENGTH_SHORT).show();
            return;
        }
        if( phoneNumberString.length()!=14){
            Toast.makeText(SignUpActivity.this, "Please enter your valid phone number ", Toast.LENGTH_SHORT).show();
            return;

        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumberString,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks         // OnVerificationStateChangedCallbacks
        );
        phoneNoEdit.setEnabled(false);
        countrCode.setEnabled(false);
           // OnVerificationStateChangedCallbacks



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            //System.out.println("Successful");
                            FirebaseUser user = task.getResult().getUser();
                            uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            ifDataExist(uid);

                            Toast.makeText(getApplicationContext(),"User Signed In Successfully",Toast.LENGTH_SHORT).show();
                            sp.edit().putBoolean("logged",true).apply();

                        } else {
                            //System.out.println(task.getException()+" task exception");
                            Toast.makeText(getApplicationContext(),"Please use the valid code",Toast.LENGTH_SHORT).show();
                            // Sign in failed, display a message and update the UI


                        }
                    }
                });
    }
    //verify button
    public void verify(View v){
        enterededCodeString=enteredCodeEdit.getText().toString();
        if(TextUtils.isEmpty(enterededCodeString)){
            Toast.makeText(SignUpActivity.this, "Please enter the varification code", Toast.LENGTH_SHORT).show();
            return;
        }


        verfyPhoneNumber(verification,enterededCodeString);
    }

    private void verfyPhoneNumber(String verification, String enterededCodeString) {

        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verification,enterededCodeString);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }



    public void ifDataExist(final String uid){

        FirebaseDatabase database = FirebaseDatabase.getInstance();




        DatabaseReference ref = database.getReference().child("UserInfo");

        ValueEventListener valueEventListener = new ValueEventListener() {
            {System.out.println("method");}
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(uid).exists()){

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else {

                    startActivity(new Intent(getApplicationContext(), UserInfoFormActivity.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);

    }

    public void goToMainActivity(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
    public  String getPhoneNumber() {
        return phoneNumberString;
    }
}
