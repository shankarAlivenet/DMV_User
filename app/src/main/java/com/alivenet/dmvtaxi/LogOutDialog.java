package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/17/2016.
 */
public class LogOutDialog  extends Dialog {

    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    Activity activity;
    String noofpeople,mUserId;
    Button btnlogout_Yes , btnlogout_No;
    ProgressDialog prgDialog;

    public LogOutDialog(Activity a, String msg) {
        super(a, R.style.custom_dialog_theme);
        // TODO Auto-generated constructor stub
        activity = a;
        noofpeople = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logout_dialog);
        mPref = getContext().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        prgDialog = new ProgressDialog(activity);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        btnlogout_Yes = (Button) findViewById(R.id.btnlogout_yes);
        btnlogout_No = (Button) findViewById(R.id.btnlogout_no);
        // people_size.setText(noofpeople);
        btnlogout_No.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dismiss dialog box
                dismiss();
            }
        });

        btnlogout_Yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dismiss dialog box
                ValidateLogout();


            }
        });

    }


    private void ValidateLogout()
    {
        mPref = activity.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);
        AsyncHttpClient client=new AsyncHttpClient();
        client.addHeader("Content-Type","application/x-www-from-urlencoded");
        RequestParams params=new RequestParams();
        params.add("userId",mUserId);
        client.post(WebserviceUrlClass.logout,params,
                new JsonHttpResponseHandler(){

                    @Override
                    public void onStart() {
                        super.onStart();
                        prgDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseCode) {
                        super.onSuccess(statusCode, headers, responseCode);
                        prgDialog.hide();
                        try{
                            JSONObject object=new JSONObject(responseCode.toString());
                            Log.e("response_logout",":"+object.toString(2));

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putBoolean("loginflag", false);
                        editor.putString("clinetTokenpayment", "");
                        editor.apply();
                        Intent homeIntent = new Intent(getContext(), LoginActivity.class);
                        ComponentName cn = homeIntent.getComponent();
                        Intent mainIntent = Intent
                                .makeRestartActivityTask(cn);
                        getContext().startActivity(mainIntent);
                        dismiss();

                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        prgDialog.hide();
                        CommanMethod.showAlert(activity.getResources().getString(R.string.connection_error),activity);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.hide();

                    }
                });
    }

}