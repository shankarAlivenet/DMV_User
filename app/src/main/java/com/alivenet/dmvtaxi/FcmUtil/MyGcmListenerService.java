package com.alivenet.dmvtaxi.FcmUtil;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alivenet.dmvtaxi.AcceptNotificationUIwork;
import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.SharedPreference;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;
import com.alivenet.dmvtaxi.pojo.RideDriverComplete;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

import constant.MyApplication;

public class MyGcmListenerService extends GcmListenerService {
    Intent intent;
    public boolean flag=false;
    private static final String TAG = "g";

    public static boolean splitnotifiaction_flag;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
       Log.d(TAG, "From: " + data);
        String message=data.getString("message");
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        if(message!=null)
        {
            if(message.startsWith("Ride has been cancelled by the driver"))
            {
                MyApplication.declinedriver=true;
                MyApplication.driverdeclinebookingid=data.getString("bookingId");
                MyApplication.notificationconfiride=false;

                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(getApplicationContext().getPackageName())) {
                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction("Driverdecline");
                                sendBroadcast(broadcastIntent);

                            }else{

                            }
                        }
                    }else{
                        sendNotification(message);
                    }



                }



            }
            if(message.startsWith("Dear")) {

                MyApplication. notimsg =data.getString("message");
                MyApplication.notibookingId=data.getString("bookingId");
                MyApplication.notipUser=data.getString("pUser");
                MyApplication.notisUser=data.getString("sUser");
                splitnotifiaction_flag=true;
                MyApplication.splitride=true;

                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(getApplicationContext().getPackageName())) {
                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction("splitrideaccept");
                                sendBroadcast(broadcastIntent);

                            }else{

                            }
                        }
                    }else{
                        sendNotification(message);
                    }



                }





                   /* Intent i = new Intent("AcceptNotificationUIwork");
                    sendBroadcast(i);*/

            }
            if(message.equals("ride complete successfully"))
            {
                MyApplication.radeyourrite=true;
                MyApplication.notificationconfiride=false;
                MyApplication.RateyourRide=false;
                flag=true;
                MyApplication.rideDriverComplete=new RideDriverComplete();
                String driverName=data.getString("driverName");
                String pickLat=data.getString("pickLat");
                String pickLong=data.getString("pickLong");
                String destLat=data.getString("destLat");
                String destLong=data.getString("destLong");
                String pickupaddress=data.getString("pickupaddress");
                String destinationaddress=data.getString("destinationaddress");
                String driverId=data.getString("driverId");
                String vehicle=data.getString("vehicle");
                String totalfare=data.getString("total_fare");
                String rideId=data.getString("rideId");
                MyApplication.RideId=rideId;
                String licenseId=data.getString("licenseId");
                if(message!=null)
                {
                    MyApplication.rideDriverComplete.setMessageride(message);
                }else {
                    MyApplication.rideDriverComplete.setMessageride("");
                }
                if(driverName!=null)
                {
                    MyApplication.rideDriverComplete.setDriverNameride(driverName);
                }else {
                    MyApplication.rideDriverComplete.setDriverNameride("");
                }
                if(pickLat!=null)
                {
                    MyApplication.rideDriverComplete.setPickLatride(pickLat);
                }else {
                    MyApplication.rideDriverComplete.setPickLatride("");
                }
                if(pickLong!=null)
                {
                    MyApplication.rideDriverComplete.setPickLongride(pickLong);
                }else {
                    MyApplication.rideDriverComplete.setPickLongride("");
                }
                if(destLat!=null)
                {
                    MyApplication.rideDriverComplete.setDestLatride(destLat);
                }else {
                    MyApplication.rideDriverComplete.setDestLatride("");
                }
                if(destLong!=null)
                {
                    MyApplication.rideDriverComplete.setDestLongride(destLong);
                }else {
                    MyApplication.rideDriverComplete.setDestLongride("");
                }
                if(driverId!=null)
                {
                    MyApplication.rideDriverComplete.setDriverIdride(driverId);
                }else {
                    MyApplication.rideDriverComplete.setDriverIdride("");
                }
                if(vehicle!=null)
                {
                    MyApplication.rideDriverComplete.setVehicle(vehicle);
                }else {
                    MyApplication.rideDriverComplete.setVehicle("");
                }
                if(rideId!=null)
                {
                    MyApplication.rideDriverComplete.setRideId(rideId);
                }else {
                    MyApplication.rideDriverComplete.setRideId("");
                }
                if(licenseId!=null)
                {
                    MyApplication.rideDriverComplete.setLicenseId(licenseId);
                }else {
                    MyApplication.rideDriverComplete.setLicenseId("");
                }
                if(totalfare!=null)
                {
                    MyApplication.rideDriverComplete.setTotalfare(totalfare);
                }else {
                    MyApplication.rideDriverComplete.setTotalfare("");
                }
                if(pickupaddress!=null)
                {
                    MyApplication.rideDriverComplete.setPickupaddress(pickupaddress);
                }else {
                    MyApplication.rideDriverComplete.setPickupaddress("");
                }
                if(destinationaddress!=null)
                {
                    MyApplication.rideDriverComplete.setDestinationaddress(destinationaddress);
                }else {
                    MyApplication.rideDriverComplete.setDestinationaddress("");
                }


                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(getApplicationContext().getPackageName())) {
                                MyApplication.pickuploction=false;
                                Intent broadcastIntent = new Intent();
                                broadcastIntent.putExtra("ridedriverInfo",MyApplication.rideDriverComplete);
                                broadcastIntent.setAction("DriverNotificationcomplete");
                                sendBroadcast(broadcastIntent);

                            }else{

                            }
                        }
                    }



                }



            }
        }


        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(getApplicationContext().getPackageName())) {


                    }else{
                        System.out.println("App in background state");
                        sendNotification(message);
                }
            }
        }else{
                System.out.println("App in background state");
                sendNotification(message);
            }



    }



    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void sendNotification(String message) {
        SharedPreference sharedPreference=new SharedPreference();

        if (splitnotifiaction_flag)
        {
            intent = new Intent(this, AcceptNotificationUIwork.class);
            if (flag == true) {
                intent.putExtra("ridedriverInfo", MyApplication.rideDriverComplete);
                intent.putExtra("flag", flag);
            }
        }else {

            intent = new Intent(this, DeashboardActivity.class);
            if (flag == true) {
                intent.putExtra("ridedriverInfo", MyApplication.rideDriverComplete);
                intent.putExtra("flag", flag);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Notification")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
    }



}
