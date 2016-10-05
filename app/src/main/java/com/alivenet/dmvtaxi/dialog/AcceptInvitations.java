package com.alivenet.dmvtaxi.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.alivenet.dmvtaxi.AcceptNotificationUIwork;
import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

/**
 * Created by navin on 8/11/2016.
 */
public class AcceptInvitations extends Dialog {
    Context activity;
   Button macceptfrndrequest,mcancellfrndrequest;String Amsg,AbookingId,ApUser,AsUser;

    public static ProgressDialog prgDialog;
    public static RequestParams params;


    public AcceptInvitations(Context a, String msg, String bookingfId, String puser, String Auser ) {
        super(a, R.style.custom_dialog_theme);
        // TODO Auto-generated constructor stub
        activity = a;
        Amsg = msg;
        AbookingId=bookingfId;
        ApUser=puser;
        AsUser=Auser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.accept_frnd_inviations);
        macceptfrndrequest=(Button)findViewById(R.id.acceptfrndrequest);
        mcancellfrndrequest=(Button)findViewById(R.id.cancellfrndrequest);

        macceptfrndrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.manageAcceptnoti=true;
                dismiss();
                Intent in = new Intent(activity, AcceptNotificationUIwork.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(in);
                dismiss();
            }
        });


        mcancellfrndrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                prgDialog = new ProgressDialog(activity);
                params = new RequestParams();
                prgDialog.setMessage("Please wait...");
                prgDialog.setCancelable(false);

                System.out.println("In split_fare bookingId ===>> "+AbookingId);

                System.out.println("In split_fare pUser ===>> "+ApUser);
                System.out.println("In split_fare status ===>> "+AsUser);
                params.put("bookingId", AbookingId);
                params.put("nounce","");
                params.put("pUser", ApUser);
                params.put("sUser", AsUser);
                params.put("status","9" );
                callbackacceptnotificationtoserver(params);

            }
        });


    }
    public void callbackacceptnotificationtoserver(RequestParams params) {
        String url = WebserviceUrlClass.SendSplitRide;
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("SendSplitInvite =", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");
                    if (responseCode.equals("200")) {
                        MyApplication.manageAcceptnoti=false;
                        Intent in = new Intent(activity, DeashboardActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(in);
                        dismiss();

                        CommanMethod.showAlert(responseMessage, activity);

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
                // CommanMethod.showAlert(activity.getString(R.string.connection_error), activity);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismiss();
    }
}
