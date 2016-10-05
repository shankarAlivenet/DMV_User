package com.alivenet.dmvtaxi.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.RideInvioce;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.pojo.RideListResource;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import constant.MyApplication;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 7/26/2016.
 */
public class RideListRecyclerViewAdapter extends RecyclerView.Adapter<RideListRecyclerViewAdapter.RideListViewHolder> {

    public static ArrayList<RideListResource> mRideList;
    private Context context;
    private SparseBooleanArray selectedItems;


    public RideListRecyclerViewAdapter(Context context, ArrayList<RideListResource> mRideList) {
        this.mRideList = mRideList;
        this.context = context;

    }




    @Override
    public RideListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_ride, null);
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_ride, parent, false);
        RideListViewHolder rcv = new RideListViewHolder(layoutView);
        return rcv;
    }




    @Override
    public void onBindViewHolder(final RideListViewHolder holder, final int position) {

        String status = mRideList.get(position).getUstatus();

        //    holder.cabName.setText(mCabDetailsList.get(position).getCabName());

        holder.bokkingTime.setText(CommanMethod.Timeformate(mRideList.get(position).getBookingTime()));
        if (status.equals("1")) {
            holder.status.setText("Scheduled");
            holder.status.setBackgroundColor(ContextCompat.getColor(context, R.color.status_sheduled));
            holder.status.setTag(1);
        } else if (status.equals("2")) {
            holder.status.setText("Started");
            holder.status.setBackgroundColor(ContextCompat.getColor(context, R.color.status_sheduled));
            holder.status.setTag(2);

        } else if (status.equals("3")) {
            holder.status.setText("Cancelled");
            holder.status.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.status.setTag(3);
            //  holder.status.setBackgroundColor(Color.RED);
        } else if (status.equals("4")) {
            holder.status.setText("Completed");
            holder.status.setBackgroundColor(ContextCompat.getColor(context, R.color.graycolor));
            holder.status.setTag(4);
        }

         holder.txtcarnumber.setText(mRideList.get(position).getCabName());

        if(mRideList.get(position).getPickaddress()!=null&&mRideList.get(position).getPickaddress()!="")
        holder.txtsourceaddress.setText(mRideList.get(position).getPickaddress());

        if(mRideList.get(position).getDestaddress()!=null&&mRideList.get(position).getDestaddress()!="")
        holder.txtdestinationaddress.setText(mRideList.get(position).getDestaddress());


        if(mRideList.get(position).getCabIcon()!=null&&mRideList.get(position).getCabIcon()!="") {

            Picasso.with(context)
                    .load(mRideList.get(position).getCabIcon())
                    .into(holder.cabIcon);

        }

        if(mRideList.get(position).getDriverImage()!=null&&mRideList.get(position).getDriverImage()!="")
        {
            Picasso.with(context)
                    .load(mRideList.get(position).getDriverImage())
                    .into(holder.driverimage);
        }




        //  getCompleteAddressString(Double.parseDouble(mRideList.get(position).getPickupLat()),Double.parseDouble(mRideList.get(position).getPickupLong()));
    }

    @Override
    public int getItemCount() {

        return this.mRideList.size();
    }


    class RideListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View oldSelection = null;
        public View view;
        public TextView cabName, bokkingTime, status,txtcarnumber,txtsourceaddress,txtdestinationaddress;
        public ImageView cabIcon,driverimage;
        public CardView myBackground;
        int position;
        int oldposition;
        ProgressDialog prgDialog;
        public RideListViewHolder(View itemView) {
            super(itemView);


            itemView.setOnClickListener(this);


            prgDialog = new ProgressDialog(context);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);


            bokkingTime = (TextView) itemView.findViewById(R.id.booking_time);
            status = (TextView) itemView.findViewById(R.id.status);
            txtcarnumber = (TextView) itemView.findViewById(R.id.carnumber);
            txtsourceaddress = (TextView) itemView.findViewById(R.id.txt_sourceaddress);
            txtdestinationaddress = (TextView) itemView.findViewById(R.id.txt_destinationaddress);
            driverimage = (ImageView) itemView.findViewById(R.id.driverimage);
            cabIcon = (ImageView) itemView.findViewById(R.id.restro_photo);
            status.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            oldposition = getAdapterPosition();
            System.out.println("posddd>>>>>>>>"+oldposition);


            if (Integer.parseInt(status.getTag().toString()) == 1) {

                String bookingID = mRideList.get(oldposition).getBookingId();
                Toast.makeText(context, "hello1 " + bookingID, Toast.LENGTH_LONG).show();
                RequestParams params = new RequestParams();
                params.put("bookingId", bookingID);
                cancelledRideWs(params);
                // Toast.makeText(context,"hello1 ",Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(status.getTag().toString()) == 2) {/*
                String bookingID = mRideList.get(oldposition).getBookingId();
                Toast.makeText(context, "hello1 " + bookingID, Toast.LENGTH_LONG).show();
                RequestParams params = new RequestParams();
                params.put("bookingId", bookingID);
                cancelledRideWs(params);*/
                Toast.makeText(context, "Ride is started you cant cancel.  ", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(status.getTag().toString()) == 3) {
              //  Toast.makeText(context, "hello2 ", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(status.getTag().toString()) == 4) {

                MyApplication.bookingId=mRideList.get(oldposition).bookingId;
                System.out.println("bookingId == " + MyApplication.bookingId);
                Intent in = new Intent(context, RideInvioce.class);
                context.startActivity(in);



            }






        }
        public void cancelledRideWs(RequestParams params) {
            String url = WebserviceUrlClass.Ridecancel;
            prgDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    prgDialog.hide();
                    try {
                        Log.d("Json_con", response.toString());
                        String responseCode = response.getString("responseCode");
                        String responseMessage = response.getString("responseMsg");

                        if (responseCode.equals("200")) {

                            MyApplication.notificationconfiride=false;
                            // notifyDataSetChanged();
                            status.setText("Cancelled");
                            status.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            status.setTag(3);

                            System.out.println("calling updatelist broadcastreciver ");

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("updatelist");
                            context.sendBroadcast(broadcastIntent);

                        } else {
                            //  new IncorrectPasswordDialog(LoginActivity.this, "hello").show();
                            Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    prgDialog.hide();
                    Toast toast = Toast.makeText(context, "Unable to connect to the server, please try again later.", Toast.LENGTH_SHORT);toast.show();
                }


                @Override
                public void onFinish() {
                    super.onFinish();
                    prgDialog.hide();
                }
            });
        }






    }



}



