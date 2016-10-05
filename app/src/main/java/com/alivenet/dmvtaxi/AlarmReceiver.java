package com.alivenet.dmvtaxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Alivenet 05 on 7/13/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";
    String latitude;
    String longitude;
    public  String mUserId;
    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        mUserId=intent.getStringExtra("userid");
        latitude=intent.getStringExtra("lat");
        longitude=intent.getStringExtra("long");
        Intent i = new Intent(context, BackgroundService.class);
        i.putExtra("userid", mUserId);
        i.putExtra("lat", latitude);
        i.putExtra("long", longitude);
        context.startService(i);
    }
}