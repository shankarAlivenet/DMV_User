package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

public class MyLayoutOperation {

	public static ProgressDialog prgDialog;
	public static RequestParams params;
	public static final String MYPREF = "user_info";
	public static  SharedPreferences mPref;
	public static String mUserId,mbookingID;
	public static String splitmobileno="",splitname="";

	public static void sendinvitaions(final Activity activity, Button btn)
	{
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				splitmobileno="";
				splitname="";
				LinearLayout scrollViewlinerLayout = (LinearLayout) activity.findViewById(R.id.linearLayoutForm);
				
				ArrayList<String> array_name = new ArrayList<String>();
			 	ArrayList<String> array_mobile = new ArrayList<String>();


				if(scrollViewlinerLayout.getChildCount()< MyApplication.passngercont)
				{

				for (int i = 0; i < scrollViewlinerLayout.getChildCount(); i++)
				{
					LinearLayout innerLayout = (LinearLayout) scrollViewlinerLayout.getChildAt(i);
					EditText mname = (EditText) innerLayout.findViewById(R.id.editname);
					EditText mMobile = (EditText) innerLayout.findViewById(R.id.editMobile);

					array_name.add(mname.getText().toString());
					array_mobile.add(mMobile.getText().toString());

				}

				//Toast t = Toast.makeText(activity.getApplicationContext(), array_name.toString()+"<====>"+array_mobile.toString(), Toast.LENGTH_SHORT);
				//t.show();


				for (String s : array_mobile)
				{
					splitmobileno += s + ",";
				}
				for (String s : array_name)
				{
					splitname += s + ",";
				}

				mPref = activity.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
				mUserId = mPref.getString("userId", null);
				mbookingID=mPref.getString("bookingId", null);

                System.out.println("In split_fare userId ===>> "+mUserId);
                System.out.println("In split_fare bookingId ===>> "+mbookingID);
                System.out.println("In split_fare mobileno ===>> "+splitmobileno);
                System.out.println("In split_fare name ===>> "+splitname);


				// code will wirite here for invite frnds
				prgDialog = new ProgressDialog(activity);
				params = new RequestParams();
				prgDialog.setMessage("Please wait...");
				prgDialog.setCancelable(false);
				params.put("userId", mUserId);
				params.put("bookingId", mbookingID);
				params.put("mobileNumber", splitmobileno);
				params.put("name", splitname);


				    String url = WebserviceUrlClass.SendSplitInvite;
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
							try {
								Log.d("SendSplitInvite =", response.toString());
								String responseCode = response.getString("responseCode");
								String responseMessage = response.getString("responseMsg");
								if (responseCode.equals("200")) {


									CommanMethod.showAlert(responseMessage, activity);
									activity.finish();

								} else {
									CommanMethod.showAlert(responseMessage, activity);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}


						@Override
						public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
							prgDialog.dismiss();

							if (activity!=null)
							{
								CommanMethod.showAlert(activity.getResources().getString(R.string.connection_error),activity);
							}

						}

						@Override
						public void onFinish() {
							super.onFinish();
							prgDialog.dismiss();
						}

					});
			}else{

					CommanMethod.showAlert("You Can Invite only "+(MyApplication.passngercont-1)+" Friends ",activity);
				}
			}
		});
	}

	public static void add(final Activity activity, ImageButton btn)
	{
		final LinearLayout linearLayoutForm = (LinearLayout) activity.findViewById(R.id.linearLayoutForm);;

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final LinearLayout newView = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.split_add_frnd_data, null);

				newView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ImageButton btnRemove = (ImageButton) newView.findViewById(R.id.btnRemove);
				btnRemove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						linearLayoutForm.removeView(newView);
					}
				});

				linearLayoutForm.addView(newView);
			}
		});
	}

	public static void bydefaultFirstItemadd(final Activity activity)
	{
		final LinearLayout linearLayoutForm = (LinearLayout) activity.findViewById(R.id.linearLayoutForm);;

				final LinearLayout newView = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.split_add_frnd_data, null);

				newView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ImageButton btnRemove = (ImageButton) newView.findViewById(R.id.btnRemove);
				btnRemove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						linearLayoutForm.removeView(newView);
					}
				});

				linearLayoutForm.addView(newView);

	}


}
