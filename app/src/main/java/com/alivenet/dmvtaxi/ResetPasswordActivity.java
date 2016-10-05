package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 8/8/2016.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    EditText etcode, etnewPassword, etConfirmPassword;
    Button btnSubmitPassword;
    Toolbar toolbar;
    String userID;
    ProgressDialog prgDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

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

        userID = getIntent().getStringExtra("userId");
        etcode = (EditText) findViewById(R.id.et_resetcode);
        etnewPassword = (EditText) findViewById(R.id.et_newpassword);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirmpassword);
        btnSubmitPassword = (Button) findViewById(R.id.btnsubmit_newpassword);
        btnSubmitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommanMethod.isOnline(ResetPasswordActivity.this)) {
                    if (checkValidationJoinUs()) {

                        RequestParams params = new RequestParams();
                        params.put("userId", userID);
                        params.put("rCode", etcode.getText().toString().trim());
                        params.put("newPassword", etnewPassword.getText().toString().trim());
                        resetPasswordWS(params);
                    }
                }
            }
        });

    }


    public void resetPasswordWS(RequestParams params) {

        String url = WebserviceUrlClass.ResetPassword;
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");
                    if (responseCode.equals("200")) {

                        MyAlert("Your password has been updated successfully", ResetPasswordActivity.this);

                    } else {
                        CommanMethod.showAlert(responseMessage, ResetPasswordActivity.this);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                prgDialog.dismiss();

                CommanMethod.showAlert(getResources().getString(R.string.connection_error),ResetPasswordActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

    public void MyAlert(String message, Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent in = new Intent(getApplication(), LoginActivity.class);
                        startActivity(in);
                    }
                });
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean checkValidationJoinUs() {

        if (etcode.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your code", ResetPasswordActivity.this);
            return false;
        } else if (etnewPassword.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your new password", ResetPasswordActivity.this);
            return false;
        } else if (etConfirmPassword.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your confirm password", ResetPasswordActivity.this);
            return false;
        } else if (!etnewPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
            CommanMethod.showAlert("password not matched", ResetPasswordActivity.this);
            return false;
        }
        return true;

    }


}
