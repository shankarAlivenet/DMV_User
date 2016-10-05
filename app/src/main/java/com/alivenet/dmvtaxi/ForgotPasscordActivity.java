package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by narendra on 6/15/2016.
 */
public class ForgotPasscordActivity extends Dialog {
    Activity activity;
    String noofpeople;
    Button btncancel , btnconfirm;

    public ForgotPasscordActivity(Activity a, String msg) {
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
        setContentView(R.layout.forgotpassword_dialog);
        btnconfirm = (Button) findViewById(R.id.btncanfirm_dialog);
        btncancel = (Button) findViewById(R.id.btncancel_dialog);
        // people_size.setText(noofpeople);
        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dismiss dialog box
                dismiss();
            }
        });

        btnconfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // dismiss dialog box
                dismiss();
            }
        });

    }

}