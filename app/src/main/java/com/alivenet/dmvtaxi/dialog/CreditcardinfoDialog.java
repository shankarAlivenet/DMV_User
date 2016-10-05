package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alivenet.dmvtaxi.CardInfoActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.pojo.DriverTime;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;

import constant.MyPreferences;

/**
 * Created by ranjeet on 7/25/2016.
 */
public class CreditcardinfoDialog extends Dialog {

    Activity activity;
    String noofpeople;
    TextView mcardno,mexpmonth,mexpyear,mcvv,mchangecardInfo,mNo;

    public CreditcardinfoDialog(Activity a, String msg) {
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
        setContentView(R.layout.credit_card_info);

        mcardno=(TextView)findViewById(R.id.tv_cardno);
        mexpmonth=(TextView)findViewById(R.id.tv_expmonth);
        mexpmonth=(TextView)findViewById(R.id.tv_expmonth);
        mexpyear=(TextView)findViewById(R.id.tv_expyear);
        mcvv=(TextView)findViewById(R.id.tv_cvv);
        mchangecardInfo=(TextView)findViewById(R.id.btn_change);
        mNo=(TextView)findViewById(R.id.btn_no) ;
        mcardno.setText(MyPreferences.getActiveInstance(activity).getCardno());
        mexpmonth.setText(MyPreferences.getActiveInstance(activity).getExpmonth());
        mexpyear.setText(MyPreferences.getActiveInstance(activity).getExpyear());
        mcvv.setText(MyPreferences.getActiveInstance(activity).getCvvno());

        mchangecardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreferences.getActiveInstance(activity).setcardInfo(true);
                Intent in = new Intent(activity, CardInfoActivity.class);
                activity.startActivityForResult(in, 1);
            }
        });

        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}