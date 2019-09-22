package com.example.sanjay.cab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PersonalDetailActivity extends AppCompatActivity {
    AppCompatEditText nameEdit;
    AppCompatEditText emailEdit;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        nameEdit=findViewById(R.id.editName);
        emailEdit=findViewById(R.id.editEmailID);
        SharedPreferences sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
        mobile=sharedPreferences.getString("MOBILE","");
    }
    boolean checkName(){
        if (!Objects.requireNonNull(nameEdit.getText()).toString().isEmpty())
        {   nameEdit.setError("Please enter your full name");
            return false;
        }
        return true;
    }
    boolean checkEmail(String email){

         if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {   emailEdit.setError("Please enter a valid email");
            return true;
        }
        return false;
    }
    private void storeData(String mobileno) {
        String name=Objects.requireNonNull(nameEdit.getText()).toString();
        String email = Objects.requireNonNull(emailEdit.getText()).toString();
        if (checkName()) {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference("users/" + mobile);
            name=formatUserName(name);
            database.child("MOBILE").setValue(mobileno);
            database.child("NAME").setValue(name);

            if (!email.isEmpty()) {
                if (checkEmail(email)) {

                    database.child("EMAIL").setValue(email);
                } else return;
            }
        }
        SharedPreferences sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("USERNAME",name);
        edit.putString("MOBILE",mobile);
        edit.putString("EMAIL",email);
        edit.apply();
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public static String formatUserName(String name) {


        String[] words;
        if (!name.isEmpty()) {
            words = name.split("\\s");
            StringBuilder nameBuilder = new StringBuilder();
            for (String w : words) {
                 try {
                    nameBuilder.append(String.valueOf(w.charAt(0)).toUpperCase()).append(w.substring(1).toLowerCase()).append(" ");
                }catch (Exception e){e.printStackTrace();}
            }
            name = nameBuilder.toString();

        }
        return name;
    }
    public void doneButtonClicked(View view) {
        storeData(mobile);
    }
}
