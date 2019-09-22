package com.example.sanjay.cab.map;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sanjay.cab.Constants;
import com.example.sanjay.cab.MyLocationActivity;
import com.example.sanjay.cab.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchPickup extends AppCompatActivity {
    private static final String TAG = "GEO";
    ArrayAdapter arrayAdapter;
    List<Address> list;
    private ListView listView;
    private Geocoder geocoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pickup);
        initializeList();
          geocoder=new Geocoder(this);
        initializeSearchView();
    }
    void initializeList(){
          listView=findViewById(R.id.listView);
        list=new ArrayList<>();



    }
    void initializeSearchView(){
        EditText searchView=findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
             loadQuery(editable.toString());
            }
        });
    }

    private  void loadQuery(String search){

        list=new ArrayList<>();
        new ReverseGeoCodeTask(getResources().getString(R.string.google_maps_key)).execute(search);
//        try {list=geocoder.getFromLocationName(search,5);
//            Log.e("QUERY",""+list);
//
            arrayAdapter=new ArrayAdapter<Address>(this,android.R.layout.simple_list_item_1,list);
//            listView.setAdapter(arrayAdapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    Address address=list.get(position);
//                    Constants.pLat=address.getLatitude();
//                    Constants.pLng=address.getLongitude();
//                    Constants.isMyLocChanged=true;
//                    finish();
//                }
//            });
//        }catch (Exception e){e.printStackTrace();}
    }

    public void backOnClicked(View view) {
        finish();
    }

    public void useMyLocationClicked(View view) {
        Constants.getMyLoc=true;
        finish();
    }

  private class ReverseGeoCodeTask extends AsyncTask<String, Void, String>{

        private final static String GEOCODE_API_ENDPOINT_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        private final static String JSON_PROPERTY_RESULTS = "results";
        private final static String JSON_PROPERTY_FORMATTED_ADDRESS = "formatted_address";
        private final static String JSON_PROPERTY_REQUEST_STATUS = "status";
        private final static String STATUS_OK = "OK";
        private String apiKey;

        ReverseGeoCodeTask(final String apiKey){
            this.apiKey = apiKey;
        }

        @Override
        protected String doInBackground(String... params) {

            if(apiKey == null){
                throw new IllegalStateException("Pass in a geocode api key in the ReverseGeoCoder constructor");
            }

//            Location location = params[0];
            String googleGeocodeEndpoint = null;
            try {
                googleGeocodeEndpoint = GEOCODE_API_ENDPOINT_BASE + URLEncoder.encode(params[0], "UTF-8") + "&key=" + apiKey;

            Log.d(TAG, "Requesting gecoding endpoint : " + googleGeocodeEndpoint);

//                URL url = new URL(googleGeocodeEndpoint);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                StringBuilder result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//                Log.e("SearchResponse","Url=>"+url);
//                StringRequest request=new StringRequest(Request.Method.POST, url.toString(), new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("SearchResponse","from volley=>"+response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("SearchResponse","from volley=>"+"error"+error.getLocalizedMessage());
//                    }
//                });
//                Volley.newRequestQueue(getApplicationContext()).add(request);
//

                        JSONObject json = new JSONObject(retrurnCustomRespnose());
                String requestStatus = json.getString(JSON_PROPERTY_REQUEST_STATUS);
                if(requestStatus.equals(STATUS_OK)){
                    JSONArray results = json.getJSONArray(JSON_PROPERTY_RESULTS);
                    if(results.length() > 0){
                        JSONObject result1 = results.getJSONObject(0);
                        String address =  result1.getString(JSON_PROPERTY_FORMATTED_ADDRESS);
                        Log.d(TAG, "First result's address : " + address );
                        return  address;


                    }
                    else{
                        Log.d(TAG, "There were no results.");
                    }
                }
                else{
                    Log.w(TAG, "Geocode request status not " + STATUS_OK + ", it was " + requestStatus );
                    Log.w(TAG, "Did you enable the geocode in the google cloud api console? Is it the right api key?");
                }


            }catch ( IOException | JSONException e){

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            if(address != null){
                // update the UI here with the address, if its not null
//                originEditText.setText(address);
                Log.d(TAG, address);

            }
            else{
                Log.d(TAG, "Did not find an address, UI not being updated");
            }

        }
    }
    String retrurnCustomRespnose(){
        String json="{" +
                "   \"results\" : [" +
                "      {" +
                "         \"address_components\" : [" +
                "            {" +
                "               \"long_name\" : \"# 190\"," +
                "               \"short_name\" : \"# 190\"," +
                "               \"types\" : [ \"subpremise\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"39675\"," +
                "               \"short_name\" : \"39675\"," +
                "               \"types\" : [ \"street_number\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"Cedar Boulevard\"," +
                "               \"short_name\" : \"Cedar Blvd\"," +
                "               \"types\" : [ \"route\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"Newark\"," +
                "               \"short_name\" : \"Newark\"," +
                "               \"types\" : [ \"locality\", \"political\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"Alameda County\"," +
                "               \"short_name\" : \"Alameda County\"," +
                "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"California\"," +
                "               \"short_name\" : \"CA\"," +
                "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"United States\"," +
                "               \"short_name\" : \"US\"," +
                "               \"types\" : [ \"country\", \"political\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"94560\"," +
                "               \"short_name\" : \"94560\"," +
                "               \"types\" : [ \"postal_code\" ]" +
                "            }," +
                "            {" +
                "               \"long_name\" : \"5491\"," +
                "               \"short_name\" : \"5491\"," +
                "               \"types\" : [ \"postal_code_suffix\" ]" +
                "            }" +
                "         ]," +
                "         \"formatted_address\" : \"39675 Cedar Blvd # 190, Newark, CA 94560, USA\"," +
                "         \"geometry\" : {" +
                "            \"location\" : {" +
                "               \"lat\" : 37.520283," +
                "               \"lng\" : -121.996313" +
                "            }," +
                "            \"location_type\" : \"ROOFTOP\"," +
                "            \"viewport\" : {" +
                "               \"northeast\" : {" +
                "                  \"lat\" : 37.5216319802915," +
                "                  \"lng\" : -121.9949640197085" +
                "               }," +
                "               \"southwest\" : {" +
                "                  \"lat\" : 37.5189340197085," +
                "                  \"lng\" : -121.9976619802915" +
                "               }" +
                "            }" +
                "         }," +
                "         \"place_id\" : \"ChIJ87No5VS_j4ARybL_P2Eiggc\"," +
                "         \"plus_code\" : {" +
                "            \"compound_code\" : \"G2C3+4F Newark, California, United States\"," +
                "            \"global_code\" : \"849WG2C3+4F\"" +
                "         }," +
                "         \"types\" : [ \"establishment\", \"insurance_agency\", \"point_of_interest\" ]" +
                "      }" +
                "   ]," +
                "   \"status\" : \"OK\"" +
                "}";
        return json;
    }
}
