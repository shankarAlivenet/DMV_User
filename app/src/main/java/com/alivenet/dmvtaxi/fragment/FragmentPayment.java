package com.alivenet.dmvtaxi.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.alivenet.dmvtaxi.LoginDialogActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/17/2016.
 */
public class FragmentPayment extends Fragment {

    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    public String mUserId;
    Button btnSavepayment, btnPaypal;
    Spinner spinneryear, spinnermonth;
    EditText metcardNumber, metcvvNumber;
    ArrayList<Integer> yearlist;
    String[] monthArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    String year;
    String month;
    String cardNumber, cvvNo, expirationDate;
    ArrayAdapter monthAdapter,yearAdapter;
    ProgressDialog prgDialog;

    Activity activity;

    // change below by navin
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
    // end here changes



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        ButterKnife.bind(activity);
    }

    public static FragmentPayment newInstance(String sectionTitle) {
        FragmentPayment fragment = new FragmentPayment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_update_payment, container, false);
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);



        metcardNumber = (EditText) view.findViewById(R.id.cardNumberEditText);
        metcvvNumber = (EditText) view.findViewById(R.id.et_cardcvvnumber);
        spinnermonth = (Spinner) view.findViewById(R.id.spinner_month);
        spinneryear = (Spinner) view.findViewById(R.id.spinner_year);
        btnSavepayment = (Button) view.findViewById(R.id.btnsave_cardinfo);
        // btnPaypal = (Button)view.findViewById(R.id.btn_paypal);
        prgDialog = new ProgressDialog(activity);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        yearlist = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 9; i++) {
            yearlist.add(year);
            year = year + 1;
        }
         monthAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, monthArray);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(monthAdapter);

         yearAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, yearlist);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneryear.setAdapter(yearAdapter);

        spinnermonth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        spinneryear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        btnSavepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommanMethod.isOnline(getActivity())) {
                    if (checkValidationJoinUs()) {

                        String year = spinneryear.getSelectedItem().toString();
                        String month = spinnermonth.getSelectedItem().toString();
                        RequestParams params = new RequestParams();
                        params.put("userId", mUserId);
                        params.put("cardNo", metcardNumber.getText().toString().trim());
                        params.put("cvv", metcvvNumber.getText().toString().trim());
                        params.put("expDate", year + "-" + month);
                        updateCreditCardDetailsWS(params);
                    }
                }

            }
        });



        RequestParams params = new RequestParams();
        params.put("userId", mUserId);
        getProfileDataWS(params);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener


                    if (keyCode == KeyEvent.KEYCODE_BACK) {


                        Fragment homeFragment = new FragmentMainScreen();
                        FragmentManager frgManager;
                        frgManager = getFragmentManager();
                        frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                        return true;
                    }


                    return true;
                }
                return false;
            }
        });

        return view;
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
                    Log.d("ViewProfile response", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        JSONObject jsonObject = response.getJSONObject("profileData");

                        cardNumber = jsonObject.getString("card_no");
                        cvvNo = jsonObject.getString("cvv");
                        expirationDate = jsonObject.getString("expiration_date");
                        String[] bits = expirationDate.split("-");
                        if(bits.length>0)
                        {
                             year = bits[0];
                        }
                        if(bits.length>1)
                        {
                            month = bits[1];
                        }


                        Log.e("month and year is ", month+","+year);

                        if (cardNumber!=null&&cardNumber.equals("null")) {
                            metcardNumber.setText("");
                        }
                        else {
                            metcardNumber.setText(cardNumber);
                        }
                        if (cvvNo!=null&&cvvNo.equals("null")) {
                            metcvvNumber.setText("");
                        }else {
                            metcvvNumber.setText(cvvNo);
                        }
                        if (expirationDate!=null&&expirationDate.equals("null")) {
                            expirationDate = "";
                        }
                        if (month!=null&&!month.equals(null)) {
                            int spinnerPosition = monthAdapter.getPosition(month);
                            spinnermonth.setSelection(spinnerPosition);
                        }
                        if (year!=null&&!year.equals(null)) {
                            int spinnerPositionyear = yearAdapter.getPosition(year);
                            spinneryear.setSelection(spinnerPositionyear);
                        }

                    } else {
                        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.hide();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
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
                    Log.d("UpdateCreditCardDetail=", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        Toast.makeText(getActivity(),responseMessage,Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(),responseMessage,Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.hide();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
            }
            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.hide();
            }
        });
    }



    private boolean checkValidationJoinUs() {

        if (metcardNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your card number ", getActivity());
            return false;
        } else if (metcvvNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your cvv number", getActivity());
            return false;
        }
        return true;

    }

}

