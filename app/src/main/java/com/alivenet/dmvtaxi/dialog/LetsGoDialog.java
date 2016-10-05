package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.alivenet.dmvtaxi.R;

/**
 * Created by narendra on 6/30/2016.
 */
public class LetsGoDialog  extends Dialog {

    private final int SPLASH_DISPLAY_LENGTH = 4000;

    Context activity;
    String noofpeople;


    public LetsGoDialog(Context a, String msg) {
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
        setContentView(R.layout.lets_go_dialog);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                dismiss();
               new FrequentLocationDialog(getContext(),"hello").show();

            }
        }, SPLASH_DISPLAY_LENGTH);

    }

}