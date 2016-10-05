package com.alivenet.dmvtaxi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.squareup.picasso.Picasso;

/**
 * Created by narendra on 7/12/2016.
 */
public class CarInfoDialog extends Dialog {
    ImageView btnClose, carIcon;
    TextView txtcarName, txtbaseFare, txtbasefareDistance, txtperKm, txtafterbasefareDistance, txtPerMin, txtNote,txtRidetimerate;
    Context activity;
    String carName, baseFare, baseFareDistance, perKm, waitingCharge, note, iconUrl,ridetimerate;
    LinearLayout layoutAll;
    FrameLayout frameLayout;

    public CarInfoDialog(Context a, String carName, String baseFare, String baseFareDistance, String perKm,
                         String waitingCharge, String note, String iconUrl) {
        super(a, R.style.custom_dialog_theme);
        // TODO Auto-generated constructor stub
        activity = a;
        this.carName = carName;
        this.baseFare = baseFare;
        this.baseFareDistance = baseFareDistance;
        this.perKm = perKm;
        this.waitingCharge = waitingCharge;
        this.note = note;
        this.iconUrl = iconUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.car_dailogs);
        btnClose = (ImageView) findViewById(R.id.imageView_close);
        carIcon = (ImageView) findViewById(R.id.car_icon);
        txtcarName = (TextView) findViewById(R.id.car_type_name);
        txtbaseFare = (TextView) findViewById(R.id.text_basefare);
        txtbasefareDistance = (TextView) findViewById(R.id.befor_basefare_distance);
        txtperKm = (TextView) findViewById(R.id.text_per_km);
        txtafterbasefareDistance = (TextView) findViewById(R.id.after_basefare_distance);
        txtPerMin = (TextView) findViewById(R.id.text_per_min);
        txtNote = (TextView) findViewById(R.id.text_note);
        layoutAll = (LinearLayout)findViewById(R.id.layour_all);
        frameLayout = (FrameLayout) findViewById(R.id.mainfram);
        txtRidetimerate=(TextView)findViewById(R.id.txt_ride_time_rate);
        if(carName!=null&&!carName.equals(""))
        {
            txtcarName.setText(carName);
        }
        if(baseFare!=null&&!baseFare.equals(""))
        {
            txtbaseFare.setText("$ " + baseFare);
            txtRidetimerate.setVisibility(View.VISIBLE);
        }else {
            txtbaseFare.setVisibility(View.GONE);
        }
        if(baseFareDistance!=null&&!baseFareDistance.equals(""))
        {
            txtbasefareDistance.setText("First " + baseFareDistance + " mile");
        }else {
            txtbasefareDistance.setVisibility(View.GONE);
        }
       if(perKm!=null&&!perKm.equals("")){
           txtperKm.setText("$ " + perKm + " mile");
       }else {
           txtperKm.setVisibility(View.GONE);
       }
        if(baseFareDistance!=null&&!baseFareDistance.equals(""))
        {
            txtafterbasefareDistance.setText("After " + baseFareDistance + " mile");
        }else {
            txtafterbasefareDistance.setVisibility(View.GONE);
        }
       if(waitingCharge!=null&&!waitingCharge.equals(""))
       {
           txtPerMin.setText("$ " + waitingCharge + "/hr");
       }else {
           txtPerMin.setVisibility(View.GONE);
       }
        if(note!=null&&!note.equals(""))
        {
            txtNote.setText(note);
        }else {
            txtNote.setVisibility(View.GONE);
        }
        if(iconUrl!=null&&!iconUrl.equals(""))
        {
            Picasso.with(activity).load(WebserviceUrlClass.ImageUrl + iconUrl).into(carIcon);

        }
        if(ridetimerate!=null)
        {
            txtRidetimerate.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        layoutAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}