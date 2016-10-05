package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import constant.MyApplication;

/**
 * Created by narendra on 6/16/2016.
 */
public class WelcomeRegistation extends AppCompatActivity {

    Toolbar toolbar;
    Button btnCreateAccount, btnCreateVipAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount_fragment);

        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
       // toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.nav_icon_1));
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCreateAccount = (Button)findViewById(R.id.btn_crateaccount);
        btnCreateVipAccount = (Button)findViewById(R.id.btnvip_createaccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vipaccount=false;
                Intent in = new Intent(getApplication(),RegistationActivity.class);
                in.putExtra("role_Id","2");
                startActivity(in);
            }
        });


        btnCreateVipAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vipaccount=true;
                Intent in = new Intent(getApplication(),RegistationActivity.class);
                in.putExtra("role_Id","3");
                startActivity(in);
            }
        });


    }
}
