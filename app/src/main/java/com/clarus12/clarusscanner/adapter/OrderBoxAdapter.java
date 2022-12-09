package com.clarus12.clarusscanner.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.clarus12.clarusscanner.R;
import com.clarus12.clarusscanner.dto.OrderBoxResponseDto;

import java.util.ArrayList;


public class OrderBoxAdapter extends RecyclerView.Adapter<OrderBoxAdapter.ViewHolder> {
    private ArrayList<OrderBoxResponseDto> mList;
    private Context context;

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

    public OrderBoxAdapter(Context context, ArrayList<OrderBoxResponseDto> list) {
        this.mList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        // 아이템 클릭 이벤트 처리.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getBindingAdapterPosition();

                if (pos != 0 && pos != RecyclerView.NO_POSITION) {
                    OrderBoxResponseDto dto = mList.get(pos);
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("trackingNo", dto.getOverseasTrackingNo());
                    clipboardManager.setPrimaryClip(clip);
                    Toast.makeText(context, " 해외송장 " + dto.getOverseasTrackingNo() + " 복사되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        if (item.getLastShipStatusDate() == null) {
            item.setLastShipStatusDate("-");
        }
        viewholder.trackingNo.setText(item.getLocalTrackingNo() + "/\n"  + item.getOverseasTrackingNo());
        viewholder.container.setText(item.getContainerCode());
        viewholder.status.setText(item.getShipStatusName() + "/\n"  + item.getLastShipStatusDate());
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

    public void makeTitle() {
        OrderBoxResponseDto dto = new OrderBoxResponseDto();
        dto.setOrderBoxShortId("No");
        dto.setLocalTrackingNo("국내송장");
        dto.setOverseasTrackingNo("해외송장");
        dto.setContainerCode("컨테이너코드");
        dto.setShipStatusName("상태");
        dto.setLastShipStatusDate("날짜");
        mList.add(dto);
    }
}