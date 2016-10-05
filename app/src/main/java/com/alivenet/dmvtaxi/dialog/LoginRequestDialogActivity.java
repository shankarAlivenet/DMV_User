package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.LoginActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.VerificationActivity;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EventObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/15/2016.
 */
public class LoginRequestDialogActivity extends Dialog {
    Context activity;
    String userId;
    Button btnok;
    EditText editText;
    ProgressDialog prgDialog;
    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    public LoginRequestDialogActivity(Context a, String msg) {
        super(a, R.style.custom_dialog_theme);
        // TODO Auto-generated constructor stub
        activity = a;
        userId = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_request_dialog);
        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        mPref = activity.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        btnok = (Button) findViewById(R.id.btnok_dialog);
        editText = (EditText) findViewById(R.id.etlogin_request);
        // people_size.setText(noofpeople);
        btnok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dismiss dialog box
                String code = editText.getText().toString().trim();
                if (CommanMethod.isOnline(getContext())) {
                    if (checkValidationJoinUs()) {
                        RequestParams params = new RequestParams();
                        params.put("code", editText.getText().toString().trim());
                        params.put("id", userId);
                        verifiCodeWS(params);
                        Log.e("id_code", "code -" + code + ", id-" + userId);
                        //   dismiss();
                    }
                }
            }
        });

    }


    public void verifiCodeWS(RequestParams params) {

        String url = WebserviceUrlClass.verifiCode;
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
                    Log.d("Json_concodeverficatin", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        if (responseMessage.equals("success")) {
                            dismiss();
                            new LetsGoDialog(getContext(), "hello").show();
                            SharedPreferences.Editor editor = mPref.edit();
                            editor.putBoolean("loginflag", true);
                            editor.apply();
                        }
                    } else {
                        Toast.makeText(getContext(), "" + responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.dismiss();
                CommanMethod.showAlert(activity.getResources().getString(R.string.connection_error),activity);

            }
            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

    private boolean checkValidationJoinUs() {

        if (editText.getText().toString().trim().length() == 0) {
            //   CommanMethod.showAlertt("Please enter code", getContext());
            Toast.makeText(getContext(), "Please enter verification code", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
