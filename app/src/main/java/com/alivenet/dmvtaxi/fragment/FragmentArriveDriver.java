package com.alivenet.dmvtaxi.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telecom.Call;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.BackgroundService;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.SplitAddFrnd;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.GPSTracker;
import com.alivenet.dmvtaxi.commonutil.SharedPreference;
import com.alivenet.dmvtaxi.pojo.DriverTime;
import com.alivenet.dmvtaxi.pojo.Driverdetails;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import constant.MyApplication;
import constant.MyPreferences;
import cz.msebera.android.httpclient.Header;

/**
 * Created by ranjeet on 7/26/2016.
 */
public class FragmentArriveDriver extends Fragment {


    Button btnSplite;
    LinearLayout mlinerlayoutbottom;
    TextView marrivaltime,mname,mlicence,mtaximodel;
    ArrayList<Driverdetails> mDriverdetailsList=new ArrayList<>();
    GoogleMap googleMap;
    private BroadcastReceiver deliveryBroadcastReceiver;
    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    public  String mUserId;
    TextView mphonenumber;
    ImageView mphone,muserImage;
    Marker marker;
    double previousLocationLatitude;
    double previousLocationLongitude;
    float angle;
    ProgressDialog progressDialog;
    MarkerOptions markerOptions = new MarkerOptions();
    private static View view;
    double latitude;
    double longitude;
    private GPSTracker gps;
    com.sinch.android.rtc.calling.Call call;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {

            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);

        }
        try {
            view = inflater.inflate(R.layout.fragment_time_estemet, container, false);
        } catch (InflateException e) {
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);


        gps = new GPSTracker(getActivity());
        MyApplication.arrivedrivermarker=true;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        mlinerlayoutbottom=(LinearLayout)view.findViewById(R.id.ll_bottom);
        mphone=(ImageView)view.findViewById(R.id.iv_phone);
        mphonenumber=(TextView) view.findViewById(R.id.tv_phonenumber);
        marrivaltime=(TextView) view.findViewById(R.id.tv_textarrival);
        mname=(TextView) view.findViewById(R.id.tv_name);
        mlicence=(TextView) view.findViewById(R.id.tv_licence);
        mtaximodel=(TextView) view.findViewById(R.id.tv_model);
        muserImage=(ImageView)view.findViewById(R.id.imageView_close);
        getLatLong();
        btnSplite = (Button) view.findViewById(R.id.btn_splite_rides);

        btnSplite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), SplitAddFrnd.class);
                startActivity(in);
            }
        });



        mphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + mphonenumber.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });


        System.out.println("userId>>>>>>>>>>>>>>>>>>>>>"+mUserId);

        mname.setText(MyApplication.driverName);
        mlicence.setText(MyApplication.licenseId);
        mtaximodel.setText(MyApplication.vehicle);
        mphonenumber.setText(MyApplication.mobileNO);

        Picasso.with(getActivity()).load(MyApplication.imageUrl)
                .error(R.mipmap.avtar)
                .placeholder(R.mipmap.avtar)
                .into(muserImage);

        System.out.println("driverImageurl"+"chceck="+MyApplication.imageUrl);

        mphonenumber.setText("Call To"+" "+MyApplication.driverName);

        final SinchClient sinchClient = Sinch.getSinchClientBuilder()
                .context(getActivity())
                .userId(mUserId)
                .applicationKey("3b013f09-db1f-422e-ac7b-c6498e119612")
                .applicationSecret("LMhelEFYtUqms55VX1C7MQ==")
                .environmentHost("sandbox.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.start();
        mphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    if(MyApplication.mobileNO!=null&&!MyApplication.mobileNO.equals("")) {
                        call = sinchClient.getCallClient().callPhoneNumber("+"+MyApplication.mobileNO);
                        System.out.println("mobile_number"+ MyApplication.mobileNO);
                        //call = sinchClient.getCallClient().callPhoneNumber("+918510834641");
                        try {
                            call.addCallListener(new SinchCallListener());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mphonenumber.setText("Hang Up");
                    }
                } else {
                    call.hangup();
                }
            }
        });

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

    private class SinchCallListener implements CallListener {
        //the call is ended for any reason
        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedCall) {
            call = null; //no longer a current call
            mphonenumber.setText("Call"); //change text on button
            mphonenumber.setText(""); //empty call state
            //hardware volume buttons should revert to their normal function
            // setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }
        //call is connected
        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call establishedCall) {
            //change the call state in the view
            mphonenumber.setText("connected");
            //the hardware volume buttons should control the voice stream volume
            //setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }
        //call is trying to connect
        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call progressingCall) {
            //set call state to "ringing" in the view
            mphonenumber.setText("ringing");
        }
        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> pushPairs) {
            //intentionally left empty
        }
    }

    private void getLatLong() {

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("latitude....=", "" + latitude);
            Log.d("longitude....=", "" + longitude);
            getCurrentLocation(latitude,longitude);
            callBackgroundService(latitude,longitude);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
    @Override
    public void onResume() {


        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);

        DataUpdateBackgroundService();


        super.onResume();

    }
    @Override
    public void onPause() {

        getActivity().unregisterReceiver(deliveryBroadcastReceiver);
        super.onPause();

    }
    public void getCurrentLocation(double latitude,double longitude)
    {
        googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.maparrive)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;


        if (googleMap != null) {


                googleMap.clear();
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title("");

                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(10).build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
                googleMap.addMarker(marker);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                googleMap.setMyLocationEnabled(true);


        }

        if(latitude!=0.0d&&longitude!=0.0d) {

            Intent intent = new Intent(getActivity(), BackgroundService.class);
            intent.putExtra("userid",mUserId);
            intent.putExtra("lat",String.valueOf(latitude));
            intent.putExtra("long",String.valueOf(longitude));
            getActivity().startService(intent);
        }

    }

    public void DataUpdateBackgroundService(){

        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.car);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("update_map1");
        deliveryBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO  map updations work here
                System.out.println("update run FragmentArrive");

                mDriverdetailsList = ( ArrayList<Driverdetails>)intent.getSerializableExtra("backgroundArraylist");
                // System.out.println("mDriverdetailsList info===>>>>"+mDriverdetailsList+mDriverdetailsList.size());
                if(mDriverdetailsList!=null)
                {
                    System.out.println("mDriverdetailsList infoarrive===>>>>"+mDriverdetailsList+mDriverdetailsList.size());
                    for(int j=0;j<mDriverdetailsList.size();j++)
                    {

                        if(mDriverdetailsList.get(j).getDriverdetailslocations()!=null&&mDriverdetailsList.get(j).getDriverdetailslocations().size()>0){

                            String lat = "";
                            String lng = "";

                            for(int i=0;i< mDriverdetailsList.get(j).getDriverdetailslocations().size();i++)
                            {
                                if(MyApplication.driverId!=null&&!MyApplication.driverId.equals("")&&MyApplication.driverId.equals(mDriverdetailsList.get(j).getDriverId()))
                                {
                                    System.out.println("ArriveDriver"+MyApplication.driverId);
                                    lat =  mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLatitude();
                                    lng=mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLongitude();


                                    validateGET_TIME(latitude,longitude,Double.parseDouble(lat),Double.parseDouble(lng));
                                    if(marker!=null)
                                    {
                                        System.out.println("ArriveDriver"+"ifunder>>>>>>>>>>>>>>>>>>>>>");
                                        marker.setPosition(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)));
                                        angle = (float) finalBearing(previousLocationLatitude, previousLocationLongitude, Double.parseDouble(lat), Double.parseDouble(lng));
                                        CameraPosition cameraPosition =
                                                new CameraPosition.Builder()
                                                        .target(new LatLng(previousLocationLatitude, previousLocationLongitude))
                                                        .bearing(angle)
                                                        .tilt(90)
                                                        .zoom(15)
                                                        .build();
                                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                        previousLocationLatitude =Double.parseDouble(lat);
                                        previousLocationLongitude = Double.parseDouble(lng);


                                    }else {
                                        System.out.println("ArriveDriver"+"elseunder>>>>>>>>>>>>>>>>>>>>>");
                                        marker= googleMap.addMarker(markerOptions
                                                .icon(icon)
                                                .position(new LatLng(Double.parseDouble(mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLatitude()),Double.parseDouble(mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLongitude())))
                                                .draggable(true).visible(true));

                                        CameraPosition cameraPosition =
                                                new CameraPosition.Builder()
                                                        .target(new LatLng(previousLocationLatitude, previousLocationLongitude))
                                                        .bearing(angle)
                                                        .tilt(90)
                                                        .zoom(15)
                                                        .build();
                                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                        previousLocationLatitude =Double.parseDouble(mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLatitude());
                                        previousLocationLongitude = Double.parseDouble(mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLongitude());

                                    }
                                }

                            }

                        }

                    }
                }

            }
        }; getActivity().registerReceiver(deliveryBroadcastReceiver , intentFilter);


    }

    static public double finalBearing(double lat1, double long1, double lat2, double long2)
    {
        return (_bearing(lat2, long2, lat1, long1) + 180.0) % 360;
    }
    static private double _bearing(double lat1, double long1, double lat2, double long2)
    {
        double degToRad = Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),
                Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
        ) * 180/Math.PI;
    }
    protected void validateGET_TIME(double latitude, double longitude, double destLat, double destLong) {
       System.out.println("fragmentArrive"+"dkljkadjsfl");
        String  GET_TIMEFROMGOOGLEMAP ="https://maps.googleapis.com/maps/api/distancematrix/json?";
        StringBuilder builder=new StringBuilder();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
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
        builder.append("&mode=driving&language=en-US");
        client.post(builder.toString(), null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        //progressDialog.show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject responseCode) {
                        super.onSuccess(statusCode, headers, responseCode);
                        Log.e("", "On Success of NoTs");
                        System.out.println("successasdgasdg"+"dkljkadjsfl");
                        try{

                            JSONObject object=new JSONObject(responseCode.toString());
                            Log.e("responseGetTime",":-"+object.toString(2));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        DriverTime driverTime=parseResult(responseCode.toString());
                        if(driverTime!=null)
                        {

                            StringBuilder builder=new StringBuilder();
                            builder.append("ESTIMATED ARRIVAL TIME IN  ");
                            builder.append(driverTime.duration);
                            marrivaltime.setText(builder.toString());



                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                       /* progressDialog.dismiss();
                        CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());*/
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        //progressDialog.dismiss();
                    }

                });
    }



    private DriverTime  parseResult(String result) {

        DriverTime driverTime=new DriverTime();
        try {

            JSONObject response = new JSONObject(result);
            if (response.getString("status").equals("OK")) {

                JSONArray destination_addresses=response.getJSONArray("destination_addresses");
                driverTime.setDestination_addresses(destination_addresses.getString(0));
                JSONArray origin_addresses=response.getJSONArray("origin_addresses");
                driverTime.setOrigin_addresses(origin_addresses.getString(0));
                JSONArray rows=response.getJSONArray("rows");
                for(int i=0;i<rows.length();i++) {
                    JSONObject rowobject = rows.getJSONObject(i);
                    JSONArray elements = rowobject.getJSONArray("elements");
                    for(int j=0;j<elements.length();j++)
                    {
                        JSONObject elementobj=elements.getJSONObject(j);

                        JSONObject distance = elementobj.getJSONObject("distance");
                        driverTime.setDistance(distance.getString("text"));
                        driverTime.setDistacevalue(distance.getString("value"));

                        JSONObject duration = elementobj.getJSONObject("duration");
                        driverTime.setDuration(duration.getString("text"));
                        Log.e("durationjhgfhg", "" + duration.getString("text"));
                        driverTime.setDurationvalue(duration.getString("value"));
                    }
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return driverTime;
    }

    public void callBackgroundService(final double latitude, final double longitude) {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            if (latitude != 0.0d && longitude != 0.0d) {

                                Intent intent = new Intent(getActivity(), BackgroundService.class);
                                intent.putExtra("userid", mUserId);
                                intent.putExtra("lat", String.valueOf(latitude));
                                intent.putExtra("long", String.valueOf(longitude));
                                getActivity().startService(intent);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
    }



}
