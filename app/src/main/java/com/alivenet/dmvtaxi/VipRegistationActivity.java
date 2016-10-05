package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by narendra on 6/20/2016.
 */
public class VipRegistationActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btnNext;

    EditText metfirstName, metlastName, metemailAddress, metpassword, metcallNumber, metcardNumber, metcardHoldername, metcvcNumber;
    CheckBox chnewLetters;
    String newLater;
    Spinner spinneryear, spinnermonth;
    ArrayList<Integer> yearlist;

    String[] monthArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_registation);

        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
     //   toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.nav_icon_1));
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        metfirstName = (EditText) findViewById(R.id.et_regfirstname);
        metlastName = (EditText) findViewById(R.id.et_reglastname);
        metemailAddress = (EditText) findViewById(R.id.et_regemail);
        metpassword = (EditText) findViewById(R.id.et_regpassword);
        metcallNumber = (EditText) findViewById(R.id.et_regcellnumber);
        metcardNumber = (EditText) findViewById(R.id.et_regcardno);
        metcardHoldername = (EditText) findViewById(R.id.et_regcardhodername);
        metcvcNumber = (EditText) findViewById(R.id.et_regcvcno);

        spinneryear = (Spinner) findViewById(R.id.strip_year);
        spinnermonth = (Spinner) findViewById(R.id.strip_month);

        chnewLetters = (CheckBox) findViewById(R.id.ch_regnewlater);
       /* if (chnewLetters.isChecked()) {
            newLater = "yes";
            System.out.println("newLaterchec" + newLater);
            Log.e("newslatercheck",newLater);
        } else {
            newLater = "no";
            Log.e("newslater_uncheck",newLater);
        }*/

        yearlist = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 9; i++) {
            yearlist.add(year);
            year = year + 1;
        }
        ArrayAdapter monthAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, monthArray);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(monthAdapter);

        ArrayAdapter yearAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, yearlist);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneryear.setAdapter(yearAdapter);
        //  ArrayAdapter yearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yearlist);
        //  yearAdapter.setDropDownViewResource(R.layout.simple_spinner_item);


        spinnermonth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) VipRegistationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(VipRegistationActivity.this.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        spinneryear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) VipRegistationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(VipRegistationActivity.this.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });


        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = spinnermonth.getSelectedItem().toString();
                String year = spinneryear.getSelectedItem().toString();
                String expdate = month + "/" + year;

                if (checkValidationJoinUs()) {
                    Intent in = new Intent(getApplication(), VerificationActivity.class);
                    in.putExtra("regfirstname", metfirstName.getText().toString().trim());
                    in.putExtra("reglastname", metlastName.getText().toString().trim());
                    in.putExtra("regemailaddress", metemailAddress.getText().toString().trim());
                    in.putExtra("regpassword", metpassword.getText().toString().trim());
                    in.putExtra("regmobileno", metcallNumber.getText().toString().trim());
                    in.putExtra("regcardholdername", metcardHoldername.getText().toString().trim());
                    in.putExtra("regcardnumber", metcardNumber.getText().toString().trim());
                    in.putExtra("regcvcnumber", metcvcNumber.getText().toString().trim());
                    in.putExtra("regexpdatecard", expdate);
                    if (chnewLetters.isChecked()) {
                        in.putExtra("regnewsLater", "yes");
                        newLater = "yes";
                        System.out.println("newLaterchec" + newLater);
                        Log.e("newslatercheck", newLater);
                    } else {
                        in.putExtra("regnewsLater", "no");
                        newLater = "no";
                        Log.e("newslater_uncheck", newLater);
                    }

                    Log.e("newslater", newLater);
                    in.putExtra("flag", true);
                    startActivity(in);
                }
            }
        });

    }

    private boolean checkValidationJoinUs() {

        if (metfirstName.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter First Name", VipRegistationActivity.this);
            return false;
        } else if (metlastName.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter Last Name", VipRegistationActivity.this);
            return false;
        } else if (metemailAddress.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter Email", VipRegistationActivity.this);
            return false;
        } else if (!CommanMethod.isEmailValid(metemailAddress.getText().toString().trim())) {
            CommanMethod.showAlert("Please enter valid Email", VipRegistationActivity.this);
            return false;
        } else if (metpassword.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter Password", VipRegistationActivity.this);
            return false;
        }
        else if (metcallNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter call Number", VipRegistationActivity.this);
            return false;
        } else if (metcallNumber.getText().toString().trim().length() != 10) {
            CommanMethod.showAlert("Please enter valid call Number", VipRegistationActivity.this);
            return false;
        }else if (metcardHoldername.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter name on Card ", VipRegistationActivity.this);
            return false;
        } else if (metcardNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter card Number", VipRegistationActivity.this);
            return false;
        }  else if ((metcardNumber.getText().toString().trim().length() > 16)&&(metcardNumber.getText().toString().trim().length() < 16)) {
            CommanMethod.showAlert("Please enter valid card Number", VipRegistationActivity.this);
            return false;
        }else if (metcvcNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter cvc Number", VipRegistationActivity.this);
            return false;
        }
        return true;


    }


}
