package vn.com.call.adapter;

import android.provider.CallLog;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;


import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import vn.com.call.R;
import vn.com.call.adapter.viewholder.CallLogByContactViewHolder;
import vn.com.call.model.calllog.CallLogDetail;
import vn.com.call.utils.TimeUtils;


/**
 * Created by ngson on 13/07/2017.
 */

public class CallLogByContactAdapter extends BaseQuickAdapter<CallLogDetail, CallLogByContactViewHolder> {
    public CallLogByContactAdapter(@LayoutRes int layoutResId, @Nullable List<CallLogDetail> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(CallLogByContactViewHolder holder, CallLogDetail callLogDetail) {

        holder.number.setText(callLogDetail.getNumber());
        holder.date.setText(TimeUtils.getTimeFormatCallLogDetail(callLogDetail.getDate()));
        holder.duration.setText(callLogDetail.getDuration() + " " + holder.duration.getContext().getString(R.string.second));

        if (callLogDetail.getType() == CallLog.Calls.INCOMING_TYPE) {
            holder.typeCallLog.setImageResource(R.drawable.ic_call_received_primary_color_18dp);
            holder.duration.setVisibility(View.VISIBLE);
        } else if (callLogDetail.getType() == CallLog.Calls.OUTGOING_TYPE) {
            holder.typeCallLog.setImageResource(R.drawable.ic_call_made_primary_color_18dp);
            holder.duration.setVisibility(View.VISIBLE);
        } else {
            holder.typeCallLog.setImageResource(R.drawable.ic_call_received_red_700_18dp);
            holder.duration.setVisibility(View.GONE);
        }
    }
}
