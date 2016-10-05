package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alivenet.dmvtaxi.FcmUtil.QuickstartPreferences;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.LocationTracker;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.IncorrectPasswordDialog;
import com.alivenet.dmvtaxi.dialog.NotVerifiedUserDialog;
import com.alivenet.dmvtaxi.dialog.PasswordResetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import constant.MyApplication;
import constant.MyPreferences;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/16/2016.
 */
public class LoginActivity extends AppCompatActivity {

    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    Toolbar toolbar;
    TextView txtCreateAccount,txtForgotPassword;
    Button btnlogin;
    EditText metEmail, metPassword;
    ProgressDialog prgDialog;
    String EmailId, Password, rememberEmail, rememberPassword, checkBoxvalue, strLat, strLong;
    CheckBox rememberEmailPassword;
    LocationTracker gps;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private static final String AUTH_KEY = "key=YOUR KEY SERVER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            toolbar = (Toolbar) findViewById(R.id.maintoolbar);
            setSupportActionBar(toolbar);

            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);


            metEmail = (EditText) findViewById(R.id.et_emailid);
            metPassword = (EditText) findViewById(R.id.et_passwrod);
            mPref = LoginActivity.this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

            rememberEmail = mPref.getString("rememberEmail", "");
            rememberPassword = mPref.getString("rememberPassword", "");
            checkBoxvalue = mPref.getString("checkBoxvalue", "");

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_CODE_LOCATION);
            }


            getLatLong();

            txtCreateAccount = (TextView) findViewById(R.id.txtcreate_account);
            txtForgotPassword = (TextView) findViewById(R.id.txtforgot_password);
            btnlogin = (Button) findViewById(R.id.btnlogin);
            rememberEmailPassword = (CheckBox) findViewById(R.id.cb_remember);
            metEmail.setText(rememberEmail);
            metPassword.setText(rememberPassword);
            if (checkBoxvalue.equals("true")) {
                rememberEmailPassword.setChecked(true);
            }

            txtCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplication(), WelcomeRegistation.class);
                    startActivity(in);
                }
            });

            txtForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplication(), ForgotPassword.class);
                    startActivity(in);
                }
            });


            btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommanMethod.isOnline(LoginActivity.this)) {
                        if (checkValidationJoinUs()) {

                            String token = mPref.getString("token", "");
                            Log.e("fcmid:", "" + token);
                            String email = metEmail.getText().toString().trim();
                            String password = metPassword.getText().toString().trim();
                            RequestParams params = new RequestParams();
                            params.put("email", email);
                            params.put("password", password);
                            params.put("lat", strLat);
                            params.put("long", strLong);
                            params.put("fcmid", token);
                            params.put("deviceType", "android");
                            loginWs(params);

                        }
                    }
                }
            });

        }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void loginWs(RequestParams params) {

        String url = WebserviceUrlClass.login;
        prgDialog.show();
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
                    JSONObject object=new JSONObject(response.toString());
                    Log.d("Login_response", object.toString(2));
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");


                    if(responseCode.equals("0"))
                    {
                        String invalid_attemp_today = response.optString("invalid_attemp_today");

                        if(invalid_attemp_today.equals("3"))
                        {
                            new PasswordResetDialog(LoginActivity.this, "hello").show();
                        }

                    }

                    if (responseCode.equals("200")) {

                            if (responseMessage.equals("success")) {
                            String userid = response.getString("userId");
                            String role = response.getString("roleId");

                            SharedPreferences.Editor editor = mPref.edit();
                            editor.putString("userId", userid);
                            editor.putBoolean("loginflag", true);

                            if (role.equals("2")) {
                                editor.putString("userType", "Normal");
                            } else if (role.equals("3")) {
                                editor.putString("userType", "Vip");
                            }
                            editor.apply();
                            MyApplication.radeyourrite=false;
                            Intent in = new Intent(getApplication(), DeashboardActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                        } else if (responseMessage.equals("user is not verified")) {
                            String userid = response.getString("userId");
                            String role = response.getString("roleId");
                            SharedPreferences.Editor editor = mPref.edit();
                            editor.putString("userId", userid);
                            //editor.putBoolean("loginflag", true);
                            if (role.equals("2")) {
                                editor.putString("userType", "Normal");
                            } else if (role.equals("3")) {
                                editor.putString("userType", "Vip");
                            }
                            editor.apply();
                            new NotVerifiedUserDialog(LoginActivity.this, userid).show();
                        }

                    } else {
                        new IncorrectPasswordDialog(LoginActivity.this, "hello").show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.dismiss();
                    CommanMethod.showAlert(getResources().getString(R.string.connection_error),LoginActivity.this);

            }
            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

    private boolean checkValidationJoinUs() {


        if (metEmail.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter Email", LoginActivity.this);
            return false;
        } else if (!CommanMethod.isEmailValid(metEmail.getText().toString().trim())) {
            CommanMethod.showAlert("Please enter valid Email", LoginActivity.this);
            return false;
        } else if (metPassword.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter Password", LoginActivity.this);
            return false;
        }

        EmailId = metEmail.getText().toString().trim();
        Password = metPassword.getText().toString().trim();
        if (rememberEmailPassword.isChecked()) {
            SharedPreferences spref = getSharedPreferences(MYPREF, MODE_PRIVATE);
            SharedPreferences.Editor edit = spref.edit();
            edit.putString("rememberEmail", EmailId);
            edit.putString("rememberPassword", Password);
            edit.putString("checkBoxvalue", "true");
            edit.apply();

        } else {
            SharedPreferences spref = getSharedPreferences(MYPREF, MODE_PRIVATE);
            SharedPreferences.Editor edit = spref.edit();
            edit.putString("rememberEmail", "");
            edit.putString("rememberPassword", "");
            edit.putString("checkBoxvalue", "false");
            edit.apply();
        }


        return true;
    }

    private void getLatLong() {
        gps = new LocationTracker(LoginActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.e("login_latlong", "lat " + latitude + ", long " + longitude);

            strLat = Double.toString(latitude);
            strLong = Double.toString(longitude);
        } else {
            gps.showSettingsAlert();
        }
    }


}
