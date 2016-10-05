package com.alivenet.dmvtaxi.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.alivenet.dmvtaxi.adapter.ReservationlistRecylerViewAdapter;
import com.alivenet.dmvtaxi.adapter.RideListRecyclerViewAdapter;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.pojo.ReservationListModel;
import com.alivenet.dmvtaxi.pojo.RideListResource;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/17/2016.
 */
public class FragmentReservation extends Fragment implements ReservationlistRecylerViewAdapter.BookingId{

    RecyclerView recyclerView;
    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    public String mUserId;
    private ArrayList<ReservationListModel> mReslist = new ArrayList<ReservationListModel>();
    ProgressDialog progressDialog;
    TextView mreservation;
    Activity activity;
    ReservationlistRecylerViewAdapter rcAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public static FragmentReservation newInstance(String sectionTitle) {
        FragmentReservation fragment = new FragmentReservation();
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
        View view = inflater.inflate(R.layout.fragment_reservation_list, container, false);
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);

        System.out.println("userId >>>>>>>>>>>>>"+mUserId);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mreservation=(TextView) view.findViewById(R.id.tv_reservation);
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
        String url = WebserviceUrlClass.Reservation_list;
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
                try {
                    JSONObject object=new JSONObject(response.toString());
                    Log.d("Json_conFragmeRese", ""+object.toString(2));
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        if (responseMessage.equals("success")) {
                            if (response.get("reservationList") instanceof JSONArray) {

                                mReslist.clear();
                                ReservationListModel mResv = null;
                                JSONArray jsonArray = null;
                                jsonArray = response.getJSONArray("reservationList");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    mResv = new ReservationListModel();
                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    obj.remove(i+":");
                                    if (obj.getString("booking_id").toString().trim().equals("null") || obj.getString("booking_id").equals("")) {
                                        mResv.setBookingId("");
                                    } else {
                                        mResv.setBookingId(obj.getString("booking_id").toString().trim());
                                    }

                                    if (obj.getString("user_id").toString().trim().equals("null") || obj.getString("user_id").equals("")) {
                                        mResv.setUserid("");
                                    } else {
                                        mResv.setUserid(obj.getString("user_id").toString().trim());
                                    }

                                    if (obj.getString("pick_lat").toString().trim().equals("null") || obj.getString("pick_lat").equals("")) {
                                        mResv.setPiclat("");
                                    } else {
                                        mResv.setPiclat(obj.getString("pick_lat").toString().trim());

                                    }

                                    if (obj.getString("pick_long").toString().trim().equals("null") || obj.getString("pick_long").equals("")) {
                                        mResv.setPiclong("");

                                    } else {
                                        mResv.setPiclong(obj.getString("pick_long").toString().trim());
                                    }

                                    if (obj.getString("dest_lat").toString().trim().equals("null") || obj.getString("dest_lat").equals("")) {
                                        mResv.setDestlat("");
                                    } else {
                                        mResv.setDestlat(obj.getString("dest_lat").toString().trim());
                                    }

                                    if (obj.getString("dest_long").toString().trim().equals("null") || obj.getString("dest_long").equals("")) {
                                        mResv.setDestlong("");
                                    } else {
                                        mResv.setDestlong(obj.getString("dest_long").toString().trim());
                                    }

                                    if (obj.getString("cab_type").toString().trim().equals("null") || obj.getString("cab_type").equals("")) {
                                        mResv.setCabtype("");
                                    } else {
                                        mResv.setCabtype(obj.getString("cab_type").toString().trim());
                                    }

                                    if (obj.getString("rev_date").toString().trim().equals("null") || obj.getString("rev_date").equals("")) {
                                        mResv.setRevdate("");
                                    } else {
                                        mResv.setRevdate(obj.getString("rev_date").toString().trim());
                                    }

                                    if (obj.getString("rev_time").toString().trim().equals("null") || obj.getString("rev_time").equals("")) {
                                        mResv.setRevtime("");
                                    } else {
                                        mResv.setRevtime(obj.getString("rev_time").toString().trim());
                                    }
                                    if (obj.getString("status").toString().trim().equals("null") || obj.getString("status").equals("")) {
                                        mResv.setStatus("");
                                    } else {
                                        mResv.setStatus(obj.getString("status").toString().trim());
                                    }

                                    if (obj.getString("created").toString().trim().equals("null") || obj.getString("created").equals("")) {
                                        mResv.setCreated("");
                                    } else {
                                        mResv.setCreated(obj.getString("created").toString().trim());
                                    }
                                    if (obj.getString("updated").toString().trim().equals("null") || obj.getString("updated").equals("")) {
                                        mResv.setUpdate("");
                                    } else {
                                        mResv.setUpdate(obj.getString("updated").toString().trim());
                                    }
                                    if (obj.getString("pickup_address").toString().trim().equals("null") || obj.getString("pickup_address").equals("")) {

                                        mResv.setPicAddress("");

                                    }else {
                                        mResv.setPicAddress(obj.getString("pickup_address").toString());
                                    }
                                    if(obj.getString("destination_address").toString().trim().equals("null") || obj.getString("destination_address").equals(""))
                                    {
                                        mResv.setDestAddress("");

                                    }else {

                                        mResv.setDestAddress(obj.getString("destination_address").toString().trim());

                                    }
                                    mReslist.add(mResv);
                                }
                                rcAdapter = new ReservationlistRecylerViewAdapter(getActivity(), mReslist,FragmentReservation.this);
                                recyclerView.setAdapter(rcAdapter);
                                mreservation.setVisibility(View.GONE);
                            }else{
                                CommanMethod.showAlert("No Reservation List Found.",getActivity());
                                mreservation.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        Toast.makeText(getActivity(), "" + responseMessage, Toast.LENGTH_LONG).show();
                        mreservation.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressDialog.dismiss();
                mreservation.setVisibility(View.GONE);
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
            }
            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }
        });

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }

    @Override
    public void bookId(String bookId,int position) {
        ValidatecancelledRide(bookId, position);
    }



    public void ValidatecancelledRide(String bookingID, final int position) {

        String url = WebserviceUrlClass.cancelledRide;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("bookingId", bookingID);
        System.out.println("bookingId==>>"+bookingID);
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
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        mReslist.remove(position);
                        rcAdapter.notifyDataSetChanged();
                        CommanMethod.showAlert(responseMessage,getActivity());


                    } else {
                        //  new IncorrectPasswordDialog(LoginActivity.this, "hello").show();
                        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressDialog.hide();
                Toast toast = Toast.makeText(getActivity(), "Unable to connect to the server, please try again later.", Toast.LENGTH_SHORT);toast.show();
            }


            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.hide();
            }
        });
    }


}



