package com.alivenet.dmvtaxi;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.HttpClient;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.pojo.DriverTime;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;



/**
 * Created by Alivenet 05 on 7/14/2016.
 */
public class Activity_ConfirmMybooking extends AppCompatActivity {

    GoogleMap googleMap;
    double latitude;
    double longitude;
    double lat;
    double lng;
    Ride_trip_validate ride_trip_validate;
    ImageView mivimagecar;
    String pickup_address, drop_address, cabId, userId;
    double baseFare, baseFareDistance, perKm, waitingCharge;
    private Dialog mDialog;
    public double am_additioncharge;
    public double am_telephone_dispatch;
    public double am_passengerSurgcharge;
    public double am_luggagecharge;
    public double am_airportSurcharge;
    public double am_additionHourcharge;
    public double am_perpassengercharge;
    public double am_snowcharge;



    ProgressDialog prgDialog;
    TextView mtvpickup_address,cinfirmbooking, mtvdrop_address, mtvestimateprice;
    String zoom = "10";
    String paymentoption, passenger;
    LinearLayout mconfirmbooking;
    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    private static DecimalFormat df2 = new DecimalFormat(".##");
    Driverfulldetails driverfulldetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custome_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPref =Activity_ConfirmMybooking.this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mtvpickup_address = (TextView) findViewById(R.id.tv_pickup_address);
        mtvdrop_address = (TextView) findViewById(R.id.tv_drop_address);
        mtvestimateprice = (TextView) findViewById(R.id.tv_primerate);
        mconfirmbooking = (LinearLayout) findViewById(R.id.ll_confirmbooking);
        cinfirmbooking = (TextView) findViewById(R.id.cinfirmbooking);
        mivimagecar=(ImageView)findViewById(R.id.iv_carimage);
        Bundle b = getIntent().getExtras();
        Bundle b1 = getIntent().getExtras();
        latitude = b1.getDouble("currentlatitude");
        longitude = b1.getDouble("currentlongitude");
        lat = b.getDouble("distantionLat");
        lng = b.getDouble("distantionLng");

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        pickup_address = getIntent().getStringExtra("pickup_address");
        drop_address = getIntent().getStringExtra("drop_address");
        cabId = getIntent().getStringExtra("cabId");
        paymentoption = getIntent().getStringExtra("paymentoption");
        passenger = getIntent().getStringExtra("passenger");
        userId = getIntent().getStringExtra("userId");

        cinfirmbooking.setText("CONFIRM BOOKING");





        mivimagecar.startAnimation(inFromRightAnimation());



        if (pickup_address != null) {
            mtvpickup_address.setText(pickup_address);
            mtvdrop_address.setText(drop_address);
        }


        if(getIntent()!=null) {
            baseFare = Double.parseDouble(getIntent().getStringExtra("baseFare"));
            baseFareDistance = Double.parseDouble(getIntent().getStringExtra("baseFareDistance"));
            perKm = Double.parseDouble(getIntent().getStringExtra("perKm"));
            waitingCharge = Double.parseDouble(getIntent().getStringExtra("waitingCharge"));

            am_additioncharge = Double.parseDouble(getIntent().getStringExtra("madditioncharge"));
            am_telephone_dispatch = Double.parseDouble(getIntent().getStringExtra("mtelephone_dispatch"));
            am_passengerSurgcharge = Double.parseDouble(getIntent().getStringExtra("mpassengerSurgcharge"));
            am_luggagecharge = Double.parseDouble(getIntent().getStringExtra("mluggagecharge"));
            am_airportSurcharge = Double.parseDouble(getIntent().getStringExtra("mairportSurcharge"));
            am_additionHourcharge = Double.parseDouble(getIntent().getStringExtra("madditionHourcharge"));
            am_perpassengercharge = Double.parseDouble(getIntent().getStringExtra("mperpassengercharge"));
            am_snowcharge = Double.parseDouble(getIntent().getStringExtra("msnowcharge"));

            System.out.println("am_additioncharge==>>"+am_additioncharge);
            System.out.println("am_telephone_dispatch==>>"+am_telephone_dispatch);
            System.out.println("am_passengerSurgcharge==>>"+am_passengerSurgcharge);
            System.out.println("am_luggagecharge==>>"+am_luggagecharge);
            System.out.println("am_airportSurcharge==>>"+am_airportSurcharge);
            System.out.println("am_additionHourcharge==>>"+am_additionHourcharge);
            System.out.println("am_perpassengercharge==>>"+am_perpassengercharge);
            System.out.println("am_snowcharge==>>"+am_snowcharge);

        }
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapdirection)).getMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);

        if (latitude != 0.0d && longitude != 0.0d) {
            Log.e("longitudeconfirm", "" + longitude);
            Log.e("latitudeconfirm", "" + latitude);

            drawMarker(new LatLng(longitude, longitude));
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title("");
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(10).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));


            googleMap.addMarker(marker);
            if (ActivityCompat.checkSelfPermission(Activity_ConfirmMybooking.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Activity_ConfirmMybooking.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            googleMap.setMyLocationEnabled(true);
        }

        if (lat != 0.0d && lng != 0.0d) {
            drawMarkerOnActivity(new LatLng(lat, lng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            // Setting the zoom level in the map on last position  is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
        }

        mconfirmbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId != null && latitude != 0.0d && longitude != 0.0d && lat != 0.0d && lng != 0.0d && cabId != null&&paymentoption!=null){
                    ride_trip_validate =new Ride_trip_validate();
                   // ride_trip_validate.Ride_trip_validategetvalue(userId, String.valueOf(latitude), String.valueOf(longitude), String.valueOf(lat), String.valueOf(lng), cabId,paymentoption);
                    ride_trip_validate.execute();
                    //validateRide(userId, String.valueOf(latitude), String.valueOf(longitude), String.valueOf(lat), String.valueOf(lng), cabId,paymentoption);

                }
            }
        });


        cinfirmbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cinfirmbooking.getText().equals("Share Of The Fare"))
                {

                    Intent in = new Intent(Activity_ConfirmMybooking.this, SplitAddFrnd.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }else{
                    if (userId != null && latitude != 0.0d && longitude != 0.0d && lat != 0.0d && lng != 0.0d && cabId != null&&paymentoption!=null)
                        ride_trip_validate =new Ride_trip_validate();
                        ride_trip_validate.execute();

                }


            }
        });








        validateGET_TIME(latitude, longitude, lat, lng);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Activity_ConfirmMybooking.this.finish();

                return false;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void drawMarker(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.car));

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);

    }

    private void drawMarkerOnActivity(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);

    }


    // Asyntask for user Ride confirm
    public class Ride_trip_validate extends AsyncTask<String, Void, String> {

        private String response;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mDialog = ProgressDialog.show(Activity_ConfirmMybooking.this, "", "Loading...", true);
            mDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            response = ridebook_trip();
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            // TODO Auto-generated method stub
            super.onPostExecute(response);
            if (mDialog != null) {
                mDialog.dismiss();
            }
            Log.d("Confirm_trip_Response", "" + response);

            if (response != null) {

                try {
                    JSONObject responseobject = new JSONObject(response.toString());


                    if(responseobject.optString("responseMsg").toString().equals("error: no driver found"))
                    {
                        String messaage=responseobject.getString("responseMsg");
                        CommanMethod.showAlert(messaage,Activity_ConfirmMybooking.this);
                    }
                    else if(responseobject.optString("responseMsg").toString().equals("error: driver not responding"))
                    {

                        Activity_ConfirmMybooking.this.finish();
                    }
                    else if (responseobject.getString("responseCode").equals("200")) {

                        String responseMsg =responseobject.getString("responseMsg");
                        String booking_id =responseobject.getString("bookingId");

                        MyApplication.pickuploction=false;


                        System.out.println("RIDE split_fare responseMsg ===>> "+responseMsg);
                        System.out.println("RIDE split_fare booking_id ===>> "+booking_id);

                        CommanMethod.showAlert(responseobject.getString("responseMsg").toString(),Activity_ConfirmMybooking.this);

                        if(booking_id!=null)
                        {

                            SharedPreferences.Editor editor = mPref.edit();
                            editor.putString("bookingId", booking_id);
                            editor.apply();
                            MyApplication.arrivedrivermarker=true;
                            MyApplication.pickuploctiononetime=true;

                            String driverName =  responseobject.getString("driverName");
                            String driverId =  responseobject.getString("driverId");
                            System.out.println("activity_condirmMybooking=:"+driverId);
                            String licenseId =  responseobject.getString("licenseId");
                            String vehicle =  responseobject.getString("vehicle");
                            String imageUrl =  responseobject.getString("imageUrl");
                            String mobileNO =  responseobject.getString("mobileNO");
                            String message =  responseobject.getString("message");


                            driverfulldetails=new Driverfulldetails();
                            if(driverName!=null)
                            {
                                MyApplication.driverName=driverName;
                            }
                            else {
                                MyApplication.driverName="";
                            }
                            if(driverId!=null)
                            {
                                MyApplication.driverId=driverId;
                            } else {
                                MyApplication.driverId="";
                            }
                            if(licenseId!=null)
                            {
                                MyApplication.licenseId=licenseId;
                            }else {
                                MyApplication.licenseId="";
                            }
                            if(vehicle!=null)
                            {
                                MyApplication.vehicle=vehicle;
                            }else {
                                MyApplication.vehicle="";
                            }
                            if(imageUrl!=null)
                            {
                                MyApplication.imageUrl=imageUrl;
                            }else {
                                MyApplication.imageUrl="";
                            }
                            if(mobileNO!=null)
                            {
                                MyApplication.mobileNO=mobileNO;
                            }else {
                                MyApplication.mobileNO="";
                            }
                            MyApplication.conformbooking=true;
                            MyApplication.notificationconfiride=true;
                            Activity_ConfirmMybooking.this.finish();

                        }

                    }else if(responseobject.getString("responseCode").equals("0")){

                        CommanMethod.showAlert(responseobject.getString("responseMsg").toString(),Activity_ConfirmMybooking.this);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor = mPref.edit();
                editor.putString("clinetTokenpayment", "");
                editor.apply();


            } else {

                CommanMethod.showAlert("pleae try again",Activity_ConfirmMybooking.this);

            }

        }


        private String ridebook_trip() {
            String url = WebserviceUrlClass.RIDE;
            HttpClient client = new HttpClient(url);
            Log.e("before connection", "" + url);


            System.out.println("check confirmTrip===>"+userId);
            System.out.println("check pickLat===>"+String.valueOf(latitude));
            System.out.println("check confirmTrip pickLong ===>"+String.valueOf(longitude));
            System.out.println("check confirmTrip destLat ===>"+String.valueOf(lat));
            System.out.println("check confirmTrip destLong ===>"+String.valueOf(lng));
            System.out.println("check confirmTrip cabId ===>"+cabId);
            System.out.println("check confirmTrip pickupaddress ===>"+pickup_address);
            System.out.println("check confirmTrip dropaddress ===>"+drop_address);
            System.out.println("check confirmTrip paymentoption ===>"+paymentoption);
            System.out.println("check nonce paymentoption ===>"+MyApplication.payment_method_nonce);


            try {
                client.connectForMultipart();
                client.addFormPart("userId", userId);
                client.addFormPart("pickLat", String.valueOf(latitude));
                client.addFormPart("pickLong",String.valueOf(longitude));
                client.addFormPart("destLat", String.valueOf(lat));
                client.addFormPart("destLong",String.valueOf(lng));
                client.addFormPart("cabId", cabId);
                client.addFormPart("pickupaddress", pickup_address);
                client.addFormPart("destinationaddress", drop_address);
                client.addFormPart("paymentType",paymentoption);
                client.addFormPart("nonce", MyApplication.payment_method_nonce);

                client.finishMultipart();
                response = client.getResponse().toString();
                System.out.println("responsebooking"+response);
                System.out.println("sucess");
                Log.d("Confirm_trip_Respo", response);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;
        }


    }

    protected void validateGET_TIME(double latitude, double longitude, double destLat, double destLong) {
        int Time = (int) (new Date().getTime() / 1000);
        System.out.println("Integer : " + Time);
        String GET_TIMEFROMGOOGLEMAP = "https://maps.googleapis.com/maps/api/distancematrix/json?";
        StringBuilder builder = new StringBuilder();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        builder.append(GET_TIMEFROMGOOGLEMAP);
        builder.append("origins=");
        builder.append(latitude);
        builder.append(",");
        builder.append(longitude);
        builder.append("&");
        builder.append("destinations=");
        builder.append(destLat);
        builder.append(",");
        builder.append(destLong);
        builder.append("&mode=");
        builder.append("driving&departure_time=");
        builder.append(Time);
        builder.append("&traffic_model=best_guess&language=en-US&key=AIzaSyA35oihmkDaMt8O_FcQZXRhmIVcIFGo-Ag");
        client.post(builder.toString(), null,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject responseCode) {
                        super.onSuccess(statusCode, headers, responseCode);
                        Log.e("", "On Success of NoTs");
                        try {

                            JSONObject object = new JSONObject(responseCode.toString());
                            Log.e("responseGetTime", ":-" + object.toString(2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DriverTime driverTime = parseResultGteTime(responseCode.toString());
                        if (driverTime != null) {

                            Log.e("duration and time", driverTime.getDistance() + "," + driverTime.getDuration());
                            if(driverTime.getDurationvalue()!=null) {
                                double duration = Double.parseDouble(driverTime.getDurationvalue()) / 3600;
                                double trafficduration = Double.parseDouble(driverTime.getDuration_in_trafficvalue()) / 3600;
                                // double distanceTotal = Double.parseDouble(driverTime.getDistacevalue()) / 1000;
                                double distanceTotal = Double.parseDouble(driverTime.getDistacevalue()) / 1609.344;


                                // hourlyCharge = Math.round(ceil($totaltimeSec/900)*($additionHourcharge/15));

                                double ChargeDistace = distanceTotal - baseFareDistance;
                                double totlaEstimation, totlaEstimationmax;
                                if (ChargeDistace < 0) {
                                    totlaEstimation = baseFare + (duration * waitingCharge);

                                } else {


                                    totlaEstimation = baseFare + (ChargeDistace * perKm) + (duration * waitingCharge);


                                }
                                double totlaEstimationTraffic = baseFare + (ChargeDistace * perKm) + (trafficduration * waitingCharge);
                                totlaEstimationmax = totlaEstimationTraffic + am_additionHourcharge + am_passengerSurgcharge + am_luggagecharge + am_airportSurcharge + am_perpassengercharge + am_snowcharge + am_telephone_dispatch + am_additioncharge;

                                double totlaEstimationTrafficnewKB = Math.round(totlaEstimationTraffic * 100.0) / 100.0;

                                double totlaEstimationnewKB = Math.round(totlaEstimation * 100.0) / 100.0;
                                totlaEstimationnewKB = totlaEstimationnewKB + am_telephone_dispatch + am_additioncharge;

                                Log.e("Estimation", "==" + totlaEstimationnewKB + "," + totlaEstimationTrafficnewKB);
                                df2.setRoundingMode(RoundingMode.UP);
                                mtvestimateprice.setText(" $ " + "" + df2.format((totlaEstimationnewKB)) + "   $" + " " + df2.format((totlaEstimationmax)));
                            }
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                });
    }


    private DriverTime parseResultGteTime(String result) {

        DriverTime driverTime = new DriverTime();
        try {

            JSONObject response = new JSONObject(result);
            if (response.getString("status").equals("OK")) {

                JSONArray destination_addresses = response.getJSONArray("destination_addresses");
                driverTime.setDestination_addresses(destination_addresses.getString(0));
                JSONArray origin_addresses = response.getJSONArray("origin_addresses");
                driverTime.setOrigin_addresses(origin_addresses.getString(0));
                JSONArray rows = response.getJSONArray("rows");
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject rowobject = rows.getJSONObject(i);
                    JSONArray elements = rowobject.getJSONArray("elements");
                    for (int j = 0; j < elements.length(); j++) {
                        JSONObject elementobj = elements.getJSONObject(j);

                        JSONObject distance = elementobj.getJSONObject("distance");
                        driverTime.setDistance(distance.getString("text"));
                        driverTime.setDistacevalue(distance.getString("value"));

                        JSONObject duration = elementobj.getJSONObject("duration");
                        driverTime.setDuration(duration.getString("text"));
                        Log.e("durationjhgfhg", "" + duration.getString("text"));
                        driverTime.setDurationvalue(duration.getString("value"));
                        JSONObject traficduration = elementobj.getJSONObject("duration_in_traffic");
                        driverTime.setDuration_in_traffic(traficduration.getString("text"));
                        Log.e("durationjhgfhg", "" + traficduration.getString("text"));
                        driverTime.setDuration_in_trafficvalue(traficduration.getString("value"));

                    }

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return driverTime;
    }


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(2000);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    @Override
    protected void onPause() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onPause();
    }
}