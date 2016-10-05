package com.alivenet.dmvtaxi.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.adapter.DropLocationRecyclerViewAdapter;
import com.alivenet.dmvtaxi.adapter.PickUpLocationRecyclerViewAdapter;
import com.alivenet.dmvtaxi.adapter.PresetLocationRecyclerViewAdapter;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.commonutil.WrappingLinearLayoutManaager;
import com.alivenet.dmvtaxi.pojo.DropLocationResource;
import com.alivenet.dmvtaxi.pojo.PickUpLocationResource;
import com.alivenet.dmvtaxi.pojo.PreSetLocationResource;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/17/2016.
 */
public class FragmentLocation extends Fragment implements View.OnClickListener {

    private GridLayoutManager presetGridLayoutManager;
    private RecyclerView recyclerView, recyclerView_dropLocation, recyclerView_pickupLocation;
    LinearLayoutManager llm;
    private int PLACE_PICKER_REQUEST;
    private LatLngBounds BOUNDS_MOUNTAIN_VIEW;
    String address1;
    private static double lat;
    private static double lng;

    TextView metpickupHome, metpickupOffice, metpickupOther, metdropHome, metdropOffice, metdropOther;
    ImageView imgebtnpickupHome, imgebtnpickupOffice, imgebtnpickupOther, imgebtndropHome, imgebtndropOffice, imgebtndropOther;

    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    String mUserId;
    ProgressDialog prgDialog;
    Button btnSave;

    String home_pickup_Address, home_pickup_shortAddress, home_pickup_lat, home_pickup_long;
    String office_pickup_Address, office_pickup_shortAddress, office_pickup_lat, office_pickup_long;
    String other_pickup_Address, other_pickup_shortAddress, other_pickup_lat, other_pickup_long;

    String home_drop_Address, home_drop_shortAddress, home_drop_lat, home_drop_long;
    String office_drop_Address, office_drop_shortAddress, office_drop_lat, office_drop_long;
    String other_drop_Address, other_drop_shortAddress, other_drop_lat, other_drop_long;


    private ArrayList<PreSetLocationResource> mPresetLocationList = new ArrayList<PreSetLocationResource>();
    private ArrayList<DropLocationResource> mDropLocationList = new ArrayList<DropLocationResource>();
    private ArrayList<PickUpLocationResource> mPickUpLocationList = new ArrayList<PickUpLocationResource>();

    public static FragmentLocation newInstance(String sectionTitle) {
        FragmentLocation fragment = new FragmentLocation();
        Bundle args = new Bundle();
        // args.putString(ARG_SECTION_TITLE, sectionTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_frequent_location, container, false);
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);


        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        btnSave = (Button) view.findViewById(R.id.btn_puckupres);
        /////////preset Location///////////////////

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_presertLocation);
        recyclerView.setHasFixedSize(true);
        presetGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(presetGridLayoutManager);


        metdropHome = (TextView) view.findViewById(R.id.edit_home_drop);
        metdropOffice = (TextView) view.findViewById(R.id.edit_office_drop);
        metdropOther = (TextView) view.findViewById(R.id.edit_other_drop);

        metpickupHome = (TextView) view.findViewById(R.id.edit_home_pickup);
        metpickupOffice = (TextView) view.findViewById(R.id.edit_office_pickup);
        metpickupOther = (TextView) view.findViewById(R.id.edit_other_pickup);

        imgebtnpickupHome = (ImageView) view.findViewById(R.id.pickup_addhome_address);
        imgebtnpickupOffice = (ImageView) view.findViewById(R.id.pickup_addoffice_address);
        imgebtnpickupOther = (ImageView) view.findViewById(R.id.pickup_addother_address);

        imgebtndropHome = (ImageView) view.findViewById(R.id.drop_addhome_address);
        imgebtndropOffice = (ImageView) view.findViewById(R.id.drop_addoffice_address);
        imgebtndropOther = (ImageView) view.findViewById(R.id.drop_addother_address);

        imgebtnpickupHome.setOnClickListener(this);
        imgebtnpickupOffice.setOnClickListener(this);
        imgebtnpickupOther.setOnClickListener(this);

        imgebtndropHome.setOnClickListener(this);
        imgebtndropOffice.setOnClickListener(this);
        imgebtndropOther.setOnClickListener(this);

        metdropHome.setOnClickListener(this);
        metdropOffice.setOnClickListener(this);
        metdropOther.setOnClickListener(this);

        metpickupHome.setOnClickListener(this);
        metpickupOffice.setOnClickListener(this);
        metpickupOther.setOnClickListener(this);


        ///////////////////////////////////////pickUp Location //////////////////////////


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });

       /* if (CommanMethod.isOnline(getActivity())) {
            RequestParams params = new RequestParams();
            params.add("userId", mUserId);
            presetLocations(params);

        } else {
            CommanMethod.showAlert(getString(R.string.networkError_Message), getActivity());
        }*/
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener


                    if (keyCode == KeyEvent.KEYCODE_BACK) {


                        Fragment homeFragment = new FragmentMainScreen();
                        FragmentManager frgManager;
                        frgManager = getFragmentManager();
                        frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                        return true;
                    }


                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {

        if (CommanMethod.isOnline(getActivity())) {
            RequestParams params = new RequestParams();
            System.out.println("mUserId==>>" + mUserId);
            params.add("userId", mUserId);
            presetLocations(params);

        } else {
            CommanMethod.showAlert(getString(R.string.networkError_Message), getActivity());
        }
        super.onResume();
    }

    public void presetLocations(RequestParams params) {

        String Url = WebserviceUrlClass.PresetLocations;
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        client.post(Url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        prgDialog.hide();
                        Log.d("NearByResponse=", response.toString());
                        try {
                            String responseCode = response.getString("responseCode");
                            String responseMessage = response.getString("responseMsg");
                            if (responseCode.toString().trim().equals("200")) {
                                if (responseCode.toString().trim().equals("200")) {
                                    JSONArray memberArray = response.getJSONArray("presetLocation");
                                    JSONObject pickupObject = null;
                                    JSONObject dropObject = null;


                                    mPresetLocationList.clear();
                                    mDropLocationList.clear();
                                    mPickUpLocationList.clear();
                                    PreSetLocationResource mPresetLocationDetails = null;

                                    for (int i = 0; i < memberArray.length(); i++) {
                                        mPresetLocationDetails = new PreSetLocationResource();
                                        JSONObject obj = (JSONObject) memberArray.get(i);

                                        if (obj.getString("id").toString().trim().equals("null") || obj.getString("id").equals("")) {
                                            mPresetLocationDetails.setId("");
                                        } else {
                                            mPresetLocationDetails.setId(obj.getString("id").toString().trim());
                                        }
                                        if (obj.getString("latitude").toString().trim().equals("null") || obj.getString("latitude").equals("")) {
                                            mPresetLocationDetails.setLatitude("");
                                        } else {
                                            mPresetLocationDetails.setLatitude(obj.getString("latitude").toString().trim());
                                        }
                                        if (obj.getString("longitude").toString().trim().equals("null") || obj.getString("longitude").equals("")) {
                                            mPresetLocationDetails.setLongitude("");
                                        } else {
                                            mPresetLocationDetails.setLongitude(obj.getString("longitude").toString().trim());
                                        }
                                        if (obj.getString("google_address").toString().trim().equals("null") || obj.getString("google_address").equals("")) {
                                            mPresetLocationDetails.setGoogle_address("");
                                        } else {
                                            mPresetLocationDetails.setGoogle_address(obj.getString("google_address").toString().trim());
                                        }
                                        if (obj.getString("short_address").toString().trim().equals("null") || obj.getString("short_address").equals("")) {
                                            mPresetLocationDetails.setShort_address("");
                                        } else {
                                            mPresetLocationDetails.setShort_address(obj.getString("short_address").toString().trim());
                                        }

                                        mPresetLocationList.add(mPresetLocationDetails);

                                    }


                                    if (response.get("userPickup") instanceof JSONObject) {
                                        pickupObject = response.getJSONObject("userPickup");
                                        Log.d("NearByResponse=pick", pickupObject.toString());

                                        if (pickupObject.has("Home")) {
                                            JSONObject homeOject = pickupObject.getJSONObject("Home");
                                            {

                                                String pickUpid = homeOject.getString("id");
                                                String pickUp_userid = homeOject.getString("user_id");
                                                String pickUpaddressType = homeOject.getString("address_type");
                                                home_pickup_lat = homeOject.getString("latitude");
                                                home_pickup_long = homeOject.getString("longitude");
                                                home_pickup_Address = homeOject.getString("google_address");
                                                home_pickup_shortAddress = homeOject.getString("short_address");
                                                MyApplication.home_pickup_shortAddress = homeOject.getString("short_address");
                                                String pickUplocation = homeOject.getString("location");

                                                if (home_pickup_shortAddress.equals(null) || home_pickup_shortAddress.equals("")) {
                                                    metpickupHome.setText("");
                                                } else {
                                                    metpickupHome.setText(home_pickup_shortAddress);
                                                }
                                            }
                                        }
                                        if (pickupObject.has("Office")) {
                                            JSONObject officeOject = pickupObject.getJSONObject("Office");
                                            {

                                                String pickUpid = officeOject.getString("id");
                                                String pickUp_userid = officeOject.getString("user_id");
                                                String pickUpaddressType = officeOject.getString("address_type");
                                                office_pickup_lat = officeOject.getString("latitude");
                                                office_pickup_long = officeOject.getString("longitude");
                                                office_pickup_Address = officeOject.getString("google_address");
                                                office_pickup_shortAddress = officeOject.getString("short_address");
                                                String pickUplocation = officeOject.getString("location");
                                                if (office_pickup_shortAddress.equals(null) || office_pickup_shortAddress.equals("")) {
                                                    metpickupOffice.setText("");
                                                } else {
                                                    metpickupOffice.setText(office_pickup_shortAddress);
                                                }
                                            }
                                        }
                                        if (pickupObject.has("Other")) {
                                            JSONObject otherOject = pickupObject.getJSONObject("Other");
                                            {

                                                String pickUpid = otherOject.getString("id");
                                                String pickUp_userid = otherOject.getString("user_id");
                                                String pickUpaddressType = otherOject.getString("address_type");
                                                other_pickup_lat = otherOject.getString("latitude");
                                                other_pickup_long = otherOject.getString("longitude");
                                                other_pickup_Address = otherOject.getString("google_address");
                                                other_pickup_shortAddress = otherOject.getString("short_address");
                                                String pickUplocation = otherOject.getString("location");

                                                if (other_pickup_shortAddress.equals(null) || other_pickup_shortAddress.equals("")) {
                                                    metpickupOther.setText("");
                                                } else {
                                                    metpickupOther.setText(other_pickup_shortAddress);
                                                }
                                            }
                                        }
                                    }
                                    if (response.get("userdestination") instanceof JSONObject) {
                                        dropObject = response.getJSONObject("userdestination");
                                        Log.d("NearByResponse=drop", dropObject.toString());


                                        if (dropObject.has("Home")) {
                                            JSONObject drophomeOject = dropObject.getJSONObject("Home");
                                            {


                                                String pickUpid = drophomeOject.getString("id");
                                                String pickUp_userid = drophomeOject.getString("user_id");
                                                String pickUpaddressType = drophomeOject.getString("address_type");
                                                home_drop_lat = drophomeOject.getString("latitude");
                                                home_drop_long = drophomeOject.getString("longitude");
                                                home_drop_Address = drophomeOject.getString("google_address");
                                                home_drop_shortAddress = drophomeOject.getString("short_address");
                                                String pickUplocation = drophomeOject.getString("location");

                                                if (home_drop_shortAddress.equals(null) || home_drop_shortAddress.equals("")) {
                                                    metdropHome.setText("");
                                                } else {
                                                    metdropHome.setText(home_drop_shortAddress);
                                                }
                                            }
                                        }
                                        if (dropObject.has("Office")) {
                                            JSONObject dropofficeOject = dropObject.getJSONObject("Office");
                                            {


                                                String pickUpid = dropofficeOject.getString("id");
                                                String pickUp_userid = dropofficeOject.getString("user_id");
                                                String pickUpaddressType = dropofficeOject.getString("address_type");
                                                office_drop_lat = dropofficeOject.getString("latitude");
                                                office_drop_long = dropofficeOject.getString("longitude");
                                                office_drop_Address = dropofficeOject.getString("google_address");
                                                office_drop_shortAddress = dropofficeOject.getString("short_address");
                                                String pickUplocation = dropofficeOject.getString("location");
                                                if (office_drop_shortAddress.equals(null) || office_drop_shortAddress.equals("")) {
                                                    metdropOffice.setText("");
                                                } else {
                                                    metdropOffice.setText(office_drop_shortAddress);
                                                }
                                            }
                                        }
                                        if (dropObject.has("Other")) {
                                            JSONObject dropotherOject = dropObject.getJSONObject("Other");
                                            {

                                                String pickUpid = dropotherOject.getString("id");
                                                String pickUp_userid = dropotherOject.getString("user_id");
                                                String pickUpaddressType = dropotherOject.getString("address_type");
                                                other_drop_lat = dropotherOject.getString("latitude");
                                                other_drop_long = dropotherOject.getString("longitude");
                                                other_drop_Address = dropotherOject.getString("google_address");
                                                other_drop_shortAddress = dropotherOject.getString("short_address");
                                                String pickUplocation = dropotherOject.getString("location");

                                                if (other_drop_shortAddress.equals(null) || other_drop_shortAddress.equals("")) {
                                                    metdropOther.setText("");
                                                } else {
                                                    metdropOther.setText(other_drop_shortAddress);
                                                }
                                            }
                                        }
                                    }

                                }

                                PresetLocationRecyclerViewAdapter rcAdapter = new PresetLocationRecyclerViewAdapter(getActivity(), mPresetLocationList);
                                recyclerView.setAdapter(rcAdapter);


                            }
                            if (responseCode.equals("0")) {
                                System.out.print("No record is found");
                                CommanMethod.showAlert("No Record is Found", getActivity());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        prgDialog.hide();
                        CommanMethod.showAlert(getResources().getString(R.string.connection_error), getActivity());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.dismiss();
                    }
                }
        );

    }


    @Override
    public void onClick(View v) {

        /////////////pick up buttons //////////
        if (v == imgebtnpickupHome) {
            placepicker(1);
        }
        if (v == imgebtnpickupOffice) {
            placepicker(2);
        }
        if (v == imgebtnpickupOther) {
            placepicker(3);
        }
        /////////////drop buttons//////////
        if (v == imgebtndropHome) {
            placepicker(4);
        }
        if (v == imgebtndropOffice) {
            placepicker(5);
        }
        if (v == imgebtndropOther) {
            placepicker(6);
        }
        ///////////// drop address text view ////////////////
        if (v == metdropHome) {
            if (home_drop_Address != null && home_drop_lat != null && home_drop_long != null) {
                MyApplication.pickuplocationslat = home_drop_lat;
                MyApplication.pickuplocationslong = home_drop_long;
                MyApplication.google_address = home_drop_Address;

              /*  Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);*/
                Fragment homeFragment = new FragmentMainScreen();
                FragmentManager frgManager;
                frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                MyApplication.frequentlocationset=true;
                MyApplication.pickuploctiononetime=true;
                //MyApplication.pickuploction=true;

            }
        }

        if (v == metdropOffice) {
            if (office_drop_Address != null && office_drop_lat != null && office_drop_long != null) {
                MyApplication.pickuplocationslat = office_drop_lat;
                MyApplication.pickuplocationslong = office_drop_long;
                MyApplication.google_address = office_drop_Address;

                /*Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);*/
                Fragment homeFragment = new FragmentMainScreen();
                FragmentManager frgManager;
                frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                MyApplication.frequentlocationset=true;
                MyApplication.pickuploctiononetime=true;
                //MyApplication.pickuploction=true;
            }

        }

        if (v == metdropOther) {
            if (other_drop_Address != null && other_drop_lat != null && other_drop_long != null) {
                MyApplication.pickuplocationslat = other_drop_lat;
                MyApplication.pickuplocationslong = other_drop_long;
                MyApplication.google_address = other_drop_Address;

               /* Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);*/
                Fragment homeFragment = new FragmentMainScreen();
                FragmentManager frgManager;
                frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                MyApplication.frequentlocationset=true;
                MyApplication.pickuploctiononetime=true;
                //MyApplication.pickuploction=true;
            }

        }
        ///////////// pick Up address text view /////////////////

        if (v == metpickupHome) {
            if (home_pickup_Address != null && home_pickup_lat != null && home_pickup_long != null) {
                MyApplication.droplocationslat = home_pickup_lat;
                MyApplication.droplocationslong = home_pickup_long;
                MyApplication.pickup_google_address = home_pickup_Address;

              /*  Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);*/
                Fragment homeFragment = new FragmentMainScreen();
                FragmentManager frgManager;
                frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                MyApplication.frequentlocationset=true;
                MyApplication.pickuploction=true;
            }

        }
        if (v == metpickupOffice) {

            if (office_pickup_Address != null && office_drop_lat != null && office_drop_long != null) {
                MyApplication.droplocationslat = office_pickup_lat;
                MyApplication.droplocationslong = office_pickup_long;
                MyApplication.pickup_google_address = office_pickup_Address;

               /* Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);*/
                Fragment homeFragment = new FragmentMainScreen();
                FragmentManager frgManager;
                frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                MyApplication.frequentlocationset=true;
                MyApplication.pickuploction=true;
            }
        }

        if (v == metpickupOther) {

            if (other_pickup_Address != null && other_pickup_lat != null && other_pickup_long != null) {
                MyApplication.droplocationslat = other_pickup_lat;
                MyApplication.droplocationslong = other_pickup_long;
                MyApplication.pickup_google_address = other_pickup_Address;

              /*  Intent in = new Intent(getActivity(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);*/
                Fragment homeFragment = new FragmentMainScreen();
                FragmentManager frgManager;
                frgManager = getFragmentManager();
                frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                MyApplication.frequentlocationset=true;
                MyApplication.pickuploction=true;
            }

        }


    }


    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(getActivity(), data);
            final CharSequence short_address = place.getName();
            final CharSequence address = place.getAddress();

            address1 = address.toString();
            LatLng latlong = place.getLatLng();
            lat = latlong.latitude;
            lng = latlong.longitude;
            if (PLACE_PICKER_REQUEST == 1) {

                Log.e("ditails", String.valueOf(lat) + "," + String.valueOf(lng) + "," + short_address + address);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getActivity(), short_address, Toast.LENGTH_LONG).show();
                Log.e("ditails", String.valueOf(lat) + "," + String.valueOf(lng) + "," + short_address + address);
                Toast.makeText(getActivity(), "PickUP", Toast.LENGTH_LONG).show();
                Log.e("pick", "pick UP address");

                RequestParams params = new RequestParams();
                params.put("userId", mUserId);
                params.put("lat", lat);
                params.put("long", lng);
                params.put("googleAddress", address1);
                params.put("ShortAddress", short_address);
                params.put("location", "pickup");
                params.put("address_type", "Home");
                addAddressWS(params, PLACE_PICKER_REQUEST);
                //   metpickupHome.setText("" + short_address);
            }
            if (PLACE_PICKER_REQUEST == 2) {
                RequestParams params = new RequestParams();
                params.put("userId", mUserId);
                params.put("lat", lat);
                params.put("long", lng);
                params.put("googleAddress", address1);
                params.put("ShortAddress", short_address);
                params.put("location", "pickup");
                params.put("address_type", "Office");
                addAddressWS(params, PLACE_PICKER_REQUEST);
                // metpickupOffice.setText("" + short_address);
            }
            if (PLACE_PICKER_REQUEST == 3) {
                RequestParams params = new RequestParams();
                params.put("userId", mUserId);
                params.put("lat", lat);
                params.put("long", lng);
                params.put("googleAddress", address1);
                params.put("ShortAddress", short_address);
                params.put("location", "pickup");
                params.put("address_type", "Other");
                addAddressWS(params, PLACE_PICKER_REQUEST);
                //  metpickupOther.setText("" + short_address);
            }
            if (PLACE_PICKER_REQUEST == 4) {
                RequestParams params = new RequestParams();
                params.put("userId", mUserId);
                params.put("lat", lat);
                params.put("long", lng);
                params.put("googleAddress", address1);
                params.put("ShortAddress", short_address);
                params.put("location", "destination");
                params.put("address_type", "Home");
                addAddressWS(params, PLACE_PICKER_REQUEST);
                //  metdropHome.setText("" + short_address);
            }
            if (PLACE_PICKER_REQUEST == 5) {
                RequestParams params = new RequestParams();
                params.put("userId", mUserId);
                params.put("lat", lat);
                params.put("long", lng);
                params.put("googleAddress", address1);
                params.put("ShortAddress", short_address);
                params.put("location", "destination");
                params.put("address_type", "Office");
                addAddressWS(params, PLACE_PICKER_REQUEST);
                // metdropOffice.setText("" + short_address);
            }
            if (PLACE_PICKER_REQUEST == 6) {
                RequestParams params = new RequestParams();
                params.put("userId", mUserId);
                params.put("lat", lat);
                params.put("long", lng);
                params.put("googleAddress", address1);
                params.put("ShortAddress", short_address);
                params.put("location", "destination");
                params.put("address_type", "Other");
                addAddressWS(params, PLACE_PICKER_REQUEST);
                // metdropOther.setText("" + short_address);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }


    public void placepicker(int RequestCode) {
        PLACE_PICKER_REQUEST = RequestCode;
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            if (BOUNDS_MOUNTAIN_VIEW != null) {
                intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            }

            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }


    public void addAddressWS(RequestParams params, final int RequestCode) {

        String url = WebserviceUrlClass.AddLocations;
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {
                        RequestParams params = new RequestParams();
                        if (RequestCode == 1) {
                            metpickupHome.setText("" + address1);
                            params.add("userId", mUserId);
                            presetLocations(params);
                        } else if (RequestCode == 2) {
                            metpickupOffice.setText("" + address1);
                            presetLocations(params);
                        } else if (RequestCode == 3) {
                            metpickupOther.setText("" + address1);
                            params.add("userId", mUserId);
                            presetLocations(params);
                        } else if (RequestCode == 4) {
                            metdropHome.setText("" + address1);
                            params.add("userId", mUserId);
                            presetLocations(params);
                        } else if (RequestCode == 5) {
                            metdropOffice.setText("" + address1);
                            params.add("userId", mUserId);
                            presetLocations(params);
                        } else if (RequestCode == 6) {
                            metdropOther.setText("" + address1);
                            params.add("userId", mUserId);
                            presetLocations(params);
                        }

                        //CommanMethod.showAlert(responseMessage, getActivity());

                    } else {
                        CommanMethod.showAlert(responseMessage, getActivity());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.hide();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error), getActivity());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

}


