package com.example.sanjay.cab.map;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.sanjay.cab.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Objects;

public class AllCars extends AsyncTask<String,Void,String> {
    private GoogleMap map;
    private BitmapDescriptor icon;
    private Context context;
    XmlResourceParser xml;
    public AllCars(GoogleMap map, Drawable markerIcon, Context context, XmlResourceParser xml) {
         this.map=map;
         this.xml=xml;
         this.context=context;
        icon=icon(markerIcon);
    }

    @Override
    protected String doInBackground(String... strings) {

        FirebaseDatabase.getInstance().getReference("map/markers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    createMarkerFromJson(dataSnapshot);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;
    }
   private void createMarkerFromJson(DataSnapshot dataSnapshot) {
       Marker mClosestMarker = null;
       float minDist = 0;
       int i=0;
       int index=0;
         for (DataSnapshot data:dataSnapshot.getChildren()) {

            Log.e("AllCars",i+"=> Found lat=>"+data.getValue());
           double lat= (double) data.child("a").child("lat").getValue();
            double lng= (double) data.child("a").child("lng").getValue();
           Log.e("AllCars",i+"=> Found lat=>"+lat+"lng=>"+lng);
            LatLng latLng=new LatLng(lat,lng);
           Marker currentMarker=map.addMarker(new MarkerOptions()
           .position(latLng)
                   .icon(icon)
           );
         try {
             new CarMarker(currentMarker,latLng,new LatLng((double) data.child("b").child("lat").getValue(),(double) data.child("b").child("lat").getValue())) ;
         }catch (Exception e){}
           float[] distance=new float[1];
           Location.distanceBetween(Constants.pLat,Constants.pLng,lat,lng,distance);
             Log.e("AllCars","ConputedLoc=>"+" mark_dis=>"+minDist);
           if (i==0){
               minDist=distance[0];
           }else if (minDist>distance[0]){
               minDist=distance[0];
               index=i;
               mClosestMarker=currentMarker;
           }
           i++;
       }
       Log.e("AllCars","Marker closest=>"+Objects.requireNonNull(mClosestMarker).getPosition()+" min_dis=>"+minDist);
        createPloyLine(mClosestMarker);
   }
   private void createPloyLine(Marker marker){
       //PolylineOptions options=new PolylineOptions().width(5).color(Color.BLACK).geodesic(true);
       GMapV2Direction md = new GMapV2Direction(context);
       Document doc = md.getDocument(new LatLng(22.7078176,75.7111283501), marker.getPosition(),xml
              ,map);


   }
   private BitmapDescriptor icon(Drawable drawable){
       Drawable vectorDrawable=drawable;
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        vectorDrawable.setBounds(20,10,drawable.getIntrinsicWidth()+20,drawable.getIntrinsicHeight()+10);
         Bitmap bitmap=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
       Canvas canvas=new Canvas(bitmap);
       drawable.draw(canvas);
       vectorDrawable.draw(canvas);
       return BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap,50,70,false));
    }
}
