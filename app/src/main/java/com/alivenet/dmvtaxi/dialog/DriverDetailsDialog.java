package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.pojo.DriverTime;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;

/**
 * Created by bali on 17/7/16.
 */
public class DriverDetailsDialog extends Dialog {

    Activity activity;
    String noofpeople;
    DriverTime driverTime;
    Driverfulldetails driverfulldetails;
    TextView marrivaltime,mname,mlicence,mtaximodel;

    public DriverDetailsDialog(Activity a, String msg,DriverTime driver, Driverfulldetails mdriverfulldetails) {
        super(a, R.style.custom_dialog_theme);
        // TODO Auto-generated constructor stub
        activity = a;
        noofpeople = msg;
        driverTime=driver;
        driverfulldetails=mdriverfulldetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_dilog);

        marrivaltime=(TextView)findViewById(R.id.tv_textarrival);
        mname=(TextView)findViewById(R.id.tv_name);
        mlicence=(TextView)findViewById(R.id.tv_licence);
        mtaximodel=(TextView)findViewById(R.id.tv_model);

        StringBuilder builder=new StringBuilder();
        builder.append("ESTIMATED ARRIVAL TIME IN  ");
        builder.append(driverTime.duration);
        marrivaltime.setText(builder.toString());

        mname.setText(driverfulldetails.getDrivername());
        mlicence.setText(driverfulldetails.getLicenceplate());
        mtaximodel.setText(driverfulldetails.getTaximodel());

    }


}
