package com.alivenet.dmvtaxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.SharedPreference;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.fragment.FragmentArriveDriver;
import com.alivenet.dmvtaxi.fragment.FragmentLocation;
import com.alivenet.dmvtaxi.fragment.FragmentMainScreen;
import com.alivenet.dmvtaxi.fragment.FragmentRateYourRide;
import com.alivenet.dmvtaxi.fragment.FragmentReservation;
import com.alivenet.dmvtaxi.fragment.FragmentUpdateMyProfile;
import com.alivenet.dmvtaxi.fragment.Fragment_ride_list;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;
import com.alivenet.dmvtaxi.pojo.RideDriverComplete;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.paypal.android.sdk.ak;

import constant.MyApplication;
import constant.MyPreferences;


/**
 * Created by narendra on 6/16/2016.
 */
public class DeashboardActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener,
        BraintreeCancelListener, BraintreeErrorListener,ConfigurationListener {

    NavigationView navigationView;
    private ViewPager viewPager;
    public static Toolbar toolbar;
    public static DrawerLayout drawerLayout;
    private Menu menuNav;
    private String drawerTitle;
    TextView passengertermconditions;
    public boolean flag;
    RideDriverComplete rideDriverComplete;
    SharedPreference sharedPreference=new SharedPreference();


   //BrainTree
    static final String EXTRA_PAYMENT_REQUEST = "payment_request";
    static final String EXTRA_COLLECT_DEVICE_DATA = "collect_device_data";
    static final String EXTRA_ANDROID_PAY_CART = "android_pay_cart";

    private static final int DROP_IN_REQUEST = 100;
    private static final int PAYMENT_BUTTON_REQUEST = 200;
    private static final int CUSTOM_REQUEST = 300;
    private static final int PAYPAL_REQUEST = 400;

    private static final String KEY_NONCE = "nonce";
    private BroadcastReceiver sendBroadcastReceiver= null;
    private BroadcastReceiver sendBroadcastReceiverComplete=null;
    private BroadcastReceiver sendBroadcastdecline=null;
    private BroadcastReceiver sendBroadcastsplit=null;
    private BraintreeFragment mBraintreeFragment;
    private PaymentMethodNonce mNonce;
    private String mDeviceData;

    FragmentManager fragmentManager1;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
       /* outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);*/
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        fragmentManager1 = getSupportFragmentManager();
            System.out.println("check for onCreate "+"DeashboardActivity");
            brodcastdecline();
            brodcastsplit();
            getNotificationForRidecomplete();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        passengertermconditions = (TextView) findViewById(R.id.footer_item_2);

        menuNav = navigationView.getMenu();
        navigationView.setItemBackground(getResources().getDrawable(R.mipmap.button_bg));
        navigationView.setItemIconTintList(null);
        navigationView.setBackground(getResources().getDrawable(R.mipmap.backgroundtwo));

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        drawerTitle = "Profile";
        Bundle bundle= getIntent().getExtras();

        if(bundle!=null)
        {
            flag=bundle.getBoolean("flag");
            if(flag==true)
            {
                rideDriverComplete = (RideDriverComplete) getIntent().getSerializableExtra("ridedriverInfo");
            }
        }

        MyPreferences.getActiveInstance(DeashboardActivity.this).setflag(false);

        if (rideDriverComplete!=null){

            Bundle bundles = new Bundle();
            Fragment fragment = new FragmentRateYourRide();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment.setArguments(bundles);
            bundles.putSerializable("ridedriverInfo", rideDriverComplete);
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();
            //Log.d("ridedriverInfo",""+rideDriverComplete.getDriverNameride());
        }else if (savedInstanceState == null) {

            if (MyApplication.frequentlocationflag) {
                MyApplication.frequentlocationflag=false;

                Bundle args = new Bundle();
                Fragment fragment = FragmentLocation.newInstance(drawerTitle);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();
                Log.d("saveInstance", "saveInstance");


            } else {

                Bundle args = new Bundle();
                Fragment fragment = FragmentMainScreen.newInstance(drawerTitle);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();
                Log.d("saveInstance", "saveInstance");

            }
        }



        TextView driverterm=(TextView)findViewById(R.id.footer_item_1);
        TextView privacyterm=(TextView)findViewById(R.id.footer_item_2);

        SpannableString content = new SpannableString("Passenger Terms");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        driverterm.setText(content);

        SpannableString content1 = new SpannableString("Privacy Terms");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        privacyterm.setText(content1);

        driverterm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.termconditionsApi= WebserviceUrlClass.BaseUrl+"customertemsncondition.php";
                Intent in = new Intent(getApplication(), PassangerTermPrivacyTerm.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);

            }
        });


        privacyterm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.termconditionsApi=WebserviceUrlClass.BaseUrl+"customerpolicy.php";
                Intent in = new Intent(getApplication(), PassangerTermPrivacyTerm.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });


        if(MyApplication.radeyourrite)
        {
            MyApplication.radeyourrite=false;
            rideDriverComplete = MyApplication.rideDriverComplete;
            sharedPreference.SaveRideComplete(this, rideDriverComplete);
            Bundle bundles = new Bundle();
            Fragment fragment = new FragmentRateYourRide();

            fragment.setArguments(bundles);
            bundles.putSerializable("ridedriverInfo", rideDriverComplete);
            fragmentManager1.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        }
        if(MyApplication.declinedriver)
        {
            CommanMethod.showAlert("Ride has been cancelled by the driver"+"\n"+"BookingID: "+MyApplication.driverdeclinebookingid,DeashboardActivity.this);
            MyApplication.declinedriver=false;
            Bundle args = new Bundle();
            Fragment fragment = FragmentMainScreen.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        }

        Menu menu1 = navigationView.getMenu();
        for (int menuItemIndex = 0; menuItemIndex < menu1.size(); menuItemIndex++) {
            MenuItem menuItem = menu1.getItem(menuItemIndex);
            if (menuItem.getItemId() == R.id.nav_logout) {
                menuItem.setVisible(true);
                SpannableString s = new SpannableString("LOG OUT");
                s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                menuItem.setTitle(s);
            }

        }



        ///////////////////////for navigationView Item vissiablety gone /////////////

    }


    private void setToolbar() {

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        System.out.println("dialog Icom" + ab);
        if (ab != null) {
            Log.e("icon", "dialog Icom");
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.mipmap.nav_icon_1);
            ab.setDisplayHomeAsUpEnabled(true);

        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);


                        String title = menuItem.getTitle().toString();


                        selectItem(title);
                        return true;
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            return true;
        } else {


        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);


    }

    private void selectItem(String title) {
        System.out.println("title is " + title);

        if (title.equals("PICK ME UP")) {
            MyApplication.background=false;
            MyApplication.pickuploction=false;
            MyApplication.frequentlocationset=false;
            MyApplication.currentloctionone=true;
            MyApplication.callrefragment=true;

            Bundle args = new Bundle();
            Fragment fragment = FragmentMainScreen.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();
            MyPreferences.getActiveInstance(DeashboardActivity.this).setflag(true);

            MyApplication.droplocationslat="";
            MyApplication.droplocationslong="";
            MyApplication.pickuplocationslat="";
            MyApplication.pickuplocationslong="";
            MyApplication.google_address="";
            MyApplication.pickup_google_address="";

        } else if (title.equals("RIDES")) {
            Bundle args = new Bundle();
            Fragment fragment = Fragment_ride_list.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        }  else if (title.equals("RESERVATION")) {
            Bundle args = new Bundle();
            Fragment fragment = FragmentReservation.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        }  else if (title.equals("PAYMENT")) {

           Intent in = new Intent(getApplication(), Cardformat.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);


    } else if (title.equals("RATE YOUR RIDE")) {
            MyApplication.RateyourRide=true;
            Bundle args = new Bundle();
            Fragment fragment = FragmentRateYourRide.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();



        } else if (title.equals("ARRIVE DRIVER")) {
            Fragment fragment = new FragmentArriveDriver();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        } else if (title.equals("MY ACCOUNT")) {

            Bundle args = new Bundle();
            Fragment fragment = FragmentUpdateMyProfile.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        } else if (title.equals("LOCATIONS")) {

            Bundle args = new Bundle();
            Fragment fragment = FragmentLocation.newInstance(drawerTitle);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();


        } else  if (title.equals("LOG OUT")) {

          new LogOutDialog(DeashboardActivity.this,"hello").show();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    protected void onResume() {
       if(MyApplication.conformbooking==true)
        {
            MyApplication.conformbooking=false;
            Fragment fragment = new FragmentArriveDriver();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

        }
        super.onResume();

        if(MyApplication.notificationconfiride==true)
        {
            Menu menu = navigationView.getMenu();
            for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
                MenuItem menuItem = menu.getItem(menuItemIndex);
                if (menuItem.getItemId() == R.id.nav_arrive_driver) {
                    menuItem.setVisible(true);
                }

            }
        }


    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    public void onBackPressed() {


        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onConfigurationFetched(Configuration configuration) {
        if (getIntent().getBooleanExtra(EXTRA_COLLECT_DEVICE_DATA, false)) {
            mDeviceData = DataCollector.collectDeviceData(mBraintreeFragment);
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        Log.e("PaypalNonce===",""+paymentMethodNonce);
        MyApplication.payment_method_nonce = paymentMethodNonce.getNonce();

        Intent intent = new Intent()
                .putExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE, paymentMethodNonce)
                .putExtra(BraintreePaymentActivity.EXTRA_DEVICE_DATA, mDeviceData);
        setResult(RESULT_OK, intent);
        finish();

    }



    public void getNotificationForRidecomplete() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("DriverNotificationcomplete");
        sendBroadcastReceiverComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                    //TODO  map updations work here

                    rideDriverComplete = (RideDriverComplete) intent.getSerializableExtra("ridedriverInfo");
                    sharedPreference.SaveRideComplete(context, rideDriverComplete);
                    Bundle bundles = new Bundle();
                    Fragment fragment = new FragmentRateYourRide();

                    fragment.setArguments(bundles);
                    bundles.putSerializable("ridedriverInfo", rideDriverComplete);
                    fragmentManager1.beginTransaction().replace(R.id.fragment_switch, fragment).commit();


                if(MyApplication.notificationconfiride==false)
                {
                    Menu menu = navigationView.getMenu();
                    for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
                        MenuItem menuItem = menu.getItem(menuItemIndex);
                        if (menuItem.getItemId() == R.id.nav_arrive_driver) {
                            menuItem.setVisible(false);
                        }

                    }
                    if(sendBroadcastReceiverComplete==null)
                    {

                    }else{
                        unregisterReceiver(sendBroadcastReceiverComplete);
                        sendBroadcastReceiverComplete=null;
                    }
                }


            }
        };
        registerReceiver(sendBroadcastReceiverComplete, intentFilter);


    }

    public void brodcastdecline() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Driverdecline");
        sendBroadcastdecline = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //TODO  map updations work here
                CommanMethod.showAlert("Ride has been cancelled by the driver"+"\n"+"BookingID: "+MyApplication.driverdeclinebookingid,DeashboardActivity.this);
                Fragment fragment = new FragmentMainScreen();
                fragmentManager1.beginTransaction().replace(R.id.fragment_switch, fragment).commit();

                if(MyApplication.notificationconfiride==false)
                {
                    Menu menu = navigationView.getMenu();
                    for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
                        MenuItem menuItem = menu.getItem(menuItemIndex);
                        if (menuItem.getItemId() == R.id.nav_arrive_driver) {
                            menuItem.setVisible(false);
                        }

                    }
                    if(sendBroadcastdecline==null)
                    {

                    }else{
                        unregisterReceiver(sendBroadcastdecline);
                        sendBroadcastdecline=null;
                    }
                }


            }
        };
        registerReceiver(sendBroadcastdecline, intentFilter);


    }


    // brodcast for splite ride

    public void brodcastsplit() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("splitrideaccept");
        sendBroadcastsplit = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



                if(MyApplication.splitride)
                {
                    MyApplication.splitride=false;

                    Intent in = new Intent(getApplication(), AcceptNotificationUIwork.class);
                    startActivity(in);

                    if(sendBroadcastsplit==null)
                    {

                    }else{
                        unregisterReceiver(sendBroadcastsplit);
                        sendBroadcastsplit=null;
                    }
                }


            }
        };
        registerReceiver(sendBroadcastsplit, intentFilter);


    }



}
