package com.alivenet.dmvtaxi.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.adapter.RideListRecyclerViewAdapter;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.SpacesItemDecoration;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.pojo.RideListResource;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;


public class Fragment_ride_list extends Fragment {

    RecyclerView recyclerView;
    RideListRecyclerViewAdapter rcAdapter;
    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    public String mUserId;
    TextView mnorides;
    private ArrayList<RideListResource> mRideList = new ArrayList<RideListResource>();
    ProgressDialog progressDialog;
    private static final int VERTICAL_ITEM_SPACE = 5;

    public static Fragment_ride_list newInstance(String sectionTitle) {
        Fragment_ride_list fragment = new Fragment_ride_list();
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
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);



        recyclerView.addItemDecoration(new SpacesItemDecoration(VERTICAL_ITEM_SPACE));


        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mnorides=(TextView)view.findViewById(R.id.tv_norides);
        RequestParams params = new RequestParams();
        params.put("userId", mUserId);
        rideListWs(params);


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



    public void rideListWs(RequestParams params) {

        String url = WebserviceUrlClass.RideList;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.hide();
                try {
                    JSONObject object=new JSONObject(response.toString());
                    Log.e("Json_conRidelist",""+object.toString(2));
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        if (responseMessage.equals("success")) {

                            if (response.get("rideList") instanceof JSONArray) {

                                mRideList.clear();
                                RideListResource mRide = null;
                                JSONArray jsonArray = null;
                                jsonArray = response.getJSONArray("rideList");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mRide = new RideListResource();
                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    if (obj.getString("bookingId").toString().trim().equals("null") || obj.getString("bookingId").equals("")) {
                                        mRide.setBookingId("");
                                    } else {
                                        mRide.setBookingId(obj.getString("bookingId").toString().trim());
                                    }

                                    if (obj.getString("bookingTime").toString().trim().equals("null") || obj.getString("bookingTime").equals("")) {
                                        mRide.setBookingTime("");
                                    } else {
                                        mRide.setBookingTime(obj.getString("bookingTime").toString().trim());
                                    }

                                    if (obj.getString("cabName").toString().trim().equals("null") || obj.getString("cabName").equals("")) {
                                        mRide.setCabName("");
                                    } else {
                                        mRide.setCabName(obj.getString("cabName").toString().trim());
                                    }

                                    if (obj.getString("vehicle").toString().trim().equals("null") || obj.getString("vehicle").equals("")) {
                                        mRide.setVehicle("");
                                    } else {
                                        mRide.setVehicle(obj.getString("vehicle").toString().trim());
                                    }

                                    if (obj.getString("licenseId").toString().trim().equals("null") || obj.getString("licenseId").equals("")) {
                                        mRide.setLicenseId("");
                                    } else {
                                        mRide.setLicenseId(obj.getString("licenseId").toString().trim());
                                    }

                                    if (obj.getString("cabIcon").toString().trim().equals("null") || obj.getString("cabIcon").equals("")) {
                                        mRide.setCabIcon("");
                                    } else {
                                        mRide.setCabIcon(obj.getString("cabIcon").toString().trim());
                                    }

                                    if (obj.getString("driverImage").toString().trim().equals("null") || obj.getString("driverImage").equals("")) {
                                        mRide.setDriverImage("");
                                    } else {
                                        mRide.setDriverImage(obj.getString("driverImage").toString().trim());
                                    }

                                    if (obj.getString("pickupLat").toString().trim().equals("null") || obj.getString("pickupLat").equals("")) {
                                        mRide.setPickupLat("");
                                    } else {
                                        mRide.setPickupLat(obj.getString("pickupLat").toString().trim());
                                    }

                                    if (obj.getString("pickupLong").toString().trim().equals("null") || obj.getString("pickupLong").equals("")) {
                                        mRide.setPickupLong("");
                                    } else {
                                        mRide.setPickupLong(obj.getString("pickupLong").toString().trim());
                                    }
                                    if (obj.getString("destinationLat").toString().trim().equals("null") || obj.getString("destinationLat").equals("")) {
                                        mRide.setDestinationLat("");
                                    } else {
                                        mRide.setDestinationLat(obj.getString("destinationLat").toString().trim());
                                    }

                                    if (obj.getString("destinationLong").toString().trim().equals("null") || obj.getString("destinationLong").equals("")) {
                                        mRide.setDestinationLong("");
                                    } else {
                                        mRide.setDestinationLong(obj.getString("destinationLong").toString().trim());
                                    }
                                    if (obj.getString("status").toString().trim().equals("null") || obj.getString("status").equals("")) {
                                        mRide.setUstatus("");
                                    } else {
                                        mRide.setUstatus(obj.getString("status").toString().trim());
                                    }
                                    if (obj.getString("estDistance").toString().trim().equals("null") || obj.getString("estDistance").equals("")) {
                                        mRide.setEstDistance("");
                                    } else {
                                        mRide.setEstDistance(obj.getString("estDistance").toString().trim());
                                    }

                                    if (obj.getString("estTime").toString().trim().equals("null") || obj.getString("estTime").equals("")) {
                                        mRide.setEstTime("");
                                    } else {
                                        mRide.setEstTime(obj.getString("estTime").toString().trim());
                                    }
                                    if (obj.getString("estFare").toString().trim().equals("null") || obj.getString("estFare").equals("")) {
                                        mRide.setEstFare("");
                                    } else {
                                        mRide.setEstFare(obj.getString("estFare").toString().trim());
                                    }  if (obj.getString("pickupAddress").toString().trim().equals("null") || obj.getString("pickupAddress").equals("")) {
                                        mRide.setPickaddress("");
                                    } else {
                                        mRide.setPickaddress(obj.getString("pickupAddress").toString().trim());
                                    } if (obj.getString("destinationAddress").toString().trim().equals("null") || obj.getString("destinationAddress").equals("")) {
                                        mRide.setDestaddress("");
                                    } else {
                                        mRide.setDestaddress(obj.getString("destinationAddress").toString().trim());
                                    }
                                    mRideList.add(mRide);
                                }
                                if(mRideList!=null)
                                {
                                    rcAdapter = new RideListRecyclerViewAdapter(getActivity(), mRideList);
                                    recyclerView.setAdapter(rcAdapter);
                                }else {
                                    mnorides.setVisibility(View.VISIBLE);
                                }


                            }else{
                                CommanMethod.showAlert("No Ride List Found.",getActivity());
                            }



                        }
                    } else {
                        Toast.makeText(getActivity(), "" + responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }
        });

    }
}

