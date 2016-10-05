package com.alivenet.dmvtaxi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.CarInfoDialog;
import com.alivenet.dmvtaxi.pojo.CabListResource;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by narendra on 7/9/2016.
 */
public class CabListRecyclerViewAdapter extends RecyclerView.Adapter<CabListRecyclerViewAdapter.CabListViewHolder> {

    public static ArrayList<CabListResource> mCabDetailsList;
    private Context context;
    private SparseBooleanArray selectedItems;
    maxuser callback;
    cabId callbackcabId;
    private int selectedIndex;
    private int selectedColor = Color.parseColor("#ffffff");
    public CabListRecyclerViewAdapter(Context context, ArrayList<CabListResource> mCabDetailsList, maxuser callback,cabId callbackcabId) {
        this.mCabDetailsList = mCabDetailsList;
        this.context = context;
        this.callback=callback;
        this.callbackcabId=callbackcabId;
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public CabListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_buttons, null);
        CabListViewHolder rcv = new CabListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final CabListViewHolder holder, final int position) {

        if(mCabDetailsList!=null)
        {
            //holder.myBackground.setSelected(selectedItems.get(position, false));
            holder.cabName.setText(mCabDetailsList.get(position).getCabName());
            Picasso.with(context).load(WebserviceUrlClass.ImageUrl+mCabDetailsList.get(position).getCabIcon()).into(holder.cabIcon);
            // holder.users_loacations.setVisibility(View.GONE);
            String cabId=mCabDetailsList.get(0).getmId();
            callbackcabId.cabId(cabId);
        }


        if(selectedIndex!= -1 && position == selectedIndex)
        {
            holder.myBackground.setCardBackgroundColor(Color.parseColor("#ab030a"));
            //holder.myBackground.setBackgroundColor(Color.parseColor("#ab030a"));
            String baseFare = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getBaseFare();
            String baseFareDistance = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getBaseFareDistance();
            String perKm = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getPerKm();
            String waitingCharge = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getWaitingCharge();
            if(mCabDetailsList!=null)
            {
                callback.maxusercount(mCabDetailsList.get(position).getMaxUsers(),baseFare,baseFareDistance,perKm,waitingCharge);
            }

        }
        else
        {
           // holder.myBackground.setBackgroundColor(selectedColor);
            holder.myBackground.setCardBackgroundColor(selectedColor);
        }

    }

    @Override
    public int getItemCount() {
        return this.mCabDetailsList.size();
    }


    /////////////////view Holder class////////////////////
    class CabListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View oldSelection = null;
        public TextView cabName;
        public ImageView cabIcon;
        public CardView myBackground;
        int position;
        int oldposition;

        public CabListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            cabName = (TextView) itemView.findViewById(R.id.button_text);
            cabIcon = (ImageView) itemView.findViewById(R.id.button_icon);
            myBackground = (CardView) itemView.findViewById(R.id.card_view);
        }



        @Override
        public void onClick(View view) {
            position = getPosition();
            setSelectedIndex(position);
            String cabId=mCabDetailsList.get(position).getmId();
            callbackcabId.cabId(cabId);
           // myBackground.setCardBackgroundColor(Color.parseColor("#01ff90"));
            String carName = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getCabName();
            String baseFare = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getBaseFare();
            String baseFareDistance = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getBaseFareDistance();
            String perKm = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getPerKm();
            String waitingCharge = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getWaitingCharge();
            String note = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getNote();
            String iconUrl = CabListRecyclerViewAdapter.mCabDetailsList.get(position).getCabIcon();
            callback.maxusercount(mCabDetailsList.get(position).getMaxUsers(),baseFare,baseFareDistance,perKm,waitingCharge);
            new CarInfoDialog(context, carName, baseFare, baseFareDistance, perKm, waitingCharge, note, iconUrl).show();

        }

    }

    public interface maxuser{
        public void maxusercount(String maxusercounter,String baseFare,String baseFareDistance,String perKm,String waitingCharge);
    }


    public interface cabId{
        public void cabId(String cabId);
    }


}
