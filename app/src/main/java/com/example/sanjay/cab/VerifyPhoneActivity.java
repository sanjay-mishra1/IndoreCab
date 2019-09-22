package com.example.sanjay.cab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {


    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;
    private String phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);

          phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

    }

    private void verifyCode(String code) {
       try {
           PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
           signInWithCredential(credential);
       }catch (Exception e){
           editText.setError("Invalid code...");
       }

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUser();


                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.e("OTP","Code sent");
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Log.e("OTP","Code verfication =>"+code);
                editText.setText(code);
                verifyCode(code);
            }else {
                    Log.e("OTP","Code verfication =>null=>");
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };

    private void checkUser(){
        FirebaseDatabase.getInstance().getReference("users/"+phonenumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {   SharedPreferences sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                    edit.putString("USERNAME",(String)dataSnapshot.child("USERNAME").getValue());
                    edit.putString("MOBILE",(String)dataSnapshot.child("MOBILE").getValue());
                    String img=(String)dataSnapshot.child("USERIMG").getValue();
                    LatLng latLng=(LatLng)dataSnapshot.child("home").child("COORDINATES").getValue();
                    if (latLng!=null) {
                        edit.putString("HOMELAT", String.valueOf(latLng.latitude));
                        edit.putString("HOMELNG", String.valueOf(latLng.longitude));
                        edit.putString("HOMENAME",(String) dataSnapshot.child("home").child("NAME").getValue());

                    }
                    latLng=(LatLng)dataSnapshot.child("work").child("COORDINATES").getValue();

                    if (latLng!=null) {

                        edit.putString("WORKLAT", String.valueOf(latLng.latitude));
                        edit.putString("WORKLNG", String.valueOf(latLng.longitude));
                        edit.putString("WORKNAME",(String) dataSnapshot.child("work").child("NAME").getValue());
                    }
                   if (img!=null)
                    edit.putString("USERIMG",img);
                    String email=(String)dataSnapshot.child("EMAIL").getValue();
                    if (email!=null)
                    edit.putString("EMAIL",email);
                    edit.apply();
                    Intent intent = new Intent(VerifyPhoneActivity.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }else{
                    SharedPreferences sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                     edit.putString("MOBILE",(String)dataSnapshot.child(phonenumber).getValue());
                     edit.apply();
                    Intent intent = new Intent(VerifyPhoneActivity.this, PersonalDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
