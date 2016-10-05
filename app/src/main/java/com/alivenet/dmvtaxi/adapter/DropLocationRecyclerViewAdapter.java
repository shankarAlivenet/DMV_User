package com.alivenet.dmvtaxi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.pojo.DropLocationResource;
import com.alivenet.dmvtaxi.pojo.PreSetLocationResource;

import java.util.ArrayList;

import constant.MyApplication;

/**
 * Created by narendra on 8/5/2016.
 */
public class DropLocationRecyclerViewAdapter extends RecyclerView.Adapter<DropLocationRecyclerViewAdapter.DropLocationViewHolder> {

    public static ArrayList<DropLocationResource> mRideList;
    private Context context;
    private SparseBooleanArray selectedItems;

    public DropLocationRecyclerViewAdapter(Context context, ArrayList<DropLocationResource> mRideList) {
        this.mRideList = mRideList;
        this.context = context;

    }


    @Override
    public DropLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_location, null);
        DropLocationViewHolder rcv = new DropLocationViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final DropLocationViewHolder holder, final int position) {

        holder.txtdropaddress.setText(mRideList.get(position).getShort_address());




    }

    @Override
    public int getItemCount() {

        return this.mRideList.size();
    }


    class DropLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;
        public TextView txtdropaddress;


        int position;
        public DropLocationViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            view.setOnClickListener(this);
            txtdropaddress = (TextView) itemView.findViewById(R.id.txt_drop_location);
        }


        @Override
        public void onClick(View view) {
            position = getPosition();

            MyApplication.droplocationslat=mRideList.get(position).getLatitude();
            MyApplication.droplocationslong=mRideList.get(position).getLongitude();



        }

    }


}



