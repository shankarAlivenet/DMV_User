package com.alivenet.dmvtaxi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.pojo.Driverdetails;
import com.alivenet.dmvtaxi.pojo.Driverdetailslocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by codecube on 14/6/16.
 */
public class BackgroundService extends Service {
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private ArrayList<Driverdetails> mDriverdetailsList=new ArrayList<Driverdetails>();
    ArrayList<Driverdetailslocation> driverdetailslocations=new ArrayList<>();
    String userId,latitude,longitude;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        this.context = this;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null)
        {
            userId=intent.getStringExtra("userid");
            latitude=intent.getStringExtra("lat");
            longitude=intent.getStringExtra("long");
            validateGetDriverDetails( userId,latitude,longitude);
        }
        return START_STICKY;
    }
    protected void validateGetDriverDetails(String userId,String latitude,String longitude) {
        AsyncHttpClient client = new AsyncHttpClient();
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
                        parseResult(responseCode.toString());
                        try{
                            JSONObject object=new JSONObject(responseCode.toString());
                            Log.e("responseBackground",":"+object.toString(2));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                            Intent broadcastIntent = new Intent();
                            broadcastIntent.putExtra("backgroundArraylist",mDriverdetailsList);
                            broadcastIntent.setAction("update_map");
                            context.sendBroadcast(broadcastIntent);

                            Intent broadcastIntent1 = new Intent();
                            broadcastIntent1.putExtra("backgroundArraylist",mDriverdetailsList);
                            broadcastIntent1.setAction("update_map1");
                            context.sendBroadcast(broadcastIntent1);

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


    private void  parseResult(String result) {

        try {
            mDriverdetailsList.clear();
            driverdetailslocations.clear();
            JSONObject response = new JSONObject(result);

            String message=response.getString("responseMsg");
            JSONArray allDetails = response.optJSONArray("driver_list");
            if (response.getString("responseCode").equals("200")) {
                if (allDetails != null) {

                    for (int i = 0; i < allDetails.length(); i++) {

                        Driverdetails allDetailObj = new Driverdetails();

                        JSONObject object = allDetails.getJSONObject(i);
                        JSONObject object_id=object.getJSONObject("_id");

                        allDetailObj.setId(object_id.optString("$id"));
                        allDetailObj.setDriverId(object.optString("driverId"));
                        allDetailObj.setCabType(object.optString("cabType"));


                        JSONArray location = object.optJSONArray("location");


                            Driverdetailslocation objectloc=new Driverdetailslocation();

                            if(location.getString(0).equals("0")&&location.getString(1).equals("0"))
                            {

                            }else {
                                objectloc.setLatitude(location.getString(0));
                                objectloc.setLongitude(location.getString(1));
                                driverdetailslocations.add(objectloc);
                                allDetailObj.setDriverdetailslocations(driverdetailslocations);
                            }

                        mDriverdetailsList.add(allDetailObj);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}