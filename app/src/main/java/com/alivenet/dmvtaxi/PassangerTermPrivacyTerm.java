package com.alivenet.dmvtaxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.IncorrectPasswordDialog;
import com.alivenet.dmvtaxi.dialog.NotVerifiedUserDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlRemoteImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

public class PassangerTermPrivacyTerm extends AppCompatActivity {
    ProgressDialog prgDialog;
    HtmlTextView htmlTextView;
    Toolbar toolbar;
    String driveragreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger_term_privacy_term);

       htmlTextView = (HtmlTextView)findViewById(R.id.html_text);


        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        RequestParams params = new RequestParams();
        params.put("", "");
        passngertermprivacy(params);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                PassangerTermPrivacyTerm.this.finish();

                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void passngertermprivacy(RequestParams params) {

        String url = MyApplication.termconditionsApi;
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("passger trrm response ", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if(responseMessage.equals("success"))
                    {
                        if(MyApplication.termconditionsApi.equals(WebserviceUrlClass.BaseUrl+"customertemsncondition.php"))
                        {

                            driveragreement = response.getString("termsCondition");
                        }
                        else{
                             driveragreement = response.getString("customerPolicy");
                        }



                        htmlTextView.setHtml(driveragreement, new HtmlRemoteImageGetter(htmlTextView));

                    }else{
                        CommanMethod.showAlert(responseMessage,PassangerTermPrivacyTerm.this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                prgDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),PassangerTermPrivacyTerm.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }

}
