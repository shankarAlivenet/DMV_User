package com.alivenet.dmvtaxi;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 7/15/2016.
 */
public class ReservationActivity extends AppCompatActivity {


    TextView txtDatepicker, txtTimePicker;
    Button btnSaveReservation;
    private int mYear, mMonth, mDay, mHour, mMinute,mWeek;
    SharedPreferences mPref;
    public static final String MYPREF = "user_info";
    String userId, cabId;
    ProgressDialog prgDialog;
    Toolbar toolbar;
    double pickLat;
    double pickLong;
    double destLat;
    double destLong;
    String date1,date2,monthd,getdate,paymenttype;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentreservation);


        Intent intent = getIntent();
        mPref = ReservationActivity.this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        txtDatepicker = (TextView) findViewById(R.id.txt_datepicker);
        txtTimePicker = (TextView) findViewById(R.id.txt_timepicker);
        btnSaveReservation = (Button) findViewById(R.id.btnsave_reservation);

        Bundle b = getIntent().getExtras();
        Bundle b1 = getIntent().getExtras();
        userId = mPref.getString("userId", "");
        pickLat = b.getDouble("pickLat");
        pickLong = b.getDouble("pickLong");
        paymenttype = b.getString("PaymentType", "");
        destLat = b1.getDouble("destLat");
        intent.getStringExtra("destLat");
        destLong = b1.getDouble("destLong");
        cabId = intent.getStringExtra("cabId");

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSaveReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommanMethod.isOnline(ReservationActivity.this)) {

                    if (checkValidationJoinUs()) {

                        String time = txtTimePicker.getText().toString().trim();

                        RequestParams params = new RequestParams();
                        params.put("userId", userId);
                        System.out.println("userId>>>>>>>>" + userId);
                        params.put("pickLat", String.valueOf(pickLat));
                        System.out.println("pickLat>>>>>>>>" + String.valueOf(pickLat));
                        params.put("pickLong", String.valueOf(pickLong));
                        System.out.println("pickLong>>>>>>>>" + String.valueOf(pickLong));
                        params.put("destLat", String.valueOf(destLat));
                        System.out.println("destLat>>>>>>>>" + String.valueOf(destLat));
                        params.put("destLong", String.valueOf(destLong));
                        System.out.println("destLong>>>>>>>>" + String.valueOf(destLong));
                        params.put("cabId", cabId);
                        System.out.println("cabId>>>>>>>>" + cabId);
                        params.put("revDate", date1);
                        System.out.println("revDate>>>>>>>>" + date1);
                        params.put("revTime", time);
                        System.out.println("revTime>>>>>>>>" + time);
                        params.put("paymentType", paymenttype);
                        System.out.println("paymentType>>>>>>>>" + paymenttype);

                        reservationWs(params);

                    }
                }
            }
        });


        txtDatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mWeek = c.get(Calendar.DAY_OF_WEEK);
                DatePickerDialog dpd = new DatePickerDialog(ReservationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                try {
                                    //date1 = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + year;
                                    date1 = (year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));


                                    SharedPreferences.Editor editor = mPref.edit();
                                    editor.putString("dateformat1", date1);
                                    editor.apply();
                                    System.out.println("dayOfMonth" + dayOfMonth);
                                    Log.e("dayOfMonth", "" + dayOfMonth);
                                    if (monthOfYear == 0) {
                                        monthd = "Jan";
                                    } else if (monthOfYear == 1) {
                                        monthd = "Feb";
                                    } else if (monthOfYear == 2) {
                                        monthd = "Mar";
                                    } else if (monthOfYear == 3) {
                                        monthd = "Apr";
                                    } else if (monthOfYear == 4) {
                                        monthd = "May";
                                    } else if (monthOfYear == 5) {
                                        monthd = "Jun";
                                    } else if (monthOfYear == 6) {
                                        monthd = "Jul";
                                    } else if (monthOfYear == 7) {
                                        monthd = "Aug";
                                    } else if (monthOfYear == 8) {
                                        monthd = "Sept";
                                    } else if (monthOfYear == 9) {
                                        monthd = "Oct";
                                    } else if (monthOfYear == 10) {
                                        monthd = "Nov";
                                    } else if (monthOfYear == 11) {
                                        monthd = "Dec";
                                    }
                                    getdate = monthd + "  " + String.valueOf(dayOfMonth) + "," + " " + String.valueOf(year);
                                    System.out.println("dateprntln>>>>>>>>>>" + getdate);

                                    SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
                                    Date date = new Date(year, monthOfYear, dayOfMonth - 1);
                                    System.out.println("dateofcurrent" + date.toString());
                                    String dayName = sdf_.format(date);
                                    // Display Selected date in textbox
                                    txtDatepicker.setText("" + dayName + "," + " " + getdate);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, mYear, mMonth, mDay);

                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();

            }
        });



        txtTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                int mYearyy = c.get(Calendar.YEAR);
                int mMonthhh = c.get(Calendar.MONTH);
                int mDayyy = c.get(Calendar.DAY_OF_MONTH);

                  date2 = (mYearyy + "-" + String.format("%02d", (mMonthhh + 1)) + "-" + String.format("%02d", mDayyy));
              //  date2 = String.format("%02d", mDayyy) + "-" + String.format("%02d", (mMonthhh + 1)) + "-" + mYearyy;

                Log.e("date without time  1", " date is " + txtDatepicker.getText().toString());
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReservationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                Log.e("date without time ", " date is " + txtDatepicker.getText().toString());

                                System.out.println("current date  " + date2);
                                System.out.println("set date  " + txtDatepicker.getText().toString());

                                if (date1.equals("")) {
                                    System.out.println("date in if " + date1);
                                    txtTimePicker.setText(String.format("%02d", mHour) + ":" + String.format("%02d", mMinute));
                                } else if ((date2.equals(date1)) && (mHour >= hourOfDay) && (mMinute >= minute)) {

                                    CommanMethod.showAlert("Please Select After 30 Minutes To Current Time.",ReservationActivity.this);
                                    //txtTimePicker.setText(String.format("%02d", mHour) + ":" + String.format("%02d", mMinute));
                                } else {
                                    txtTimePicker.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                                }
                            }
                        }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });
    }


    public void reservationWs(RequestParams params) {

        String url = WebserviceUrlClass.resevation;
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
                    Log.d("Json_conCALL", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        if (responseMessage.equals("success")) {
                            CommanMethod.showAlert(responseMessage,ReservationActivity.this);

                            ReservationActivity.this.finish();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),ReservationActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                ReservationActivity.this.finish();

                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private boolean checkValidationJoinUs() {

        if (date1==null) {
            CommanMethod.showAlert("Please select Date", ReservationActivity.this);
            return false;
        } else if ((txtTimePicker.getText().toString().trim().equals("Time")) || (txtTimePicker.getText().toString().trim().equals(""))) {
            CommanMethod.showAlert("Please select Time", ReservationActivity.this);
            return false;
        }

        return true;
    }


}
