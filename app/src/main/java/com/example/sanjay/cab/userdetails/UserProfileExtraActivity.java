package com.example.sanjay.cab.userdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.sanjay.cab.R;

public class UserProfileExtraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_extra);

    }

    public void shareAppClicked(View view) {
        String shareBody="here is the share content body";
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(shareIntent,getResources().getString(R.string.share_your_invite_code)));

    }

    public void backOnClicked(View view) {
        finish();
    }
}
