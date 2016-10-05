package com.alivenet.dmvtaxi.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.Activity_ConfirmMybooking;
import com.alivenet.dmvtaxi.BackgroundService;
import com.alivenet.dmvtaxi.Cardformat;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.ReservationActivity;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.GPSTracker;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.CarInfoDialog;
import com.alivenet.dmvtaxi.pojo.CabListResource;
import com.alivenet.dmvtaxi.pojo.DriverId;
import com.alivenet.dmvtaxi.pojo.Driverdetails;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.PayPalOverrides;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import constant.MyApplication;
import constant.MyPreferences;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/16/2016.
 */
public class FragmentMainScreen extends Fragment  implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    GoogleMap googleMap;
    boolean flaglogin = false;
    public String mUserId,userType;
    double latitude;
    double longitude;
    String address1;
    private static double lat;
    private static double lng;
    double latitudepickup;
    double longitudepickup;
    double latitudedestination;
    double longitudedestination;
    RecyclerView recyclerView;
    String mcabId, baseFare, baseFareDistance, perKm, waitingCharge;
    String paymenoption ;
    Button mbtnPickup;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CabListResource> mCabDetailsList = new ArrayList<CabListResource>();
    ArrayList<DriverId> driverIds=new ArrayList<>();
    ArrayList<Driverdetails> mDriverdetailsList = new ArrayList<>();
    private  LatLngBounds BOUNDS_MOUNTAIN_VIEW;
    private int PLACE_PICKER_REQUEST;
    LinearLayout pickup_location, destination_lovation;
    Spinner passengerSpinner;
    TextView mtvpickuplocation, mdestionlocation;
    ArrayList<Integer> passangerlist;
    ArrayAdapter passengerAdapter;
    Button mpayments, mcredit, mvip, msplit;
    String passenger="2";
    Button btnReservation;
    private static View view;
    RelativeLayout mrlfulldetails;
    private BroadcastReceiver sendBroadcastReceiver;
    Marker markerdriverlocation;
    MarkerOptions marker1;
    Marker destinationMarker;
    Marker marker;
    String tag;
    BraintreeFragment mBraintreeFragment;
    Button mbtncar,mbtnVan,mbtnhandicape;
    String maxusercounter;
    ProgressDialog prgDialog;
    public boolean droplocationsflag;
    int driverlocation,driverlocation1;
    public String madditioncharge;
    public String mtelephone_dispatch;
    public String mpassengerSurgcharge;
    public String mluggagecharge;
    public String mairportSurcharge;
    public String madditionHourcharge;
    public String mperpassengercharge;
    public String msnowcharge;



    private String clientToken;
    MarkerOptions markerOptions = new MarkerOptions();
    protected String mLastUpdateTime;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected Boolean mRequestingLocationUpdates=true;
    /**
     * Provides the entry point to Google Play services.
     */
    public GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    private GPSTracker gps;

    MarkerOptions markerOptions1 = new MarkerOptions();
    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
    public static FragmentMainScreen newInstance(String sectionTitle) {

        FragmentMainScreen fragment = new FragmentMainScreen();
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
        if (view != null) {

            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);

        }
        try {
            view = inflater.inflate(R.layout.deashboard, container, false);
            //googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapdirection)).getMap();
        } catch (InflateException e) {
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        freeMemory();
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);
        flaglogin = mPref.getBoolean("loginflag", false);
        userType = mPref.getString("userType",null);
        mpayments = (Button) view.findViewById(R.id.btn_payments);
        mcredit = (Button) view.findViewById(R.id.btn_credit);
        mvip = (Button) view.findViewById(R.id.btn_vip);
        msplit = (Button) view.findViewById(R.id.btn_split);
        passengerSpinner = (Spinner) view.findViewById(R.id.passenger_user);
        mtvpickuplocation = (TextView) view.findViewById(R.id.tv_pickuplocation);
        mdestionlocation = (TextView) view.findViewById(R.id.tv_destnation);
        mrlfulldetails = (RelativeLayout) view.findViewById(R.id.rl_fulldetails);
        passangerlist = new ArrayList<>();
        pickup_location = (LinearLayout) view.findViewById(R.id.pick_up_location);
        destination_lovation = (LinearLayout) view.findViewById(R.id.destination_location);
        btnReservation = (Button) view.findViewById(R.id.btn_reservations);
        mbtnPickup = (Button) view.findViewById(R.id.btn_picup);
        recyclerView = (RecyclerView) view.findViewById(R.id.hrlist_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        mbtncar=(Button)view.findViewById(R.id.btn_car);
        mbtnVan=(Button)view.findViewById(R.id.btn_van);
        mbtnhandicape=(Button)view.findViewById(R.id.btn_handicapt);
        mbtncar.setBackgroundResource(R.mipmap.car_btn1);
        prgDialog = new ProgressDialog(activity);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        MyApplication.payment_method_nonce="";


        mbtncar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mCabDetailsList!=null&&mCabDetailsList.size()>0) {

                        mcabId = mCabDetailsList.get(0).getmId();
                        MyPreferences.getActiveInstance(getActivity()).setcabId(mcabId);

                        maxusercounter = mCabDetailsList.get(0).getMaxUsers();
                        baseFare = mCabDetailsList.get(0).getBaseFare();
                        baseFareDistance = mCabDetailsList.get(0).getBaseFareDistance();
                        perKm = mCabDetailsList.get(0).getPerKm();
                        waitingCharge = mCabDetailsList.get(0).getWaitingCharge();


                         madditioncharge=mCabDetailsList.get(0).getAdditioncharge();
                         mtelephone_dispatch=mCabDetailsList.get(0).getTelephone_dispatch();
                         mpassengerSurgcharge=mCabDetailsList.get(0).getPassengerSurgcharge();
                         mluggagecharge=mCabDetailsList.get(0).getLuggagecharge();
                         mairportSurcharge=mCabDetailsList.get(0).getAirportSurcharge();
                         madditionHourcharge=mCabDetailsList.get(0).getAdditionHourcharge();
                         mperpassengercharge=mCabDetailsList.get(0).getPassengerSurgcharge();
                         msnowcharge=mCabDetailsList.get(0).getSnowcharge();


                        MyApplication.passngercont=Integer.parseInt(maxusercounter);
                        SpinerAdapterSet(maxusercounter);
                        String carName = mCabDetailsList.get(0).getCabName();
                        String note = mCabDetailsList.get(0).getNote();
                        String iconUrl = mCabDetailsList.get(0).getCabIcon();


                        mbtncar.setBackgroundResource(R.mipmap.car_btn2);
                        mbtnVan.setBackgroundResource(R.mipmap.van_btn1);
                        mbtnhandicape.setBackgroundResource(R.mipmap.wheel_btn1);

                        new CarInfoDialog(getActivity(), carName, baseFare, baseFareDistance, perKm, waitingCharge, note, iconUrl).show();
                    }else {

                        new CarInfoDialog(getActivity(), "Not Avilable", "", "", "", "","", "").show();
                    }
                }
            });

        mbtnVan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mCabDetailsList!=null&&mCabDetailsList.size()>2)
                    {

                           mcabId = mCabDetailsList.get(2).getmId();
                           MyPreferences.getActiveInstance(getActivity()).setcabId(mcabId);
                           String carName = mCabDetailsList.get(2).getCabName();
                           String note = mCabDetailsList.get(2).getNote();
                           String iconUrl = mCabDetailsList.get(2).getCabIcon();

                           maxusercounter = mCabDetailsList.get(2).getMaxUsers();
                           baseFare = mCabDetailsList.get(2).getBaseFare();
                           baseFareDistance = mCabDetailsList.get(2).getBaseFareDistance();
                           perKm = mCabDetailsList.get(2).getPerKm();
                           waitingCharge = mCabDetailsList.get(2).getWaitingCharge();
                           SpinerAdapterSet(maxusercounter);

                            madditioncharge=mCabDetailsList.get(2).getAdditioncharge();
                            mtelephone_dispatch=mCabDetailsList.get(2).getTelephone_dispatch();
                            mpassengerSurgcharge=mCabDetailsList.get(2).getPassengerSurgcharge();
                            mluggagecharge=mCabDetailsList.get(2).getLuggagecharge();
                            mairportSurcharge=mCabDetailsList.get(2).getAirportSurcharge();
                            madditionHourcharge=mCabDetailsList.get(2).getAdditionHourcharge();
                            mperpassengercharge=mCabDetailsList.get(2).getPassengerSurgcharge();
                            msnowcharge=mCabDetailsList.get(2).getSnowcharge();

                           MyApplication.passngercont=Integer.parseInt(maxusercounter);
                           mbtncar.setBackgroundResource(R.mipmap.car_btn1);
                           mbtnVan.setBackgroundResource(R.mipmap.van_btn2);
                           mbtnhandicape.setBackgroundResource(R.mipmap.wheel_btn1);
                        new CarInfoDialog(getActivity(), carName, baseFare, baseFareDistance, perKm, waitingCharge, note, iconUrl).show();
                    }else {
                        new CarInfoDialog(getActivity(), "Not Avilable", "", "", "", "", "", "").show();
                    }
                }
            });


        mbtnhandicape.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mCabDetailsList!=null&&mCabDetailsList.size()>1) {

                            mcabId = mCabDetailsList.get(1).getmId();
                            MyPreferences.getActiveInstance(getActivity()).setcabId(mcabId);
                            String carName = mCabDetailsList.get(1).getCabName();
                            String note = mCabDetailsList.get(1).getNote();
                            String iconUrl = mCabDetailsList.get(1).getCabIcon();
                            maxusercounter = mCabDetailsList.get(1).getMaxUsers();
                            baseFare = mCabDetailsList.get(1).getBaseFare();
                            baseFareDistance = mCabDetailsList.get(1).getBaseFareDistance();
                            perKm = mCabDetailsList.get(1).getPerKm();
                            waitingCharge = mCabDetailsList.get(1).getWaitingCharge();


                           MyApplication.passngercont=Integer.parseInt(maxusercounter);

                           madditioncharge=mCabDetailsList.get(1).getAdditioncharge();
                           mtelephone_dispatch=mCabDetailsList.get(1).getTelephone_dispatch();
                           mpassengerSurgcharge=mCabDetailsList.get(1).getPassengerSurgcharge();
                           mluggagecharge=mCabDetailsList.get(1).getLuggagecharge();
                           mairportSurcharge=mCabDetailsList.get(1).getAirportSurcharge();
                           madditionHourcharge=mCabDetailsList.get(1).getAdditionHourcharge();
                           mperpassengercharge=mCabDetailsList.get(1).getPassengerSurgcharge();
                           msnowcharge=mCabDetailsList.get(1).getSnowcharge();

                           SpinerAdapterSet(maxusercounter);

                            mbtncar.setBackgroundResource(R.mipmap.car_btn1);
                            mbtnVan.setBackgroundResource(R.mipmap.van_btn1);
                            mbtnhandicape.setBackgroundResource(R.mipmap.wheel_btn2);

                            new CarInfoDialog(getActivity(), carName, baseFare, baseFareDistance, perKm, waitingCharge, note, iconUrl).show();
                        }else {

                            new CarInfoDialog(getActivity(), "Not Avilable", "", "", "", "","", "").show();
                    }


                }
            });


        System.out.println(" location Drop locations lat ==>>>"+ MyApplication.droplocationslat);
        System.out.println("location Drop locations long ==>>>"+ MyApplication.droplocationslong);
        System.out.println("location pickup  locations lat  ==>>>"+ MyApplication.pickuplocationslat);
        System.out.println("location Drop locations long  ==>>>"+ MyApplication.pickuplocationslong);

        googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapdirection)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLACE_PICKER_REQUEST = 1;

                    try {
                        PlacePicker.IntentBuilder intentBuilder =
                                new PlacePicker.IntentBuilder();
                        if(BOUNDS_MOUNTAIN_VIEW!=null)
                        {
                            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                        }

                        Intent intent = intentBuilder.build(getActivity());
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException
                            | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

            }
        });


        destination_lovation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLACE_PICKER_REQUEST = 2;
                    try {
                        PlacePicker.IntentBuilder intentBuilder =
                                new PlacePicker.IntentBuilder();
                        if(BOUNDS_MOUNTAIN_VIEW!=null)
                        {
                            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                        }

                        Intent intent = intentBuilder.build(getActivity());
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException
                            | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
            }
        });

        RequestParams params = new RequestParams();
        loginWs(params);


        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckvalidationResevation()) {
                    Intent in = new Intent(getActivity(), ReservationActivity.class);
                    Bundle b = new Bundle();
                    b.putDouble("pickLat", latitude);
                    b.putDouble("pickLong", longitude);
                    b.putString("PaymentType",paymenoption);
                    in.putExtras(b);
                    Bundle b1 = new Bundle();
                    b1.putDouble("destLat", lat);
                    b1.putDouble("destLong", lng);
                    in.putExtras(b1);
                    in.putExtra("cabId", mcabId);
                    startActivity(in);
                }
            }
        });

        mbtnPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                            if (Checkvalidation()) {
                                Intent intent = new Intent(getActivity(), Activity_ConfirmMybooking.class);
                                Bundle b = new Bundle();
                                b.putDouble("distantionLat", latitudedestination);
                                b.putDouble("distantionLng", longitudedestination);
                                Bundle b1 = new Bundle();
                                if (latitudepickup != 0.0d && longitudepickup != 0.0d) {
                                    b1.putDouble("currentlatitude", latitudepickup);
                                    b1.putDouble("currentlongitude", longitudepickup);
                                } else {
                                    b1.putDouble("currentlatitude", latitude);
                                    b1.putDouble("currentlongitude", longitude);
                                }

                                intent.putExtra("pickup_address", "" + mtvpickuplocation.getText().toString().trim());
                                intent.putExtra("drop_address", "" + mdestionlocation.getText().toString().trim());
                                intent.putExtra("cabId", mcabId);
                                intent.putExtra("userId", mUserId);

                                System.out.println("paymenoption=="+paymenoption);

                                intent.putExtra("paymentoption", paymenoption);
                                intent.putExtra("passenger", passenger);
                                intent.putExtra("baseFare", baseFare);
                                intent.putExtra("baseFareDistance", baseFareDistance);
                                intent.putExtra("perKm", perKm);
                                intent.putExtra("waitingCharge", waitingCharge);


                                intent.putExtra("madditioncharge", madditioncharge);
                                intent.putExtra("mtelephone_dispatch", mtelephone_dispatch);
                                intent.putExtra("mpassengerSurgcharge", mpassengerSurgcharge);
                                intent.putExtra("mluggagecharge", mluggagecharge);
                                intent.putExtra("mairportSurcharge", mairportSurcharge);
                                intent.putExtra("madditionHourcharge", madditionHourcharge);
                                intent.putExtra("mperpassengercharge", mperpassengercharge);
                                intent.putExtra("msnowcharge", msnowcharge);

                                intent.putExtra("payment_nonce", MyApplication.payment_method_nonce);
                                intent.putExtras(b);
                                intent.putExtras(b1);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }


                    }



        });


        mpayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymenoption = "2";
                tag = "2";

                try {
                    mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), clientToken);
                    // mBraintreeFragment is ready to use!

                    PayPalOverrides.setFuturePaymentsOverride(true);
                    PayPal.authorizeAccount(mBraintreeFragment);



                } catch (InvalidArgumentException e) {
                    // There was an issue with your authorization string.
                }



            }
        });


            mcredit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymenoption = "1";
                    PaymentRequest paymentRequest;
                    tag = "1";

                    if(clientToken==null)
                    {

                        String clientToken=mPref.getString("clinetTokenpayment","");
                        paymentRequest = new PaymentRequest()
                                .clientToken(clientToken)
                                // .amount("$10.00")
                                .primaryDescription("Ride payment")
                                .secondaryDescription("DMV Taxi")
                                .submitButtonText("Submit");
                        Log.e("clinetTokenIF",""+clientToken);
                    }else {
                         paymentRequest = new PaymentRequest()
                                .clientToken(clientToken)
                                // .amount("$10.00")
                                .primaryDescription("Ride payment")
                                .secondaryDescription("DMV Taxi")
                                .submitButtonText("Submit");
                        Log.e("clinetTokenElse",""+clientToken);
                    }
                    startActivityForResult(paymentRequest.getIntent(getActivity()), 1);
                }
            });



        mvip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paymenoption = "3";
                if (userType.equals("Normal"))
                {
                    CommanMethod.showAlert("           You are not vip user.",getActivity());
                }else {

                    mpayments.setBackgroundResource(R.mipmap.btn2);
                    mcredit.setBackgroundResource(R.mipmap.btn2);
                    mvip.setBackgroundResource(R.mipmap.btn1);

                    Intent in = new Intent(getActivity(), Cardformat.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);

                }

            }
        });
        msplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymenoption = "4";
                mpayments.setBackgroundResource(R.mipmap.btn2);
                mcredit.setBackgroundResource(R.mipmap.btn2);
                mvip.setBackgroundResource(R.mipmap.btn2);
                msplit.setBackgroundResource(R.mipmap.btn1);
                PaymentRequest paymentRequest = new PaymentRequest()
                        .clientToken(clientToken)
                        // .amount("$10.00")
                        .primaryDescription("Ride payment")
                        .secondaryDescription("DMV Taxi")
                        .submitButtonText("Submit");

                startActivityForResult(paymentRequest.getIntent(getActivity()), 1);
            }
        });

        passengerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                passenger = passengerSpinner.getSelectedItem().toString();
              //  MyApplication.passngercont=Integer.parseInt(passenger);
                Log.i("Selected item : ", passenger);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        if(userType.equals("Vip")){
            paymenoption = "3";
            mpayments.setBackgroundResource(R.mipmap.btn2);
            mcredit.setBackgroundResource(R.mipmap.btn2);
            mvip.setBackgroundResource(R.mipmap.btn1);
            msplit.setBackgroundResource(R.mipmap.btn2);
        }
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        gps = new GPSTracker(getActivity());
        getLatLong();
        callBackgroundService();
        getCurrentLocation();
        DataUpdateBackgroundService();
        getToken(mUserId);
        if( MyApplication.frequentlocationset==true)
        {
            showpresetmarker();
        }

        return view;
    }

    public void showpresetmarker()
    {

        System.out.println("location pickup  locations lat  ==>>>" + MyApplication.pickuplocationslat);
        System.out.println("location Drop locations long  ==>>>" + MyApplication.pickuplocationslong);


        if (MyApplication.pickuplocationslat != null &&!MyApplication.pickuplocationslat.equals("")&& MyApplication.pickuplocationslong != null&&!MyApplication.pickuplocationslong.equals("")) {

            latitudedestination=Double.valueOf(MyApplication.pickuplocationslat);
            longitudedestination=Double.valueOf(MyApplication.pickuplocationslong);

            if (destinationMarker != null) {
                System.out.println("ifunder>>>>>>>>>>>>"+"location update frequentDestion");
                destinationMarker.setPosition(new LatLng(Double.valueOf(MyApplication.pickuplocationslat), Double.valueOf(MyApplication.pickuplocationslong)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

            } else {
                System.out.println("Elseunder>>>>>>>>>>>>"+"location update frequentDestion");
                destinationMarker = googleMap.addMarker(markerOptions
                        .position(new LatLng(Double.valueOf(MyApplication.pickuplocationslat), Double.valueOf(MyApplication.pickuplocationslong)))
                        .draggable(true).visible(true));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.valueOf(MyApplication.pickuplocationslat), Double.valueOf(MyApplication.pickuplocationslong))).zoom(15).build();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mdestionlocation.setText(MyApplication.google_address);
            }



            MyApplication.presetflag=true;

        }
        if (MyApplication.droplocationslat != null&&!MyApplication.droplocationslat.equals("") && MyApplication.droplocationslong != null&&!MyApplication.droplocationslong.equals("")) {
            droplocationsflag=true;

                latitudepickup=Double.valueOf(MyApplication.droplocationslat);
                longitudepickup=Double.valueOf(MyApplication.droplocationslong);

                if(marker!=null)
                {
                    marker.setPosition(new LatLng(Double.valueOf(MyApplication.droplocationslat), Double.valueOf(MyApplication.droplocationslong)));
                    System.out.println("Ifunder>>>>>>>>>>>>"+"location update frequent");
                }else {
                    System.out.println("Elseunder>>>>>>>>>>>>"+"location update frequent");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Double.valueOf(MyApplication.droplocationslat), Double.valueOf(MyApplication.droplocationslong))).zoom(15).build();

                    marker = googleMap.addMarker(marker1
                            .position(new LatLng(Double.valueOf(MyApplication.droplocationslat), Double.valueOf(MyApplication.droplocationslong))).title(""));
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }

            mtvpickuplocation.setText(MyApplication.pickup_google_address);
            MyApplication.presetflag=true;

        }



    }
    //*//***for getting lattitude and longitude****//*///
    private void getLatLong() {

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("latitude....=", "" + latitude);
            Log.d("longitude....=", "" + longitude);


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(MyApplication.pickuploction==true)
        {

        }else {

            getCurrentLocation();
        }

    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        //  Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                // setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                //  mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            //  updateUI();
        }
    }
    protected synchronized void buildGoogleApiClient() {
        //  Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {

        freeMemory();

        System.out.println("Check"+"onResume");
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        if(sendBroadcastReceiver==null)
        {

        }else {
            getActivity().unregisterReceiver(sendBroadcastReceiver);
            sendBroadcastReceiver=null;
        }

        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }


        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void getCurrentLocation() {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        if (googleMap != null) {

              if(marker!=null)
                {

                   marker.setPosition(new LatLng(latitude,longitude));

                    //System.out.println("ifunder>>>>>>>>>>>>"+"location update"+"latitude"+latitude+"longtitude"+latitude);

                }else {
                    googleMap.clear();
                      mtvpickuplocation.setText("PICKUP LOCATION");
                      mdestionlocation.setText("DESTINATION");
                      mpayments.setBackgroundResource(R.mipmap.btn2);
                      mcredit.setBackgroundResource(R.mipmap.btn2);
                      mvip.setBackgroundResource(R.mipmap.btn1);

                      if(driverIds!=null)
                      {
                          driverIds.clear();
                      }
                    marker1 = new MarkerOptions();
                    marker1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    System.out.println("elseunder>>>>>>>>>>>>"+"location update");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude)).zoom(20).build();


                    marker = googleMap.addMarker(marker1.position( new LatLng(latitude, longitude)).title(""));



                    BOUNDS_MOUNTAIN_VIEW= new LatLngBounds(
                            new LatLng(latitude, longitude), new LatLng(latitude, longitude));

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                    googleMap.setMyLocationEnabled(true);
                }

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }

        }



    }


    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        Log.e("payment_method_nonce"," hello narendra  ");


        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(getActivity(), data);
            //final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            address1 = address.toString();
            LatLng latlong = place.getLatLng();
            lat = latlong.latitude;
            lng = latlong.longitude;
            System.out.println("latitude"+lat);
            System.out.println("longitude"+lng);
            if (PLACE_PICKER_REQUEST == 1) {

                mtvpickuplocation.setText(address1);
                MyApplication.droplocationslat="";
                MyApplication.droplocationslong="";
                latitudepickup=lat;
                longitudepickup=lng;
                Log.e("pick_up","user Select Pickup Location");
                MyApplication.frequentlocationset=false;
                MyApplication.pickuploction=true;
                if(marker!=null)
                {
                    marker.setPosition(latlong);
                    System.out.println("ifunderOnctivity-->>>>>>>>>"+"pickuplocation");
                }else{
                    System.out.println("ElseunderOnctivity-->>>>>>>>>"+"pickuplocation");
                    marker1 = new MarkerOptions();
                    marker1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    marker = googleMap.addMarker(marker1
                            .position(latlong)
                            .draggable(true).visible(true));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                }

            }
            if (PLACE_PICKER_REQUEST == 2) {
                mdestionlocation.setText(address1);
                latitudedestination=lat;
                longitudedestination=lng;
                MyApplication.pickuplocationslat="";
                MyApplication.pickuplocationslong="";
                markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Log.e("pick_up","user Select drop Location");
                MyApplication.frequentlocationset=false;
             if (destinationMarker != null) {
                    System.out.println("ifunderOnctivity-->>>>>>>>>"+">>>>>>>>>>>");
                    destinationMarker.setPosition(latlong);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                } else {
                    System.out.println("ElseunderOnctivity-->>>>>>>>>"+">>>>>>>>>>>");
                    destinationMarker = googleMap.addMarker(markerOptions
                            .position(latlong)
                            .draggable(true).visible(true));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
               }


            }


            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

        }

        else if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
//            Log.e("payment_method_nonce","  "+paymentMethodNonce.getNonce());
            MyApplication.payment_method_nonce = paymentMethodNonce.getNonce();

            System.out.println("getNonce>>>>>>>>>>>>>"+ paymentMethodNonce.getNonce());

            if(tag.equals("1")&&!MyApplication.payment_method_nonce.equals(""))
            {
                paymenoption = "1";
                mpayments.setBackgroundResource(R.mipmap.btn2);
                mcredit.setBackgroundResource(R.mipmap.btn1);
                mvip.setBackgroundResource(R.mipmap.btn2);
                msplit.setBackgroundResource(R.mipmap.btn2);
                Log.e("payment btn tag","creadit card tag ");


            }else if(tag.equals("2")&&!MyApplication.payment_method_nonce.equals(""))
            {
                paymenoption = "2";

                mpayments.setBackgroundResource(R.mipmap.btn1);
                mcredit.setBackgroundResource(R.mipmap.btn2);
                mvip.setBackgroundResource(R.mipmap.btn2);
                msplit.setBackgroundResource(R.mipmap.btn2);
                Log.e("payment btn tag","paypal card tag ");


            }

        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }


    public void loginWs(RequestParams params) {

        String url = WebserviceUrlClass.Cablist;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                prgDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {

                    JSONObject object=new JSONObject(response.toString());

                    Log.d("CabListResposnse", object.toString(2));
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {
                            mCabDetailsList.clear();
                            CabListResource mCabDetails = null;
                            JSONArray jsonArray = null;
                            jsonArray = response.getJSONArray("cablist");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mCabDetails = new CabListResource();
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                if (obj.getString("id").toString().trim().equals("null") || obj.getString("id").equals("")) {
                                    mCabDetails.setmId("");
                                } else {
                                    mCabDetails.setmId(obj.getString("id").toString().trim());
                                }

                                if (obj.getString("cab_name").toString().trim().equals("null") || obj.getString("cab_name").equals("")) {
                                    mCabDetails.setCabName("");
                                } else {
                                    mCabDetails.setCabName(obj.getString("cab_name").toString().trim());
                                }

                                if (obj.getString("max_user").toString().trim().equals("null") || obj.getString("max_user").equals("")) {
                                    mCabDetails.setMaxUsers("");
                                } else {
                                    mCabDetails.setMaxUsers(obj.getString("max_user").toString().trim());
                                }

                                if (obj.getString("cab_icon").toString().trim().equals("null") || obj.getString("cab_icon").equals("")) {
                                    mCabDetails.setCabIcon("");
                                } else {
                                    mCabDetails.setCabIcon(obj.getString("cab_icon").toString().trim());
                                }


                                if (obj.getString("note").toString().trim().equals("null") || obj.getString("note").equals("")) {
                                    mCabDetails.setNote("");
                                } else {
                                    mCabDetails.setNote(obj.getString("note").toString().trim());
                                }

                                if (obj.getString("per_km").toString().trim().equals("null") || obj.getString("per_km").equals("")) {
                                    mCabDetails.setPerKm("");
                                } else {
                                    mCabDetails.setPerKm(obj.getString("per_km").toString().trim());
                                }

                                if (obj.getString("base_fare").toString().trim().equals("null") || obj.getString("base_fare").equals("")) {
                                    mCabDetails.setBaseFare("");
                                } else {
                                    mCabDetails.setBaseFare(obj.getString("base_fare").toString().trim());
                                }

                                if (obj.getString("base_fare_distance").toString().trim().equals("null") || obj.getString("base_fare_distance").equals("")) {
                                    mCabDetails.setBaseFareDistance("");
                                } else {
                                    mCabDetails.setBaseFareDistance(obj.getString("base_fare_distance").toString().trim());
                                }

                                if (obj.getString("waiting_charge").toString().trim().equals("null") || obj.getString("waiting_charge").equals("")) {
                                    mCabDetails.setWaitingCharge("");
                                } else {
                                    mCabDetails.setWaitingCharge(obj.getString("waiting_charge").toString().trim());
                                }

                                //

                                if (obj.getString("additioncharge").toString().trim().equals("null") || obj.getString("additioncharge").equals("")) {
                                    mCabDetails.setAdditioncharge("");
                                } else {
                                    mCabDetails.setAdditioncharge(obj.getString("additioncharge").toString().trim());
                                }

                                if (obj.getString("telephone_dispatch").toString().trim().equals("null") || obj.getString("telephone_dispatch").equals("")) {
                                    mCabDetails.setTelephone_dispatch("");
                                } else {
                                    mCabDetails.setTelephone_dispatch(obj.getString("telephone_dispatch").toString().trim());
                                }

                                if (obj.getString("passengerSurgcharge").toString().trim().equals("null") || obj.getString("passengerSurgcharge").equals("")) {
                                    mCabDetails.setPassengerSurgcharge("");
                                } else {
                                    mCabDetails.setPassengerSurgcharge(obj.getString("passengerSurgcharge").toString().trim());
                                }

                                if (obj.getString("luggagecharge").toString().trim().equals("null") || obj.getString("luggagecharge").equals("")) {
                                    mCabDetails.setLuggagecharge("");
                                } else {
                                    mCabDetails.setLuggagecharge(obj.getString("luggagecharge").toString().trim());
                                }

                                if (obj.getString("airportSurcharge").toString().trim().equals("null") || obj.getString("airportSurcharge").equals("")) {
                                    mCabDetails.setAirportSurcharge("");
                                } else {
                                    mCabDetails.setAirportSurcharge(obj.getString("airportSurcharge").toString().trim());
                                }

                                if (obj.getString("additionHourcharge").toString().trim().equals("null") || obj.getString("additionHourcharge").equals("")) {
                                    mCabDetails.setAdditionHourcharge("");
                                } else {
                                    mCabDetails.setAdditionHourcharge(obj.getString("additionHourcharge").toString().trim());
                                }

                                if (obj.getString("snowcharge").toString().trim().equals("null") || obj.getString("snowcharge").equals("")) {
                                    mCabDetails.setSnowcharge("");
                                } else {
                                    mCabDetails.setSnowcharge(obj.getString("snowcharge").toString().trim());
                                }


                                mCabDetailsList.add(mCabDetails);
                            }


                                    if(mCabDetailsList!=null)
                                    {
                                        mbtncar.setBackgroundResource(R.mipmap.car_btn2);
                                        mbtnVan.setBackgroundResource(R.mipmap.van_btn1);
                                        mbtnhandicape.setBackgroundResource(R.mipmap.wheel_btn1);



                                        mcabId=mCabDetailsList.get(0).getmId();
                                        baseFare =mCabDetailsList.get(0).getBaseFare();
                                        baseFareDistance=mCabDetailsList.get(0).getBaseFareDistance();
                                        perKm =mCabDetailsList.get(0).getPerKm();
                                        waitingCharge =mCabDetailsList.get(0).getWaitingCharge();
                                        maxusercounter=mCabDetailsList.get(0).getMaxUsers();


                                        madditioncharge=mCabDetailsList.get(0).getAdditioncharge();
                                        mtelephone_dispatch=mCabDetailsList.get(0).getTelephone_dispatch();
                                        mpassengerSurgcharge=mCabDetailsList.get(0).getPassengerSurgcharge();
                                        mluggagecharge=mCabDetailsList.get(0).getLuggagecharge();
                                        mairportSurcharge=mCabDetailsList.get(0).getAirportSurcharge();
                                        madditionHourcharge=mCabDetailsList.get(0).getAdditionHourcharge();
                                        mperpassengercharge=mCabDetailsList.get(0).getPassengerSurgcharge();
                                        msnowcharge=mCabDetailsList.get(0).getSnowcharge();




                                        SpinerAdapterSet(maxusercounter);

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
                prgDialog.dismiss();
                Activity activity1=getActivity();
                if (activity1!=null)
                {
                    CommanMethod.showAlert(getResources().getString(R.string.connection_error),activity1);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });

    }

    public void SpinerAdapterSet(String maxusercounter)
    {

        int i = Integer.parseInt(maxusercounter);
        passangerlist.clear();
        for (int k = 1; k <= i; k++) {
            passangerlist.add(k);

        }
        if (getActivity() != null) {

            passengerAdapter = new ArrayAdapter(getContext(), R.layout.simple_spinner_item, passangerlist);
            passengerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            passengerSpinner.setAdapter(passengerAdapter);

        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean Checkvalidation() {
        if(paymenoption!=null) {
            if (paymenoption.equals("3")) {
                if (MyApplication.vipaccountchk.equals("yes")) {
                    paymenoption = "3";
                    MyApplication.payment_method_nonce="3";
                    MyApplication.vipaccountchk = "no";
                } else {
                    mpayments.setBackgroundResource(R.mipmap.btn2);
                    mcredit.setBackgroundResource(R.mipmap.btn2);
                    mvip.setBackgroundResource(R.mipmap.btn2);
                    paymenoption = null;
                }
            }
        }
        if (mtvpickuplocation.getText().toString().equals("PICKUP LOCATION")) {

            Toast.makeText(getActivity(), "Please Select pickup location",
                    Toast.LENGTH_SHORT).show();
            return false;

        }else if (mdestionlocation.getText().toString().equals("DESTINATION")) {

            Toast.makeText(getActivity(), "Please Select Destination",
                    Toast.LENGTH_SHORT).show();
            return false;

        } else if (mcabId == null) {

            Toast.makeText(getActivity(), "Please Select Taxi.",
                    Toast.LENGTH_SHORT).show();
            return false;

        } else if (paymenoption == null) {

            Toast.makeText(getActivity(), "Please Select Payment option",
                    Toast.LENGTH_SHORT).show();
            return false;

        }else if(MyApplication.payment_method_nonce.equals("")){

            Toast.makeText(getActivity(), "Please Select Payment option",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if (passenger == null) {

            Toast.makeText(getActivity(), "Please Select passenger",
                    Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;

    }


    private boolean CheckvalidationResevation() {

        if (mtvpickuplocation.getText().toString().trim().equals("PICKUP LOCATION")) {

            Toast.makeText(getActivity(), "Please Select Pickup Location.",
                    Toast.LENGTH_SHORT).show();
            return false;

        } else if (mdestionlocation.getText().toString().trim().equals("DESTINATION")) {

            Toast.makeText(getActivity(), "Please Select Destination Location.",
                    Toast.LENGTH_SHORT).show();
            return false;

        } else if (mcabId == null) {

            Toast.makeText(getActivity(), "Please Select Taxi",
                    Toast.LENGTH_SHORT).show();
            return false;

        }else if(paymenoption==null){
            Toast.makeText(getActivity(), "Please Select Payment option",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(MyApplication.payment_method_nonce.equals("")){

            Toast.makeText(getActivity(), "Please Select Payment option",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;


    }

    public void DataUpdateBackgroundService() {

        final ArrayList<LatLng> point = new ArrayList<LatLng>();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("update_map");
        sendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO  map updations work here
                freeMemory();
                System.out.println("update run time in Fragemernt class");
                point.clear();
                mDriverdetailsList = (ArrayList<Driverdetails>) intent.getSerializableExtra("backgroundArraylist");
                if (mDriverdetailsList != null) {
                    if(mDriverdetailsList.size()==0)
                    {
                        MyApplication.NOTDRIVERAVILAVLE=true;
                        if(marker!=null&&markerdriverlocation!=null&&googleMap!=null)
                        {
                            //markerdriverlocation=null;
                            //marker=null;

                        }

                    }
                    System.out.println("mDriverdetailsList info===>>>>" + mDriverdetailsList + mDriverdetailsList.size());
                    for (int j = 0; j < mDriverdetailsList.size(); j++) {

                        DriverId driverIdobject=new  DriverId();
                        driverIdobject.setId(mDriverdetailsList.get(j).getDriverId());
                        driverIds.add(driverIdobject);
                        if (mDriverdetailsList.get(j).getDriverdetailslocations() != null && mDriverdetailsList.get(j).getDriverdetailslocations().size() > 0) {
                            MyApplication.NOTDRIVERAVILAVLE=false;
                            String lat = "";
                            String lng = "";
                            driverlocation=mDriverdetailsList.get(j).getDriverdetailslocations().size();
                            System.out.println("mDriverdetailsList location===>>>>" + mDriverdetailsList.get(j).getDriverdetailslocations().size());
                            for (int i = 0; i < mDriverdetailsList.get(j).getDriverdetailslocations().size(); i++) {

                                lat = mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLatitude();
                                lng = mDriverdetailsList.get(j).getDriverdetailslocations().get(i).getLongitude();

                                point.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                                System.out.println("location>>>>>>>>>>>>>>"+"markerdata-----"+lat+"long"+lng);

                            }
                            if(driverIds!=null)
                            {
                                if (markerdriverlocation != null&&driverIds.get(j).getId().equals(mDriverdetailsList.get(j).getDriverId())) {
                                    for (int k = 0; k< point.size(); k++) {
                                        markerOptions1.position(point.get(k));

                                    }

                                }else {

                                    if(markerdriverlocation!=null)
                                    {
                                        markerdriverlocation=null;

                                    }
                                    drawMarker(point);


                                }
                            }
                        }

                    }
                }

            }
        };
        getActivity().registerReceiver(sendBroadcastReceiver, intentFilter);
    }

 private void drawMarker(ArrayList<LatLng> point) {
        // Creating an instance of MarkerOptions

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.car);
        // Setting latitude and longitude for the marker

        if (point != null) {
            for (int i = 0; i < point.size(); i++) {

                    markerdriverlocation = googleMap.addMarker(markerOptions1
                            .icon(icon)
                            .position(point.get(i))
                            .draggable(true).visible(true));

                    googleMap.addMarker(markerOptions1);
                }
            }

        }

    private void getToken(String userIdd) {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.add("userId", userIdd);
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
                            Log.d("Json_con", response.toString());
                            String responseCode = response.getString("responseCode");
                            String responseMessage = response.getString("responseMsg");

                            if (responseCode.equals("200")) {
                                if (responseMessage.equals("success")) {
                                    clientToken = response.getString("token");
                                    if(clientToken!=null)
                                    {
                                        SharedPreferences.Editor editor = mPref.edit();
                                        editor.putString("clinetTokenpayment", clientToken);
                                        editor.apply();
                                        Log.e("ClientTokenIfUnder",clientToken);
                                    }

                                    Log.e("ClientToken",clientToken);
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        prgDialog.hide();
                    }


                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.hide();
                    }


                });
    }
    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public void callBackgroundService() {
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
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000s
    }

}
