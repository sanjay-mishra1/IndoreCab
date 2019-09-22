package com.example.sanjay.cab.map;

import android.location.Address;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyGeocoder extends AsyncTask<Object,Void,List> {
    MyGeocoder(){
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    private static final String TAG = MyGeocoder.class.getSimpleName();

    private static OkHttpClient client = new OkHttpClient();

    public static List<Address> getFromLocation(double lat, double lng, int maxResult) {

        String address = String.format(Locale.US,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language="
                        + Locale.getDefault().getCountry(), lat, lng);
        Log.d(TAG, "address = " + address);
        Log.d(TAG, "Locale.getDefault().getCountry() = " + Locale.getDefault().getCountry());

        return getAddress(address, maxResult);

    }

    public static List<Address> getFromLocationName(String locationName, int maxResults)  {

        String address = null;
        try {
            address = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyBo1jT616ex45y5XrWJDErEpuyLiHvprX8&address=" + URLEncoder.encode(locationName,
                    "UTF-8") + "&ka&sensor=false";
            return getAddress(address, maxResults);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Address> getAddress(String url, int maxResult) {
        List<Address> retList = null;
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Request request = new Request.Builder().url(url)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseStr = response.body().string();
            JSONObject jsonObject = new JSONObject(retrurnCustomRespnose());

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    for (int i = 0; i < results.length() && i < maxResult; i++) {
                        JSONObject result = results.getJSONObject(i);
                        Address addr = new Address(Locale.getDefault());

                        JSONArray components = result.getJSONArray("address_components");
                        String streetNumber = "";
                        String route = "";
                        for (int a = 0; a < components.length(); a++) {
                            JSONObject component = components.getJSONObject(a);
                            JSONArray types = component.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                String type = types.getString(j);
                                switch (type) {
                                    case "locality":
                                        addr.setLocality(component.getString("long_name"));
                                        break;
                                    case "street_number":
                                        streetNumber = component.getString("long_name");
                                        break;
                                    case "route":
                                        route = component.getString("long_name");
                                        break;
                                }
                            }
                        }
                        addr.setAddressLine(0, route + " " + streetNumber);

//                        addr.setLatitude(
//                                result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
//                        addr.setLongitude(
//                                result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        addr.setLatitude(22.7592275);
                        addr.setLongitude(75.9368937);
                        retList.add(addr);
                    }
                }
            }

            Log.e("Geocoding","Response=>"+responseStr+"\n"+retList);

        } catch (IOException e) {
            Log.e(TAG, "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing Google geocode webservice response.", e);
        }
        return retList;
    }

    @Override
    protected List doInBackground(Object... objects) {

        return getFromLocationName(objects[0].toString(),(int)objects[1]);
    }
    static String retrurnCustomRespnose(){
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