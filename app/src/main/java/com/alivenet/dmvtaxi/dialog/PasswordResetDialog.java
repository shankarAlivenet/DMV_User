package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.ResetPasswordActivity;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by navin on 8/10/2016.
 */
public class PasswordResetDialog extends Dialog {
    Activity activity;
    String noofpeople;
    Button btn_ok ;
    ProgressDialog prgDialog;
    EditText resetemail;
    public PasswordResetDialog(Activity a, String msg) {
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
        setContentView(R.layout.password_reset_dialog);
        btn_ok = (Button) findViewById(R.id.btnok_resetpassword);
        resetemail = (EditText) findViewById(R.id.text_registersuccess);


        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                String email = resetemail.getText().toString().trim();
                if(checkValidationJoinUs()) {
                    RequestParams params = new RequestParams();
                    params.put("email_id", email);
                    forgotPasswordWS(params);
                }

            }
        });


    }

    private boolean checkValidationJoinUs() {
        if (resetemail.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter Email",activity);
            return false;
        } else if (!CommanMethod.isEmailValid(resetemail.getText().toString().trim())) {
            CommanMethod.showAlert("Please enter valid Email", activity);
            return false;
        }
        return true;
    }
    public void forgotPasswordWS(RequestParams params) {
        prgDialog = new ProgressDialog(activity);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        String url = WebserviceUrlClass.ForgotPassword;
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

                        String userId = response.getString("userId");

                        MyAlert(responseMessage, userId, activity);

                    } else {
                        CommanMethod.showAlert(responseMessage, activity);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                prgDialog.dismiss();
                CommanMethod.showAlert(activity.getString(R.string.connection_error),activity);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }
    public void MyAlert(String message, final String userId, Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        Intent in = new Intent(activity, ResetPasswordActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.putExtra("userId", userId);
                        activity.startActivity(in);
                    }
                });
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
