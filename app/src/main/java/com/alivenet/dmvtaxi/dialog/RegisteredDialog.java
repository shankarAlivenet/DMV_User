package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.LoginActivity;
import com.alivenet.dmvtaxi.R;

/**
 * Created by narendra on 6/21/2016.
 */
public class RegisteredDialog extends Dialog {

    Activity activity;
    String userID;
    Button btn_ok ;

    public RegisteredDialog(Activity a, String msg) {
        super(a, R.style.custom_dialog_theme);
        // TODO Auto-generated constructor stub
        activity = a;
        userID = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.registation_dialog);
        btn_ok = (Button) findViewById(R.id.btnok);

        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                new LoginRequestDialogActivity(getContext(), userID).show();

            }
        });


    }

}