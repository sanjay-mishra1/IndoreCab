package com.example.sanjay.cab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
 import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sanjay.cab.map.AddressBarFragment;
import com.example.sanjay.cab.map.AllCars;
import com.example.sanjay.cab.map.SearchPickup;
import com.example.sanjay.cab.userdetails.UserProfileActivity;
import com.example.sanjay.cab.userdetails.UserProfileExtraActivity;
import com.example.sanjay.cab.webframe.JourneyDetail;
 import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;


public class MapsActivity extends   FragmentActivity implements OnMapReadyCallback,LocationListener {

   static public GoogleMap mMap;
    LocationManager manager;
    LatLng marker ;
    private SharedPreferences sharedPreferences;
    private TextView textUser;
    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private int height;
    private CardView addressCard;
    CoordinatorLayout.LayoutParams paramsBig;
    CoordinatorLayout.LayoutParams paramSmall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
         paramsBig=new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
         paramSmall=new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
       try {
             addressCard=findViewById(R.id.address_sheet);

        bottomSheetBehavior=BottomSheetBehavior.from(addressCard);
    }catch (Exception e) {
           e.printStackTrace();
       }
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height= displayMetrics.heightPixels;
         textUser=findViewById(R.id.username);
        sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);

        userCred();
        loadAddressDialog();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadCarType();
      }
     @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        Log.e("MAPACT","PICkUP Coord. "+Constants.pLat+","+Constants.pLng);
//        marker = new LatLng(Constants.pLat,Constants.pLng);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
//        loadMap(Constants.pLat,Constants.pLng);
         mMap = googleMap;
         marker = new LatLng(22.6752479,75.8745949);
         mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
         manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             String[]permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
             ActivityCompat.requestPermissions(this,
                     permission,
                     101);

             return;
         }
         requestLocation();


        drawCars();

    }
    @SuppressLint("MissingPermission")
    void requestLocation(){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Toast.makeText(this,"Please on your location",Toast.LENGTH_LONG).show();

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (LocationListener) this);
        Log.e("LOC","manager=>"+manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc!=null){
            manager.removeUpdates(this);
            loadMap(loc.getLatitude(),loc.getLongitude());

        }else{
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, (LocationListener) this);
            Log.e("LOC","manager=>"+manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc!=null) {
                manager.removeUpdates(this);
                loadMap(loc.getLatitude(),loc.getLongitude());
            }else
                Log.e("LOC","Network also. It is null");
        }
    }
    void loadMap(double lat,double lng){
         Constants.pLat=lat;
        Constants.pLng=lng;
        marker=new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(marker)
                .title("Click Here to select another location"));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                startActivity(new Intent(MapsActivity.this,SearchPickup.class));
            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker,15));
     }
     void drawCars(){
        new AllCars(mMap,getResources().getDrawable(R.drawable.ubericon),getApplicationContext(),getResources().getXml(R.xml.xml)).execute();
     }
   public void backOnClicked(View view) {
        finish();
    }

    public void backToMyLocationOnClicked(View view) {
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Constants.pLat,Constants.pLng)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.pLat,Constants.pLng),15));
    }


    public void userProfileClicked(View view) {
        startActivity(new Intent(this,UserProfileActivity.class));
    }

    public void profileExtraClicked(View view) {
        startActivity(new Intent(this,UserProfileExtraActivity.class));
    }

    void userCred(){

//        Glide.with(this).load(sharedPreferences.getString("USERIMG","")).into((FloatingActionButton) findViewById(R.id.userImage));
       textUser.setText(String.format("Hey, %s", Objects.requireNonNull(sharedPreferences.getString("USERNAME", "")).split(" ")[0]));
    }

   void loadAddressDialog(){
        TextView textView=findViewById(R.id.addressText);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_EXPANDED){
                    findViewById(R.id.arrowAdressbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.addressEdit).setVisibility(View.VISIBLE);
                    findViewById(R.id.addressbarExtra).setVisibility(View.GONE);

                }else{
                    findViewById(R.id.arrowAdressbar).setVisibility(View.GONE);
                    findViewById(R.id.addressEdit).setVisibility(View.GONE);
                 }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              bottomSheetBehavior.setPeekHeight(height);
                findViewById(R.id.arrowAdressbar).setVisibility(View.VISIBLE);
                findViewById(R.id.addressEdit).setVisibility(View.VISIBLE);
                findViewById(R.id.addressbarExtra).setVisibility(View.GONE);

                AddressBarFragment journeyDetail=new AddressBarFragment();
                Bundle bundle=new Bundle();

                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.addressBarFrag, journeyDetail);
                fragmentTransaction1.commit();
                makeAddressbarFull();
            }
        });
       disableAddressBarClicked(findViewById(R.id.arrowAdressbar),textView);

   }

    @Override
    protected void onStart() {
        super.onStart();
        userCred();
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc!=null){
            Log.e("LOC","Found location changed "+loc);
            requestLocation();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e("LOC","Found status changed s=>"+s+"=>"+bundle);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e("LOC","Provider enabled s=>"+s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e("LOC","Provider disabled s=>"+s);
    }


    public void disableAddressBarClicked(View view, final TextView textView) {
        findViewById(R.id.arrowAdressbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    Log.e("Arrow", "Hiding adressbar");
                    view.setVisibility(View.GONE);
                    makeAddressbarNormal();
                     textView.setVisibility(View.VISIBLE);

                } else Log.e("Arrow", "Hiding adressbar");
            }

        });
    }
    void makeAddressbarFull(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        paramSmall.setBehavior(bottomSheetBehavior);
//        addressCard.setLayoutParams(paramsBig);
    }
    void makeAddressbarNormal(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        paramSmall.gravity=Gravity.BOTTOM;
//        paramSmall.setBehavior(bottomSheetBehavior);
//        addressCard.setLayoutParams(paramSmall);
    }

    void loadCarType(){
        Button button=findViewById(R.id.carTypeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               CardView addressCard= findViewById(R.id.carType);
                addressCard.setVisibility(View.VISIBLE);
                BottomSheetBehavior<CardView> bottomSheetBehavior1=BottomSheetBehavior.from(addressCard);
                bottomSheetBehavior1.setHideable(true);
                bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                findViewById(R.id.userImage).setVisibility(View.VISIBLE);
                button.setOnClickListener(null);

                Bundle bundle=new Bundle();
                bundle.putString("DRIVERID","3");
                JourneyDetail journeyDetail=new JourneyDetail();
                journeyDetail.setArguments(bundle);
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.fragment, journeyDetail);
                fragmentTransaction1.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }
}

