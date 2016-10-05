package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import constant.MyPreferences;

/**
 * Created by narendra on 6/30/2016.
 */
public class CardInfoActivity extends AppCompatActivity  {

    Toolbar toolbar;
    Button btnSavepayment;
    Spinner spinneryear, spinnermonth;
    EditText metcardNumber,metcvvNumber;
    ArrayList<String> yearlist;
    String[] monthArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};


    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardinfo);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        metcardNumber = (EditText)findViewById(R.id.et_cardnumber_info);
        metcvvNumber = (EditText)findViewById(R.id.et_cardcvvnumber);
        spinnermonth = (Spinner) findViewById(R.id.spinner_month);
        spinneryear = (Spinner) findViewById(R.id.spinner_year);
        btnSavepayment = (Button) findViewById(R.id.btnsave_cardinfo);


        yearlist = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 9; i++) {

            yearlist.add(String.valueOf(year));
            System.out.println("yearlist="+yearlist.get(i));
            year = year + 1;
        }
        ArrayAdapter monthAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, monthArray);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(monthAdapter);

        ArrayAdapter yearAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, yearlist);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneryear.setAdapter(yearAdapter);

        spinnermonth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) CardInfoActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(CardInfoActivity.this.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        spinneryear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) CardInfoActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(CardInfoActivity.this.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        btnSavepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidationJoinUs())
                {
                    String cardno = metcardNumber.getText().toString();
                    String cvvno = metcvvNumber.getText().toString();
                    String expyear = spinneryear.getSelectedItem().toString();
                    String expmonth = spinnermonth.getSelectedItem().toString();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("cardNo",cardno);
                    returnIntent.putExtra("cvvNo",cvvno);
                    returnIntent.putExtra("cardexpmont",expmonth);
                    returnIntent.putExtra("cardexpyear",expyear);
                    returnIntent.putExtra("cardinfo","true");
                    setResult(1,returnIntent);
                    Toast.makeText(getApplicationContext(), "Card information is successfully saved ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
    @OnTextChanged(value = R.id.et_cardnumber_info, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }


    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    private boolean checkValidationJoinUs() {

        Calendar c = Calendar.getInstance();
        int yearr = c.get(Calendar.YEAR);
        int monthh = c.get(Calendar.MONTH)+1;
        Log.e("current Month",""+monthh);

        String year = spinneryear.getSelectedItem().toString();
        String month = spinnermonth.getSelectedItem().toString();

       if (metcardNumber.getText().toString().trim().length()==0) {
            CommanMethod.showAlert("Please enter card Number", CardInfoActivity.this);
            return false;
        }else if (metcardNumber.getText().toString().trim().length()!=19) {
           CommanMethod.showAlert("Please enter valid card Number", CardInfoActivity.this);
           return false;
       }
        else if (metcvvNumber.getText().toString().trim().length()==0) {
            CommanMethod.showAlert("Please enter cvv Number", CardInfoActivity.this);
            return false;
        }else if (metcvvNumber.getText().toString().trim().length()!=3) {
            CommanMethod.showAlert("Please enter valid cvv Number", CardInfoActivity.this);
            return false;
        }else if (Integer.parseInt(year)==yearr) {
           if (Integer.parseInt(month) < monthh) {
               CommanMethod.showAlert("Please select the valid exp date", CardInfoActivity.this);
               return false;
           } else {
               return true;
           }
       }
        return true;


    }

}
