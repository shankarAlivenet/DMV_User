package com.alivenet.dmvtaxi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.pojo.DropLocationResource;

import java.util.ArrayList;

import constant.MyApplication;

/**
 * Created by narendra on 8/5/2016.
 */
public class PickUpLocationRecyclerViewAdapter  extends RecyclerView.Adapter<PickUpLocationRecyclerViewAdapter.PickUpViewHolder> {

    public static ArrayList<DropLocationResource> mRideList;
    private Context context;
    private SparseBooleanArray selectedItems;

    public PickUpLocationRecyclerViewAdapter(Context context, ArrayList<DropLocationResource> mRideList) {
        this.mRideList = mRideList;
        this.context = context;

    }


    @Override
    public PickUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_location, null);
        PickUpViewHolder rcv = new PickUpViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final PickUpViewHolder holder, final int position) {

        holder.txtpickupaddress.setText(mRideList.get(position).getShort_address());
    }

    @Override
    public int getItemCount() {

        return this.mRideList.size();
    }


    class PickUpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;
        public TextView txtpickupaddress;


        int position;
        public PickUpViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            view.setOnClickListener(this);
            txtpickupaddress = (TextView) itemView.findViewById(R.id.txt_pickup_location);
        }


        @Override
        public void onClick(View view) {
            position = getPosition();

            MyApplication.pickuplocationslat=mRideList.get(position).getLatitude();
            MyApplication.pickuplocationslong=mRideList.get(position).getLongitude();

        }

    }


}



