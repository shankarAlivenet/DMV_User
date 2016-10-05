package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.LocationTracker;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.LoginRequestDialogActivity;
import com.alivenet.dmvtaxi.dialog.RegisteredDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import constant.MyApplication;
import constant.MyPreferences;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/16/2016.
 */
public class VerificationActivity extends AppCompatActivity {

    Button btnaccept, btndecline;
    Toolbar toolbar;
    ProgressDialog prgDialog;
    String roleID;
    LocationTracker gps;
    String regFirstName, regLastName, regEmailId, regPassword, regCountryCode, regMobileNo, regNewsLater, regCardholderName, regCardNo, regCvcNo, regCardDate, strLat, strLong;
    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veri_fication);




            toolbar = (Toolbar) findViewById(R.id.maintoolbar);
            // toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.nav_icon_1));
            toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            mPref = VerificationActivity.this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
            getLatLong();
            Intent in = getIntent();


            roleID = in.getExtras().getString("roleId");
            regFirstName = in.getStringExtra("regfirstname");
            regLastName = in.getStringExtra("reglastname");
            regEmailId = in.getStringExtra("regemailaddress");
            regPassword = in.getStringExtra("regpassword");
            regMobileNo = in.getStringExtra("regmobileno");
            regNewsLater = in.getStringExtra("regnewsLater");
            regCountryCode = in.getStringExtra("countryCode");
            regCountryCode = regCountryCode.substring(1);
            Log.e("fgghfghf", "hghg" + regCountryCode);
            //  regCardholderName = in.getStringExtra("regcardholdername");
            regCardNo = in.getStringExtra("cardNo");
            regCvcNo = in.getStringExtra("cvvNo");
            regCardDate = in.getStringExtra("cardExpdate");
            token = mPref.getString("token", "");
            System.out.println("database value " + roleID + "," + regFirstName + "," + regLastName + "," + regEmailId + "," + regPassword + "," + regMobileNo + "," + regNewsLater
                    + "\n," + regCardholderName + "," + regCardNo + "," + regCvcNo + "," + regCardDate);


            btnaccept = (Button) findViewById(R.id.btn_accept);
            btndecline = (Button) findViewById(R.id.btn_decline);

            btnaccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestParams params = new RequestParams();
                    // new FrequentLocDialog(VerificationActivity.this ,"hello").show();
                    if (CommanMethod.isOnline(VerificationActivity.this)) {
                        params.put("role_id", roleID);
                        params.put("first_name", regFirstName);
                        params.put("last_name", regLastName);
                        params.put("email", regEmailId);

                        String result = regMobileNo.replaceAll("[-+.^:,]", "");
                        System.out.println("regMobileNo" + result);
                        params.put("mobile_no", result);
                        params.put("country_code", regCountryCode);
                        params.put("password", regPassword);
                        //  params.put("name_on_card", regCardholderName);
                        params.put("card_no", regCardNo);
                        params.put("cvv", regCvcNo);
                        params.put("expiration_date", regCardDate);
                        params.put("newsletter", regNewsLater);
                        params.put("lat", strLat);
                        params.put("long", strLong);
                        params.put("fcmid", token);
                        params.put("deviceType", "android");
                        registationWs(params);

                    }
                }
            });

            btndecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(VerificationActivity.this, "You cannot register unless you accept the Terms of Use for this application.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }



    public void registationWs(RequestParams params) {

        String url = WebserviceUrlClass.Registation;
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
                prgDialog.hide();
                try {
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {


                            String userid = response.getString("userId");
                            new RegisteredDialog(VerificationActivity.this, userid).show();
                            SharedPreferences.Editor editor = mPref.edit();
                            editor.putString("userId", userid);
                            //editor.putBoolean("loginflag", true);
                            if (roleID.equals("2")) {
                                editor.putString("userType", "Normal");
                            } else if (roleID.equals("3")) {
                                editor.putString("userType", "Vip");
                            }
                            editor.apply();

                        MyPreferences.getActiveInstance(VerificationActivity.this).setCardno("");
                        MyPreferences.getActiveInstance(VerificationActivity.this).setCvvno("");
                        MyPreferences.getActiveInstance(VerificationActivity.this).setExpyear("");
                        MyPreferences.getActiveInstance(VerificationActivity.this).setExpmonth("");
                    } else {
                        Toast.makeText(getApplicationContext(), "" + responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.dismiss();

                    CommanMethod.showAlert(getResources().getString(R.string.connection_error),VerificationActivity.this);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

    private void getLatLong() {

        gps = new LocationTracker(VerificationActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.e("registation_latlong", "lat " + latitude + ", long " + longitude);

            strLat = Double.toString(latitude);
            strLong = Double.toString(longitude);
        } else {
            gps.showSettingsAlert();
        }
    }



}
