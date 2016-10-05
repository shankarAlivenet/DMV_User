package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.IncorrectPasswordDialog;
import com.alivenet.dmvtaxi.dialog.NotVerifiedUserDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 8/7/2016.
 */
public class ForgotPassword extends AppCompatActivity {

    EditText etEmaiId;
    Button btnSubmit;
    ProgressDialog prgDialog;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        etEmaiId = (EditText) findViewById(R.id.etforgot_emailid);
        btnSubmit = (Button) findViewById(R.id.btnsubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommanMethod.isOnline(ForgotPassword.this)) {
                    if (checkValidationJoinUs()) {
                        String email = etEmaiId.getText().toString().trim();
                        RequestParams params = new RequestParams();
                        params.put("email_id", email);
                        forgotPasswordWS(params);
                    }
                }
            }
        });
    }


    public void forgotPasswordWS(RequestParams params) {

        String url = WebserviceUrlClass.ForgotPassword;
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

                        String userId = response.getString("userId");

                        MyAlert(responseMessage, userId, ForgotPassword.this);

                    } else {
                        CommanMethod.showAlert(responseMessage, ForgotPassword.this);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),ForgotPassword.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }


    private boolean checkValidationJoinUs() {

        if (etEmaiId.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your email", ForgotPassword.this);
            return false;
        } else if (!CommanMethod.isEmailValid(etEmaiId.getText().toString().trim())) {
            CommanMethod.showAlert("Please enter valid email", ForgotPassword.this);
            return false;
        }
        return true;

    }

    public void MyAlert(String message, final String userId, Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent in = new Intent(getApplication(), ResetPasswordActivity.class);
                        in.putExtra("userId", userId);
                        startActivity(in);
                    }
                });
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
