package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;

import constant.MyApplication;

/**
 * Created by narendra on 6/30/2016.
 */
public class FrequentLocationDialog extends Dialog {
    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    Context activity;
    String noofpeople;
    Button btnlogout_Yes, btnlogout_No;

    public FrequentLocationDialog(Context a, String msg) {
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
        setContentView(R.layout.frequent_location_dialog);
        mPref = getContext().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        btnlogout_Yes = (Button) findViewById(R.id.btnnotnow_dialog);
        btnlogout_No = (Button) findViewById(R.id.btnyes_dialog);
        // people_size.setText(noofpeople);
        btnlogout_No.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MyApplication.frequentlocationflag=true;
                Intent in = new Intent(getContext(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(in);
                dismiss();

            }
        });

        btnlogout_Yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dismiss dialog box
                Intent in = new Intent(getContext(), DeashboardActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(in);
                dismiss();


            }
        });

    }

}
