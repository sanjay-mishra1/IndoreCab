package com.example.sanjay.cab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sanjay.cab.map.SearchPickup;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocationActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    LocationManager manager;
    LatLng marker ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        findViewById(R.id.doneBt).setVisibility(View.VISIBLE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
      * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
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

     }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                requestLocation();

                //findViewById(R.id.doneBt).setVisibility(View.VISIBLE);
            }catch (Exception e){Toast.makeText(this,"Not have Location permissions=>"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("MissingPermission")
    void requestLocation(){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Toast.makeText(this,"Please on your location",Toast.LENGTH_LONG).show();

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        Log.e("LOC","manager=>"+manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
          Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc!=null){
            loadMap(loc.getLatitude(),loc.getLongitude());

         }else{
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            Log.e("LOC","manager=>"+manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
              loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc!=null) {
                loadMap(loc.getLatitude(),loc.getLongitude());
            }else
            Log.e("LOC","Network also. It is null");
        }
    }
    void loadMap(double lat,double lng){
        manager.removeUpdates(this);
        Constants.pLat=lat;
        Constants.pLng=lng;
        marker=new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(marker)
                .title("Click Here to select another location"));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                startActivity(new Intent(MyLocationActivity.this,SearchPickup.class));
            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker,15));
        findViewById(R.id.doneBt).setVisibility(View.VISIBLE);
    }
    public void loadMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Constants.pLat!=0){
            if (Constants.getMyLoc&Constants.isMyLocChanged)
                requestLocation();
            else
            loadMap(Constants.pLat,Constants.pLng);
        }else{
            Log.e("LOC","Constants are empty");
        }

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


}
