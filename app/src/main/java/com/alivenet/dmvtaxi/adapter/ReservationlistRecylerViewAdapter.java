package com.alivenet.dmvtaxi.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.pojo.ReservationListModel;

import org.joda.time.DateTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by navin on 8/1/2016.
 */
public class ReservationlistRecylerViewAdapter  extends RecyclerView.Adapter<ReservationlistRecylerViewAdapter.CustomViewHolder> {
    public static ArrayList<ReservationListModel> mResvationlist;
    ReservationListModel feedItem;
    private Context context;
    private BookingId callback;
    DateTimeUtils obj;
    Date date;
    Calendar c;


    public static final String TIME_FORMAT = "h:mm";
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat(TIME_FORMAT);


    public ReservationlistRecylerViewAdapter(Context context,ArrayList<ReservationListModel> mResvlist,BookingId callback) {
        this.mResvationlist = mResvlist;
        this.context = context;
        this.callback=callback;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reservationlistadapterlayout, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

            feedItem = mResvationlist.get(i);




            customViewHolder.bokkingTime.setText(Timeformate(feedItem.getRevdate()+" "+feedItem.getRevtime()));



            System.out.println("dateandtime "+feedItem.getRevdate()+","+feedItem.getRevtime());

            customViewHolder.booking_id.setText(feedItem.getBookingId());
            customViewHolder.pickaddress.setText(feedItem.getPicAddress());
            customViewHolder.destinations.setText(feedItem.getDestAddress());

             customViewHolder.mreservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());


                String reservedate = feedItem.getRevdate();
                String reservetime = feedItem.getRevtime();

                final  String[] separateddate = reservedate.split("-");
                final String[] splitdaytimetime = reservetime.split(":");

                try{

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


                    String formattedDate = sdf.format(c.getTime());
                    Date date1 = sdf.parse(separateddate[0]+"-"+separateddate[1]+"-"+separateddate[2]);
                    Date date2 = sdf.parse(formattedDate);

                    System.out.println(sdf.format(date1));
                    System.out.println(sdf.format(date2));

                    if(date1.compareTo(date2)>0){
                        System.out.println("Date1 is after Date2");
                        callback.bookId(mResvationlist.get(i).bookingId,i);
                    }else if(date1.compareTo(date2)<0){
                        System.out.println("Date1 is before Date2");

                        callback.bookId(mResvationlist.get(i).bookingId,i);

                    }else if(date1.compareTo(date2)==0){

                        System.out.println("separateddate[0]==>>"+separateddate[0]);
                        System.out.println("separateddate[1]==>>"+separateddate[1]);
                        System.out.println("separateddate[2]==>>"+separateddate[2]);

                        String creationdate = feedItem.getRevdate()+" "+feedItem.getRevtime();
                        String result = creationdate.toString().replaceAll("[-,]","/");



                        SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                        formattedDate = df.format(c.getTime());




                        try {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                            // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                            date1 = simpleDateFormat.parse(separateddate[2]+"/"+separateddate[1]+"/"+separateddate[0]+" "+splitdaytimetime[0]+":"+splitdaytimetime[1]+":"+splitdaytimetime[2]);
                            date2 = simpleDateFormat.parse(formattedDate);



                           if( printDifference(date2, date1))
                           {
                               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                       context);

                               // set title
                               alertDialogBuilder.setTitle("Cancel Reservation");

                               // set dialog message
                               alertDialogBuilder
                                       .setMessage("You can't cancel Right now  if you want to cancel this reservation then you have to pay cancelation charge!")
                                       .setCancelable(false)
                                       .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog,int id) {

                                               callback.bookId(mResvationlist.get(i).bookingId,i);
                                               dialog.cancel();
                                           }
                                       })
                                       .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int id) {

                                               dialog.cancel();
                                           }
                                       });

                               // create alert dialog
                               AlertDialog alertDialog = alertDialogBuilder.create();

                               // show it
                               alertDialog.show();
                           }else{
                               callback.bookId(mResvationlist.get(i).bookingId,i);
                           }



                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                    }else{
                        System.out.println("How to get here?");
                        callback.bookId(mResvationlist.get(i).bookingId,i);
                    }

                }catch(ParseException ex){
                    ex.printStackTrace();
                }



            }

        });

        }

    @Override
    public int getItemCount() {
        return (null != mResvationlist ? mResvationlist.size() : 0);


    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView destinations, bokkingTime, status,booking_id,pickaddress;
        public Button mreservation;

        public CustomViewHolder(View view) {
            super(view);
            bokkingTime = (TextView) itemView.findViewById(R.id.booking_time);
            booking_id = (TextView) itemView.findViewById(R.id.booking_id);
            pickaddress = (TextView) itemView.findViewById(R.id.txt_sourceaddress);
            destinations = (TextView) itemView.findViewById(R.id.txt_destinationaddress);
            mreservation = (Button) itemView.findViewById(R.id.btn_reservationcancel);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface BookingId{
        public void bookId(String bookId,int position);
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public boolean printDifference(Date startDate, Date endDate){

        boolean returncancelflag=false;

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        if(elapsedHours<=3)
        {



            returncancelflag= true;
        }


        return returncancelflag;

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









