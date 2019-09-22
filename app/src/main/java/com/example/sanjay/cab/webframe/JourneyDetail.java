package com.example.sanjay.cab.webframe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.cab.CountryData;
import com.example.sanjay.cab.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class JourneyDetail extends Fragment {
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.journey_detail_layout,container,false);
        spinner = v.findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        loadDriverDetail();
        return v;
    }

    private void loadMoreOption() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                moreOption(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void loadDriverDetail(){
        new DriverDetails(Objects.requireNonNull(this.getArguments()).getString("DRIVERID")).execute();
    }
    void loadListners(final String mobile, final String tripId){
        loadOtp();
        getView().findViewById(R.id.callDriver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDriver(mobile);
            }
        });
        getView().findViewById(R.id.cancelTrip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTrip(tripId);
            }


        });
        loadMoreOption();
    }

    private void loadOtp() {
        Random random=new Random();
        TextView text=Objects.requireNonNull(getView()).findViewById(R.id.otp);
        text.append(String.format(Locale.ENGLISH,"%04d",random.nextInt(10000)));
    }

    private void moreOption(int index) {
       switch (index){
           case 0:
               break;
           case 1:
               break;
           case 2:
               break;
       }
    }

    private void callDriver(String mobile) {
        call("tel:+91"+mobile);
    }
    private void cancelTrip(String tripId) {
    }
    private void call(String url) {
        if (isHaveCallPermision()) {


            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(url));
            startActivity(callIntent);
        }else{
            Toast.makeText(getContext(),"Not have call permission",Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean isHaveCallPermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
                return false;
            }else{

                return true;
            }
        }else{
            return true;
        }

    }
     class DriverDetails extends AsyncTask<String,Void,String>{
        DatabaseReference databaseReference;
        DriverDetails(String driverId){
              databaseReference=FirebaseDatabase.getInstance().getReference("drivers/"+driverId);
        }
        void setText(String text, TextView textView){
          try {
              textView.setText(text);
          }catch (Exception e){
              e.printStackTrace();
          }
        }
         void setImage(String url, ImageView imageView){
             Glide.with(Objects.requireNonNull(getActivity())).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(url).into(imageView);
         }
         void setCircularImage(String url, ImageView imageView){
             Glide.with(Objects.requireNonNull(getActivity())).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imageView);
         }
        @Override
        protected String doInBackground(String... strings) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   try {
                       String carno = (String) dataSnapshot.child("CARNO").getValue();

                       setText(carno.substring(0, Objects.requireNonNull(carno).length() - 4), (TextView) Objects.requireNonNull(getView()).findViewById(R.id.carStartingNo));
                       setText(carno.substring(Objects.requireNonNull(carno).length() - 4), (TextView) Objects.requireNonNull(getView()).findViewById(R.id.carEndNo));
                       String car_img = (String) dataSnapshot.child("CAR_IMG").getValue();
                       setImage(car_img, (ImageView) getView().findViewById(R.id.carImage));
                       String driver_img = (String) dataSnapshot.child("DRIVER_IMG").getValue();
                       setCircularImage(driver_img, (ImageView) getView().findViewById(R.id.driverImage));
                       String driver_name = (String) dataSnapshot.child("NAME").getValue();
                       setText(driver_name, (TextView) getView().findViewById(R.id.driverName));
                       String car_name = (String) dataSnapshot.child("CAR_NAME").getValue();
                       setText(car_name, (TextView) getView().findViewById(R.id.carname));
                       String rating = String.valueOf(dataSnapshot.child("RATING").getValue());
                       setText(rating, (TextView) getView().findViewById(R.id.driverRating));
                       String mobile = (String) dataSnapshot.child("MOBILE").getValue();
                       Objects.requireNonNull(getActivity()).findViewById(R.id.fragment).setVisibility(View.VISIBLE);
                       loadListners(mobile, "");
                   }catch (Exception e){
                       e.printStackTrace();
                   }

                 }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Database",databaseError.getDetails());
                }
            });
            return null;
        }
    }

}
