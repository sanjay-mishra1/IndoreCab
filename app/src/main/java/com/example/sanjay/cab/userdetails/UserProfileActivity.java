package com.example.sanjay.cab.userdetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.cab.AppDetailActivity;
import com.example.sanjay.cab.MainActivity;
import com.example.sanjay.cab.MapsActivity;
import com.example.sanjay.cab.R;
 import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
        setUserDetail();
        setLocDetail();
    }
    private void setUserDetail(){
        SharedPreferences sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
        ImageView imageView=findViewById(R.id.userImage);
        Glide.with(this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(sharedPreferences.getString("USERIMG","")).into(imageView);
        TextView textname=findViewById(R.id.username);
        textname.setText(sharedPreferences.getString("USERNAME",""));
        TextView textmobile=findViewById(R.id.userMobile);
        String mobile=sharedPreferences.getString("MOBILE","");
        textmobile.setText("+91 "+mobile);
          databaseReference=FirebaseDatabase.getInstance().getReference("users/"+mobile);
        TextView textemail=findViewById(R.id.userEmail);
        String email=sharedPreferences.getString("EMAIL","Enter email id");
        if (!email.isEmpty())
        {   textemail.setVisibility(View.VISIBLE);
            textemail.setText(email);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserDetail();
    }

    private void setLocDetail(){
        TextView homeText=findViewById(R.id.homeLoc);
        String home=sharedPreferences.getString("HOMENAME","");
        if (!home.isEmpty())
        { homeText.setText(home);
         homeText.setVisibility(View.VISIBLE);
         findViewById(R.id.deleteLoc).setVisibility(View.VISIBLE);
        }
        String work=sharedPreferences.getString("WORKNAME","");
        TextView textWork=findViewById(R.id.workLoc);

        if (!work.isEmpty())
        { textWork.setText(work);
            textWork.setVisibility(View.VISIBLE);
            findViewById(R.id.deleteWorkLoc).setVisibility(View.VISIBLE);
        }
        textWork.setText(sharedPreferences.getString("WORKNAME",""));

    }
    public void editUserClicked(View view) {
        startActivity(new Intent(this,PersonalDetailsActivity.class));
    }

    public void deleteHomeClicked(View view) {
        databaseReference.child("home").setValue(null);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("HOMENAME",null);
        edit.putString("HOMELAT",null);
        edit.putString("HOMELNG",null);
        edit.apply();
    }

    public void deleteWorkClicked(View view) {
        databaseReference.child("work").setValue(null);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("WORKNAME",null);
        edit.putString("WORKLAT",null);
        edit.putString("WORKLNG",null);
        edit.apply();
    }

    public void seeAllTripsClicked(View view) {

    }

    public void signoutClicked(View view) {
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.clear();
        edit.apply();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    public void termsConditionClicked(View view) {
        Intent intent=new Intent(this,AppDetailActivity.class);
        intent.putExtra("FROM","Terms And Conditions");
        startActivity(intent);
    }

    public void helpClicked(View view) {
        Intent intent=new Intent(this,AppDetailActivity.class);
        intent.putExtra("FROM","Help");
        startActivity(intent);
    }

    public void privacyClicked(View view) {
        Intent intent=new Intent(this,AppDetailActivity.class);
        intent.putExtra("FROM","Privacy Policy");
        startActivity(intent);
    }

    public void backOnClicked(View view) {
        finish();
    }


}
