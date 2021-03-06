package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.LoginActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 7/1/2016.
 */
public class ChangeCellNo extends Dialog {
    Context activity;
    String userId;
    Button btnokmob;
    EditText editText;
    ProgressDialog prgDialog;
    SharedPreferences mPref;

    public ChangeCellNo (Context a, String msg) {
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
        setContentView(R.layout.changemobileno_dialog);
        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);


        btnokmob = (Button) findViewById(R.id.btnokmob_dialog);
        editText = (EditText) findViewById(R.id.et_mobileno);

        btnokmob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String code = editText.getText().toString().trim();
                if (CommanMethod.isOnline(getContext())) {
                    if (checkValidationJoinUs()) {
                        RequestParams params = new RequestParams();
                        params.put("mobile", editText.getText().toString().trim());
                        params.put("id", userId);
                        sendMobileNoWS(params);

                    }
                }
            }
        });

    }


    public void sendMobileNoWS(RequestParams params) {

        String url = WebserviceUrlClass.sendmobileno;
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

                        if (responseMessage.equals("success")) {
                            dismiss();
                       //     new LetsGoDialog(getContext(), "hello").show();
                            new NotVerifiedUserDialog(getContext(), userId).show();

                        }
                    } else {
                        //   new IncorrectPasswordDialog(VerificationActivity.this, "hello").show();
                        Toast.makeText(getContext(), "" + responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(getContext(),"Please try again", Toast.LENGTH_LONG).show();
                prgDialog.dismiss();
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
