package com.alivenet.dmvtaxi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.AcceptInvitations;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.PayPalOverrides;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PaymentMethodNonce;
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
public class AcceptNotificationUIwork extends AppCompatActivity {

    public static ProgressDialog prgDialog;
    public static RequestParams params;

    public static RadioGroup radioGroup;
    public static String mUserId, userType;
    public static String payment_method_nonce;
    private static String clientToken;
    public static BraintreeFragment mBraintreeFragment;
    public static SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    String Amsg,AbookingId,ApUser,AsUser;
    LinearLayout selectpayment;
    Button btn_accept,btn_decline;
    public static boolean chkpaymentflag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectpaymentmethod_for_split_fare);
        Amsg= MyApplication. notimsg;
        AbookingId= MyApplication.notibookingId;
        ApUser= MyApplication.notipUser;
        AsUser= MyApplication.notisUser;


        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        selectpayment = (LinearLayout) findViewById(R.id.selectpayment);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_decline = (Button) findViewById(R.id.btn_decline);

        if(MyApplication.manageAcceptnoti)
        {
            MyApplication.manageAcceptnoti=false;
            selectpayment.setVisibility(View.VISIBLE);
            getToken(mUserId);
        }else{
            selectpayment.setVisibility(View.GONE);


            new AcceptInvitations(AcceptNotificationUIwork.this,Amsg,AbookingId,ApUser,AsUser ).show();

        }



        mPref = AcceptNotificationUIwork.this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);
        userType = mPref.getString("userType", null);
        mUserId = mPref.getString("userId", null);



        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(chkpaymentflag) {
                    prgDialog = new ProgressDialog(AcceptNotificationUIwork.this);
                    params = new RequestParams();
                    prgDialog.setMessage("Please wait...");
                    prgDialog.setCancelable(false);

                    System.out.println("In split_fare bookingId ===>> " + AbookingId);
                    System.out.println("In split_fare nounce ===>> " + payment_method_nonce);
                    System.out.println("In split_fare pUser ===>> " + ApUser);
                    System.out.println("In split_fare sUser ===>> " + AsUser);
                    params.put("bookingId", AbookingId);
                    params.put("nounce", payment_method_nonce);
                    params.put("pUser", ApUser);
                    params.put("sUser", AsUser);
                    params.put("status", "3");
                    callbackacceptnotificationtoserver(params);
                }else{
                    CommanMethod.showAlert("Please Select Payment Method ",AcceptNotificationUIwork.this);
                }
            }
        });
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prgDialog = new ProgressDialog(AcceptNotificationUIwork.this);
                params = new RequestParams();
                prgDialog.setMessage("Please wait...");
                prgDialog.setCancelable(false);

                System.out.println("In split_fare bookingId ===>> "+AbookingId);
                System.out.println("In split_fare nounce ===>> "+payment_method_nonce);
                System.out.println("In split_fare pUser ===>> "+ApUser);
                System.out.println("In split_fare status ===>> "+AsUser);

                params.put("bookingId", AbookingId);
                params.put("nounce", payment_method_nonce);
                params.put("pUser", ApUser);
                params.put("sUser", AsUser);
                params.put("status","2" );

                callbackacceptnotificationtoserver(params);

            }
        });




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.paypal) {
                    chkpaymentflag=true;
                    try {
                        if(clientToken!=null) {
                            mBraintreeFragment = BraintreeFragment.newInstance(AcceptNotificationUIwork.this, clientToken);
                            PayPalOverrides.setFuturePaymentsOverride(true);
                            PayPal.authorizeAccount(mBraintreeFragment);
                        }
                    } catch (InvalidArgumentException e) {
                        // There was an issue with your authorization string.
                    }


                } else if (checkedId == R.id.credit_card) {



                    if(clientToken!=null) {
                        chkpaymentflag=true;
                        PaymentRequest paymentRequest;
                        paymentRequest = new PaymentRequest()
                                .clientToken(clientToken)
                                // .amount("$10.00")
                                .primaryDescription("Ride payment")
                                .secondaryDescription("DMV Taxi")
                                .submitButtonText("Submit");
                        Log.e("clinetTokenElse", "" + clientToken);

                        startActivityForResult(paymentRequest.getIntent(AcceptNotificationUIwork.this), 1);
                    }

                } else {
                    chkpaymentflag=true;
                }
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("payment_method_nonce", " hello narendra  ");






        if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
            Log.e("payment_method_nonce", "  " + paymentMethodNonce.getNonce());

            payment_method_nonce = paymentMethodNonce.getNonce();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


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

                        CommanMethod.showAlert(responseMessage, AcceptNotificationUIwork.this);
                        MyApplication.manageAcceptnoti=false;
                        AcceptNotificationUIwork.this.finish();


                    } else {
                        CommanMethod.showAlert(responseMessage, AcceptNotificationUIwork.this);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                prgDialog.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Message ", "" + "control in Accept noti");
            //do other stuff here
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mMessageReceiver, new IntentFilter("AcceptNotificationUIwork"));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }
    public void getToken(String userIdd) {

        prgDialog = new ProgressDialog(AcceptNotificationUIwork.this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.add("userId", userIdd);
        client.post(WebserviceUrlClass.GetToken, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        prgDialog.show();
                    }


                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        prgDialog.hide();
                        Log.e("", "On Success of NoTs");
                        try {
                            Log.d("Json_con", response.toString());
                            String responseCode = response.getString("responseCode");
                            String responseMessage = response.getString("responseMsg");

                            if (responseCode.equals("200")) {
                                if (responseMessage.equals("success")) {
                                    clientToken = response.getString("token");
                                    if (clientToken != null) {
                                        Log.e("ClientTokenIfUnder", clientToken);
                                    }

                                    Log.e("ClientToken", clientToken);
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        prgDialog.hide();
                    }


                    @Override
                    public void onFinish() {
                        super.onFinish();
                        prgDialog.hide();
                    }


                });
    }
}