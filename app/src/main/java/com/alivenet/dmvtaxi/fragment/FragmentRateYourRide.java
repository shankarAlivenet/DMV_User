package com.alivenet.dmvtaxi.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.SharedPreference;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.pojo.RideDriverComplete;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import constant.MyApplication;
import constant.MyPreferences;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/17/2016.
 */
public class FragmentRateYourRide extends Fragment {
    RelativeLayout pickup_location, destination_lovation;
    Button btntip, btnsubmit;
    TextView Text_tripcomplete_dropoff_location,Text_tripcomplete_pickuplocation, mlongtitude, mname, mlicenceplate, mtaname,Text_tripcharged;
    RatingBar ratingBar;
    EditText tipValue, mcommentbox;
    RideDriverComplete rideDriverComplete;
    ImageView mdriverImage;
    ImageView icon;
    ProgressDialog prgDialog;
    public String mUserId;
    SharedPreferences mPref;
    SharedPreference sharedPreference = new SharedPreference();
    ;
    public static final String MYPREF = "user_info";
    private String clientToken;

    public static FragmentRateYourRide newInstance(String sectionTitle) {
        FragmentRateYourRide fragment = new FragmentRateYourRide();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.trip_completed, container, false);


        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
        Text_tripcomplete_pickuplocation = (TextView) view.findViewById(R.id.tripcomplete_pickuplocation);
        Text_tripcomplete_dropoff_location = (TextView) view.findViewById(R.id.tripcomplete_dropoff_location);
        mname = (TextView) view.findViewById(R.id.tv_name);
        Text_tripcharged = (TextView) view.findViewById(R.id.tripcharged);
        mlicenceplate = (TextView) view.findViewById(R.id.tv_licenceplate);
        mtaname = (TextView) view.findViewById(R.id.tv_taname);
        mcommentbox = (EditText) view.findViewById(R.id.et_commentbox);
        btntip = (Button) view.findViewById(R.id.btntip);
        btnsubmit = (Button) view.findViewById(R.id.btnsubmit);
        icon = (ImageView) view.findViewById(R.id.tripcompltd_icons);

        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        btntip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplication.RateyourRide==false) {

                    LayoutInflater li = LayoutInflater.from(getContext());
                    View promptsView = li.inflate(R.layout.prompts, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());
                    alertDialogBuilder.setView(promptsView);
                    tipValue = (EditText) promptsView.findViewById(R.id.edittip_value);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            if (tipValue.getText().toString().trim().length() == 0) {

                                            } else {
                                                btntip.setText(String.format("Tip: $ %.2f", Double.valueOf(tipValue.getText().toString())));

                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    getToken(mUserId);

                }

            }
        });
        rideDriverComplete = sharedPreference.getDriverRidercompleate(getActivity());
        String rideId= MyApplication.RideId;
        if(rideDriverComplete==null&&rideId!=null)
        {

            ValidateRidecomplet(mUserId,rideId);
        }

        if(MyApplication.RateyourRide==true)
        {
            btnsubmit.setVisibility(View.GONE);
            mcommentbox.setVisibility(View.GONE);
        }



        if (rideDriverComplete != null) {



            if(rideDriverComplete.getPickupaddress()!=null&&rideDriverComplete.getPickupaddress()!=" "&&rideDriverComplete.getDestinationaddress()!=null&&rideDriverComplete.getDestinationaddress()!=" ") {
                Text_tripcomplete_pickuplocation.setText(rideDriverComplete.getPickupaddress());
                Text_tripcomplete_dropoff_location.setText(rideDriverComplete.getDestinationaddress());
            }
            try {

                Picasso.with(getActivity()).load(MyPreferences.getActiveInstance(getActivity()).getImageUrl())
                        .error(R.mipmap.avtar)
                        .placeholder(R.mipmap.avtar)
                        .into(icon);
            }catch (Exception e)
            {
                e.printStackTrace();
            }


            mname.setText(rideDriverComplete.driverNameride);
            mlicenceplate.setText(rideDriverComplete.licenseId);
            mtaname.setText(rideDriverComplete.vehicle);
            Text_tripcharged.setText("$"+rideDriverComplete.getTotalfare()+"  HAS BEEN CHARGED TO YOUR CREDIT CARD");

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String totalStars = "Total Stars:: " + ratingBar.getNumStars();
                    String rating = "Rating :: " + ratingBar.getRating();
                    String tip="";
           if(rideDriverComplete!=null) {

                   String driverid = rideDriverComplete.getDriverIdride();
                   String rideId = rideDriverComplete.getRideId();
                   if(tipValue!=null)
                   tip = tipValue.getText().toString();

                  String comment = mcommentbox.getText().toString();
                    if (driverid != null && rideId != null && totalStars != null && tip != null) {

                     validateRideRating(mUserId, driverid, rideId, totalStars, tip, comment);
                }


           }
                }
            });
        }

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
    protected void validateRideRating(String userId, String driver_id, String ride_id, String star, String tip, String comment) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.add("user_id", userId);
        params.add("driver_id", driver_id);
        params.add("ride_id", ride_id);
        params.add("star", star);
        params.add("tip", tip);
        params.add("comment", comment);
        client.post(WebserviceUrlClass.RIDE_RATING, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        prgDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject responseCode) {
                        super.onSuccess(statusCode, headers, responseCode);
                        Log.e("", "On Success of NoTs");
                        prgDialog.hide();
                        parseResult(responseCode.toString());
                        try {

                            JSONObject object = new JSONObject(responseCode.toString());
                            Log.e("responseRateRide", ":-" + object.toString(2));

                            Fragment homeFragment = new FragmentMainScreen();
                            FragmentManager frgManager;
                            frgManager = getFragmentManager();
                            frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();
                            MyApplication.RateyourRide=true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        prgDialog.dismiss();
                        CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.hide();
                    }

                });

    }


    private void parseResult(String result) {

        try {
            JSONObject response = new JSONObject(result);
            if (response.getString("responseCode").equals("200")) {

                String respmsg=response.getString("responseMsg");

                    mcommentbox.clearFocus();
                    CommanMethod.showAlert(respmsg ,getActivity());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    ////////////////////BrainTree payment GetWey Work narendr ///////////////////


    private void getToken(String userIdd) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.add("userId", userIdd);
        client.post(WebserviceUrlClass.GetToken, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.e("", "On Success of NoTs");
                        try {
                            Log.d("Json_con", response.toString());
                            String responseCode = response.getString("responseCode");
                            String responseMessage = response.getString("responseMsg");

                            if (responseCode.equals("200")) {
                                if (responseMessage.equals("success")) {
                                    clientToken = response.getString("token");
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // prgDialog.hide();
                        CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
                    }


                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.hide();
                    }


                });
    }
    private void ValidateRidecomplet(String userIdd, String rideId) {

        Log.e("rideId>>>>>>>>>>>",""+rideId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.add("userId", userIdd);
        params.add("rideId", rideId);
        client.post(WebserviceUrlClass.GetToken, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        prgDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        prgDialog.hide();
                        Log.e("", "On Success of NoTs");
                        try {
                            Log.d("responseGetRide", response.toString());
                            String responseCode = response.getString("responseCode");
                            String responseMessage = response.getString("responseMsg");

                            if (responseCode.equals("200")) {

                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        prgDialog.hide();
                        CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
                    }


                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.hide();
                    }


                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        rideDriverComplete = (RideDriverComplete) bundle.getSerializable("ridedriverInfo");
        if (rideDriverComplete != null) {

            Text_tripcomplete_pickuplocation.setText(rideDriverComplete.getPickupaddress());
            Text_tripcomplete_dropoff_location.setText(rideDriverComplete.getDestinationaddress());


            mname.setText(rideDriverComplete.driverNameride);
            mlicenceplate.setText(rideDriverComplete.licenseId);
            Text_tripcharged.setText("$"+rideDriverComplete.getTotalfare()+"  HAS BEEN CHARGED TO YOUR CREDIT CARD");
            mtaname.setText(rideDriverComplete.vehicle);
            sharedPreference.SaveRideComplete(getActivity(), rideDriverComplete);
            System.out.println("drivernameridecomplete" + rideDriverComplete.getDriverNameride());
            System.out.println("tripcomplete" + rideDriverComplete.getTotalfare());
        }
    }




}
