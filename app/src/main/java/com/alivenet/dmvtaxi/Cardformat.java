package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import constant.MyApplication;
import cz.msebera.android.httpclient.Header;


public class Cardformat extends AppCompatActivity {

    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    public String mUserId;
    Button btnSavepayment, btnPaypal;
    Spinner spinneryear, spinnermonth;
    EditText metcardNumber, metcvvNumber;
    ArrayList<String> yearlist;
    String[] monthArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    String year;
    String month;
    String cardNumber, cvvNo, expirationDate;
    ArrayAdapter monthAdapter, yearAdapter;
    ProgressDialog prgDialog;

    //Toolbar toolbar;


    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_payment);
        ButterKnife.bind(this);


        mPref = Cardformat.this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);


        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        metcardNumber = (EditText) findViewById(R.id.cardNumberEditText);
        metcvvNumber = (EditText) findViewById(R.id.et_cardcvvnumber);
        spinnermonth = (Spinner) findViewById(R.id.spinner_month);
        spinneryear = (Spinner) findViewById(R.id.spinner_year);
        btnSavepayment = (Button) findViewById(R.id.btnsave_cardinfo);
        prgDialog = new ProgressDialog(Cardformat.this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        yearlist = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 9; i++) {
            yearlist.add(String.valueOf(year));
            System.out.println("yearlist=" + yearlist.get(i));
            year = year + 1;
        }
        monthAdapter = new ArrayAdapter(Cardformat.this, R.layout.simple_spinner_item, monthArray);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(monthAdapter);

        yearAdapter = new ArrayAdapter(Cardformat.this, R.layout.simple_spinner_item, yearlist);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneryear.setAdapter(yearAdapter);

        spinnermonth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) Cardformat.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Cardformat.this.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        spinneryear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) Cardformat.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Cardformat.this.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        btnSavepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommanMethod.isOnline(Cardformat.this)) {
                    if (checkValidationJoinUs()) {

                        String year = spinneryear.getSelectedItem().toString();
                        String month = spinnermonth.getSelectedItem().toString();
                        RequestParams params = new RequestParams();
                        params.put("userId", mUserId);
                        params.put("cardNo", metcardNumber.getText().toString().trim());
                        params.put("cvv", metcvvNumber.getText().toString().trim());
                        params.put("expDate", month + "/" + year);
                        updateCreditCardDetailsWS(params);
                    }
                }

            }
        });


        RequestParams params = new RequestParams();
        params.put("userId", mUserId);
        getProfileDataWS(params);


    }

    @OnTextChanged(value = R.id.cardNumberEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Cardformat.this.finish();

                return false;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void getProfileDataWS(RequestParams params) {

        String url = WebserviceUrlClass.ViewProfile;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                prgDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                prgDialog.hide();
                try {
                    JSONObject object = new JSONObject(response.toString());
                    Log.d("Json_conpaymentType", object.toString(2));
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {
                        JSONObject jsonObject = response.getJSONObject("profileData");

                        cardNumber = jsonObject.getString("card_no");
                        cvvNo = jsonObject.getString("cvv");
                        expirationDate = jsonObject.getString("expiration_date");

                        System.out.println("expirationDate" + expirationDate);

                        String[] bits = expirationDate.split("/");
                        if (bits.length > 0) {
                            month = bits[0];
                            int spinnerPosition = monthAdapter.getPosition(month);
                            spinnermonth.setSelection(spinnerPosition);
                        }
                        if (bits.length > 1) {
                            year = bits[1];
                            int spinnerPositionyear = yearAdapter.getPosition(year);
                            spinneryear.setSelection(spinnerPositionyear);
                        }


                        Log.e("month and year is ", month + "," + year);

                        if (cardNumber != null && cardNumber.equals("null")) {
                            metcardNumber.setText("");
                        } else {
                            metcardNumber.setText(cardNumber);
                        }
                        if (cvvNo != null && cvvNo.equals("null")) {
                            metcvvNumber.setText("");
                        } else {
                            metcvvNumber.setText(cvvNo);
                        }
                        if (expirationDate != null && expirationDate.equals("null")) {
                            expirationDate = "";
                        }
                        if (month != null && !month.equals(null)) {
                            int spinnerPosition = monthAdapter.getPosition(month);
                            spinnermonth.setSelection(spinnerPosition);
                        }
                        if (year != null && !year.equals(null)) {
                            int spinnerPositionyear = yearAdapter.getPosition(year);
                            spinneryear.setSelection(spinnerPositionyear);
                        }

                    } else {
                        Toast.makeText(Cardformat.this, responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.hide();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error), Cardformat.this);
            }


            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.hide();
            }
        });
    }

    public void updateCreditCardDetailsWS(RequestParams params) {
        String url = WebserviceUrlClass.UpdateCreditCardDetail;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                prgDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("credt_cardsave ", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        MyApplication.vipaccountchk = "yes";
                        Toast.makeText(Cardformat.this, responseMessage, Toast.LENGTH_LONG).show();
                        finish();

                    } else {

                        Toast.makeText(Cardformat.this, responseMessage, Toast.LENGTH_LONG).show();
                        finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.hide();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error), Cardformat.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.hide();
            }
        });
    }

    private boolean checkValidationJoinUs() {

        Calendar c = Calendar.getInstance();
        int yearr = c.get(Calendar.YEAR);
        int monthh = c.get(Calendar.MONTH) + 1;

        String year = spinneryear.getSelectedItem().toString();
        String month = spinnermonth.getSelectedItem().toString();

        if (metcardNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your card number ", Cardformat.this);
            return false;
        } else if (metcvvNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your cvv number", Cardformat.this);
            return false;
        } else if (Integer.parseInt(year) == yearr) {
            if (Integer.parseInt(month) < monthh) {
                CommanMethod.showAlert("Please select the valid exp date", Cardformat.this);
                return false;
            } else {
                return true;
            }
        }
        return true;

    }


}
