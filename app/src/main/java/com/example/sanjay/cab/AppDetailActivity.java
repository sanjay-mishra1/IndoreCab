package com.example.sanjay.cab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AppDetailActivity extends AppCompatActivity {

    private TextView textTitle;
    private TextView textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        textTitle=findViewById(R.id.title);
        textContent=findViewById(R.id.content);
        Intent intent=getIntent();
        loadText(intent.getStringExtra("FROM"));
    }
    void loadText(String from){
        textTitle.setText(from);
        switch (from){
            case "Terms And Conditions":
               textContent.setText(getString(R.string.terms));
                break;
            case "Privacy Policy":
                textContent.setText(getString(R.string.privacy_policy));
                break;
            case "Help":
                textContent.setText(getString(R.string.help));
                break;
        }


    }
    public void backOnClicked(View view) {
        finish();
    }
}
