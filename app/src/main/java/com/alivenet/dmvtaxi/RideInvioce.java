package com.alivenet.dmvtaxi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

public class RideInvioce extends AppCompatActivity {
    ProgressDialog prgDialog;
    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    RatingBar mratingbar;
    ImageView minvoicemap,mimg_driver,mimg_taxi,mimg_watch;
    TextView mtv_name,mtv_taxi,mpaymenttype,mtv_price,mtv_distance,mtv_time,mtime1,mtv_address1,mtv_address2,mtime2,mtotalfarerupe,mtv_taxes,mtv_roundoff,mtv_totalbill,mtv_cash,mtv_taxicolor;


    String mUserId,bookingID;
    String invoice_id,invoice_bookingid,invoice_userid,invoice_driverid,invoice_cab_type,invoice_pickup_lat,invoice_pickup_long,invoice_pickup_address,invoice_destination_address,invoice_payment_id,invoice_payment_method,invoice_estimated_distance;
    String invoice_destination_lat,invoice_destination_long,invoice_cab_name,invoice_estimated_fare,invoice_ride_start_time,invoice_ride_end_time,invoice_image,invoice_vehicle,invoice_driverrating,invoice_estimated_time,invoice_driverName,invoice_cabIcon,invoice_driverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_invoice);

        minvoicemap = (ImageView) findViewById(R.id.invoicemap);
        mimg_driver = (ImageView) findViewById(R.id.img_driver);
        mimg_taxi = (ImageView) findViewById(R.id.img_taxi);
        mimg_watch = (ImageView) findViewById(R.id.img_watch);

        mtv_name = (TextView) findViewById(R.id.tv_name);
        mtv_taxi = (TextView) findViewById(R.id.tv_taxi);
        mtv_price = (TextView) findViewById(R.id.tv_price);
        mtv_distance = (TextView) findViewById(R.id.tv_distance);
        mtv_time = (TextView) findViewById(R.id.tv_time);
        mtime1 = (TextView) findViewById(R.id.time1);
        mtv_address1 = (TextView) findViewById(R.id.tv_address1);
        mtv_address2 = (TextView) findViewById(R.id.tv_address2);
        mtime2 = (TextView) findViewById(R.id.time2);
        mtotalfarerupe = (TextView) findViewById(R.id.totalfarerupe);
        mtv_taxes = (TextView) findViewById(R.id.tv_taxes);
        mtv_roundoff = (TextView) findViewById(R.id.tv_roundoff);
        mtv_taxes = (TextView) findViewById(R.id.tv_taxes);
        mtv_cash = (TextView) findViewById(R.id.tv_cash);
        mtv_taxicolor = (TextView) findViewById(R.id.tv_taxicolor);
        mtv_totalbill = (TextView) findViewById(R.id.tv_totalbill);
        mpaymenttype = (TextView) findViewById(R.id.paymenttype);
        mratingbar = (RatingBar) findViewById(R.id.ratingbar);

        LayerDrawable stars = (LayerDrawable) mratingbar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mPref = this.getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);
        bookingID= MyApplication.bookingId;

        prgDialog = new ProgressDialog(RideInvioce.this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        if(bookingID!=null) {
            RequestParams params = new RequestParams();
            params.put("bookingId", bookingID);
            params.put("userId", mUserId);
            User_invoice(params);
        }









    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                RideInvioce.this.finish();

                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void User_invoice(RequestParams params) {

        String url = WebserviceUrlClass.userinvoice;
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
                try {
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {
                       JSONObject invoiceobject;
                        invoiceobject = response.getJSONObject("invoicedata");
                        invoice_id=invoiceobject.getString("id");
                        invoice_bookingid=invoiceobject.getString("booking_id");
                        invoice_pickup_lat=invoiceobject.getString("pickup_lat");
                        invoice_pickup_long=invoiceobject.getString("pickup_long");
                        invoice_destination_lat=invoiceobject.getString("destination_lat");
                        invoice_destination_long=invoiceobject.getString("destination_long");

                        invoice_pickup_address=invoiceobject.getString("pickup_address");
                        invoice_destination_address=invoiceobject.getString("destination_address");

                        invoice_payment_id=invoiceobject.getString("payment_id");
                        invoice_estimated_distance=invoiceobject.getString("estimated_distance");
                        invoice_estimated_time=invoiceobject.getString("estimated_time");
                        invoice_estimated_fare=invoiceobject.getString("estimated_fare");
                        invoice_payment_method=invoiceobject.getString("payment_method");
                        invoice_ride_start_time=Timeformate(invoiceobject.getString("ride_start_time"));
                        invoice_ride_end_time=Timeformate(invoiceobject.getString("ride_end_time"));
                        invoice_image=invoiceobject.getString("image");
                        invoice_vehicle=invoiceobject.getString("vehicle");
                        invoice_driverrating=invoiceobject.getString("driverRating");
                        invoice_driverName=invoiceobject.getString("driverName");
                        invoice_cabIcon=invoiceobject.getString("cabImage");
                        invoice_driverImage=invoiceobject.getString("driverImage");
                        invoice_cab_name=invoiceobject.getString("cab_name");
                        mtv_name.setText(invoice_driverName);
                        mtv_price.setText("$ "+invoice_estimated_fare);
                        mtv_distance.setText(invoice_estimated_distance+" miles");
                        mtv_time.setText(invoice_estimated_time+" min");

                        mtime1.setText(invoice_ride_start_time);
                        mtime2.setText(invoice_ride_end_time);

                        System.out.println("invoice_ride_start_time == " + invoice_ride_start_time);
                        System.out.println("invoice_ride_end_time== " + invoice_ride_end_time);


                        mtv_taxi.setText(invoice_vehicle);
                        mtv_taxicolor.setText(invoice_cab_name);
                        mtotalfarerupe.setText("$ "+invoice_estimated_fare);
                        mtv_taxes.setText("$ "+"0.0");
                        mtv_roundoff.setText("$ "+"0.0");
                        mtv_totalbill.setText("$ "+invoice_estimated_fare);
                        mpaymenttype.setText(invoice_payment_method);
                        mtv_cash.setText("$ "+invoice_estimated_fare);



                        float rating = Float.parseFloat(invoice_driverrating);
                        mratingbar.setRating(rating);



                      //  mtv_address1.setText(getCompleteAddressString(Double.parseDouble(invoice_pickup_lat),Double.parseDouble(invoice_pickup_long)));
                        mtv_address1.setText(invoice_pickup_address);
                        mtv_address2.setText(invoice_destination_address);
                       // mtv_address2.setText(getCompleteAddressString(Double.parseDouble(invoice_destination_lat),Double.parseDouble(invoice_destination_long)));
                        String url = "http://maps.google.com/maps/api/staticmap?center="+invoice_pickup_lat+","+invoice_pickup_long+"&markers=icon:http://tinyurl.com/2ftvtt6|"+invoice_pickup_lat+","+invoice_pickup_long+"&zoom=12&size=600x400&sensor=false&markers=icon:http://tinyurl.com/2ftvtt6|"+invoice_destination_lat+","+invoice_destination_long;


                        Picasso.with(RideInvioce.this)
                                .load(url)
                                .into(minvoicemap);

                        Picasso.with(RideInvioce.this)
                                .load(invoice_driverImage)
                                .into(mimg_driver);

                        Picasso.with(RideInvioce.this)
                                .load(invoice_cabIcon)
                                .into(mimg_taxi);



                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),RideInvioce.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.dismiss();
            }
        });
    }




   private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(RideInvioce.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }

    public String Timeformate(String timestring)
    {
        String CurrentString = timestring;
        String[] separated = CurrentString.split("-");
        String[] splitdaytime = separated[2].split(" ");
        int selectedYear = Integer.parseInt(separated[0]);
        int selectedDay = Integer.parseInt(splitdaytime[0]);
        int selectedMonth = Integer.parseInt(separated[1]);
        splitdaytime[1] = splitdaytime[1].substring(0, splitdaytime[1].length() - 3);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, selectedYear);
        cal.set(Calendar.DAY_OF_MONTH, selectedDay);
        cal.set(Calendar.MONTH, selectedMonth-1);
        String format = new SimpleDateFormat("EEEE, MMM d, yyyy").format(cal.getTime());
        String finaldateformate=format+"  "+splitdaytime[1];
        System.out.println("formated day time == " + finaldateformate);
        return finaldateformate;
    }

}
