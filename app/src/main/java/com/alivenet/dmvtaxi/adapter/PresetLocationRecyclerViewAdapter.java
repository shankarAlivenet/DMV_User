package com.alivenet.dmvtaxi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.pojo.PreSetLocationResource;

import java.util.ArrayList;

import constant.MyApplication;

/**
 * Created by narendra on 8/5/2016.
 */
public class PresetLocationRecyclerViewAdapter extends RecyclerView.Adapter<PresetLocationRecyclerViewAdapter.PresetLocationViewHolder> {

    public static ArrayList<PreSetLocationResource> mRideList;
    private Context context;
    private SparseBooleanArray selectedItems;

    public PresetLocationRecyclerViewAdapter(Context context, ArrayList<PreSetLocationResource> mRideList) {
        this.mRideList = mRideList;
        this.context = context;

    }


    @Override
    public PresetLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_location, null);
        PresetLocationViewHolder rcv = new PresetLocationViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final PresetLocationViewHolder holder, final int position) {

        holder.txtshortaddress.setText(mRideList.get(position).getShort_address());
    }

    @Override
    public int getItemCount() {

        return this.mRideList.size();
    }


    class PresetLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;
        public TextView txtshortaddress;


        int position;

        public PresetLocationViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Position" + mRideList.get(position).getLatitude(), Toast.LENGTH_SHORT).show();
                    MyApplication.frequentlocationset=true;
                    MyApplication.pickuploctiononetime=true;
                    MyApplication.pickuplocationslat=mRideList.get(position).getLatitude();
                    MyApplication.pickuplocationslong=mRideList.get(position).getLongitude();
                    MyApplication.google_address=mRideList.get(position).getGoogle_address();

                    Intent in = new Intent(context, DeashboardActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(in);

                }
            });
            txtshortaddress = (TextView) itemView.findViewById(R.id.txt_setshortlocation);

        }


        @Override
        public void onClick(View view) {
            position = getPosition();

        }

    }


}



