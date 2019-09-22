package com.example.sanjay.cab.map;

import android.app.Activity;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sanjay.cab.Constants;
import com.example.sanjay.cab.MapsActivity;
import com.example.sanjay.cab.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class AddressBarFragment extends Fragment {
    ArrayAdapter arrayAdapter;
    private ListView listView;
    GoogleMap googleMap;
    LatLng latLng;
    MarkerOptions markerOptions;
    Marker marker;
    TextView textView;
     private TextView destText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.address_bar_fragment,container,false);
        listView=view.findViewById(R.id.searchResultList);
        googleMap=MapsActivity.mMap;
         destText=getActivity().findViewById(R.id.destination);
      try {
          loadListners();
      }catch (Exception e){
          e.printStackTrace();
      }
        searchBarListener();
        return view;
    }
    void searchBarListener(){

        EditText editText=(EditText)getActivity().findViewById(R.id.addressEdit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    List<Address> list = MyGeocoder.getFromLocationName(editable.toString(), 5);
                    List record=new ArrayList<>();
                    for (int i=0;i<list.size();i++)
                    {   Address address=list.get(i);
                        record.add(address.getAddressLine(0));
                    }

                    arrayAdapter = new ArrayAdapter<Address>(getContext(),R.layout.address_layout, record);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener((adapterView, view, position, l) -> {
                        Address address = (Address) list.get(position);
                        Constants.dLat = address.getLatitude();
                        Constants.dLng = address.getLongitude();
                        destText.setText(list.get(position).getAddressLine(0));
                        getActivity().findViewById(R.id.userImage).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.address_card).setVisibility(View.VISIBLE);
                        if (marker==null){
                        latLng=new LatLng(address.getLatitude(),address.getLongitude());
                        markerOptions=new MarkerOptions().position(latLng);
                       marker= googleMap.addMarker(markerOptions);
                        }else {
                            latLng=new LatLng(address.getLatitude(),address.getLongitude());
                            marker.setPosition(latLng);
                        }

                     //   Constants.isMyLocChanged = true;

                          BottomSheetBehavior<CardView> bottomSheetBehavior;
                      CardView  addressCard=getActivity(). findViewById(R.id.address_sheet);

                        bottomSheetBehavior=BottomSheetBehavior.from(addressCard);
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View  view1=getActivity().getCurrentFocus();
                        if (view1==null)
                            view1=new View(getActivity());
                        imm.hideSoftInputFromWindow(view1.getWindowToken(),0);

                        addressCard=getActivity(). findViewById(R.id.carType);
                        addressCard.setVisibility(View.VISIBLE);
                        bottomSheetBehavior=BottomSheetBehavior.from(addressCard);
                        bottomSheetBehavior.setHideable(false);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        Log.e("GeoCoding", "Query=>" + editable + "\nResponse=>" + list);


                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void loadListners() {
        Objects.requireNonNull(getView()).findViewById(R.id.homeBar).setOnClickListener(view -> {

        });
        getView().findViewById(R.id.workBar).setOnClickListener(view -> {

        });
        getView().findViewById(R.id.favBar).setOnClickListener(view -> {

        });
    }

}
