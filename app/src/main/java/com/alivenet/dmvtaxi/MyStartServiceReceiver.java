package com.alivenet.dmvtaxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.fragment.FragmentMainScreen;
import com.alivenet.dmvtaxi.pojo.Driverdetails;
import com.alivenet.dmvtaxi.pojo.Driverdetailslocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;


public class MyStartServiceReceiver extends BroadcastReceiver {
	// GPSTracker class
	private ArrayList<Driverdetails> mDriverdetailsList=new ArrayList<>();
	private Context mContext;
	String userId,latitude,longitude;

	public static String mUpdateStatus;

	// TODO: mehod for  Receive data from brodcast reciver
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		userId=intent.getStringExtra("userid");
		latitude=intent.getStringExtra("lat");
		longitude=intent.getStringExtra("long");
		validateGetDriverDetails( userId,latitude,longitude);

	}
	protected void validateGetDriverDetails(String userId, String latitude, String longitude) {

		Log.e("dkljfklsdj","djfkljd");
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-Type", "application/x-www-form-urlencoded");
		RequestParams params = new RequestParams();
		params.add("id", userId);
		params.add("lat", latitude);
		params.add("long", longitude);
		client.post(WebserviceUrlClass.Driver_details, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject responseCode) {
						super.onSuccess(statusCode, headers, responseCode);
						Log.e("", "On Success of NoTs");
						Log.e("responseDriver_details", "" + responseCode.toString());
						mDriverdetailsList=parseResult(responseCode.toString());
						try{

							JSONObject object=new JSONObject(responseCode.toString());
							Log.e("responseDriver_details",":-"+object.toString(2));
						}catch (Exception e)
						{
							e.printStackTrace();
						}

						//FragmentMainScreen.mDriverdetailsList=mDriverdetailsList;


					}

					@Override
					public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}

				});

	}


	private ArrayList<Driverdetails> parseResult(String result) {

		try {
			mDriverdetailsList.clear();
			JSONObject response = new JSONObject(result);
			ArrayList<Driverdetailslocation> driverdetailslocations=new ArrayList<>();
			driverdetailslocations.clear();
			if (response.getString("responseCode").equals("200")) {
				JSONArray allDetails = response.getJSONArray("driver_list");
				if (allDetails != null) {

					for (int i = 0; i < allDetails.length(); i++) {

						JSONObject object = allDetails.getJSONObject(i);
						JSONObject object_id=object.getJSONObject("_id");
						JSONArray location = object.getJSONArray("location");
						Driverdetails allDetailObj = new Driverdetails();
						allDetailObj.setDriverId(object.getString("driverId"));
						allDetailObj.setCabType(object.getString("cabType"));
						allDetailObj.setId(object_id.getString("$id"));


						Driverdetailslocation objectloc=new Driverdetailslocation();
						objectloc.setLatitude(location.get(0).toString());
						objectloc.setLongitude(location.get(1).toString());
						driverdetailslocations.add(objectloc);
						allDetailObj.setDriverdetailslocations(driverdetailslocations);



						mDriverdetailsList.add(allDetailObj);
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mDriverdetailsList;
	}


}
