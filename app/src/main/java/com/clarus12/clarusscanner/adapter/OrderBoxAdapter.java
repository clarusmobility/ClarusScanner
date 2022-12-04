package com.clarus12.clarusscanner.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clarus12.clarusscanner.R;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;

import java.util.ArrayList;


public class OrderBoxAdapter extends RecyclerView.Adapter<OrderBoxAdapter.ViewHolder> {
    private ArrayList<OrderBoxResponseDto> mList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView id;
        protected TextView trackingNo;
        protected TextView container;
        protected TextView status;

        public ViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.id_listitem);
            this.trackingNo = (TextView) view.findViewById(R.id.trackingno_listitem);
            this.container = (TextView) view.findViewById(R.id.container_listitem);
            this.status = (TextView) view.findViewById(R.id.status_listitem);
        }
    }

    public OrderBoxAdapter(ArrayList<OrderBoxResponseDto> list) {
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int position) {

        viewholder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.trackingNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        viewholder.id.setGravity(Gravity.CENTER);
        viewholder.trackingNo.setGravity(Gravity.CENTER);
        viewholder.status.setGravity(Gravity.CENTER);

        OrderBoxResponseDto item = mList.get(position) ;

        viewholder.id.setText(item.getOrderBoxShortId());
        if (item.getLocalTrackingNo() == null) {
            item.setLocalTrackingNo("-");
        }
        if (item.getOverseasTrackingNo() == null) {
            item.setOverseasTrackingNo("-");
        }
        viewholder.trackingNo.setText(item.getLocalTrackingNo() + "/\n"  + item.getOverseasTrackingNo());
        viewholder.container.setText(item.getContainerCode());
        viewholder.status.setText(item.getShipStatusName());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public void removeAt(int position) {
        if (mList.size() > 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size());
        }
    }
}