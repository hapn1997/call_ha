package vn.com.call.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;

import java.util.List;

import com.dialer.ios.iphone.contacts.R;
import vn.com.call.adapter.listener.OnClickViewCallLogListener;
import vn.com.call.adapter.viewholder.CallLogViewHolder;
import vn.com.call.db.SharePref;
import vn.com.call.model.calllog.CallLog;
import vn.com.call.model.calllog.CallLogDetail;
import vn.com.call.model.contact.Contact;
import vn.com.call.ui.main.CallLogFragment;
import vn.com.call.ui.main.CallLogSectionEntity;
import vn.com.call.utils.TimeUtils;

/**
 * Created by ngson on 03/07/2017.
 */

public class CallLogAdapter extends BaseSectionQuickAdapter<CallLogSectionEntity, CallLogViewHolder> {
    private final static String TAG = CallLogAdapter.class.getSimpleName();

    private final int MAX_CALL_LOG_DETAIL = 3;
    public  CallLogFragment fragment;
    private OnClickViewCallLogListener listener;
    private boolean checklog;
    public CallLogAdapter(OnClickViewCallLogListener listener, List<CallLogSectionEntity> data, CallLogFragment fragment) {
        super(R.layout.item_call_log, R.layout.item_header_calllog, data);
      this.fragment = fragment;
        this.listener = listener;
    }
    public void enableSwipeToDeleteAndUndo1(CallLog callLog){
        callLog.enableSwipeToDeleteAndUndo(mContext,false,fragment);

    }
    @Override
    protected void convert(final CallLogViewHolder holder, CallLogSectionEntity item) {
        final CallLog callLog = item.t;
        enableSwipeToDeleteAndUndo1(callLog);
       // callLog.enableSwipeToDeleteAndUndo(mContext,false,fragment);
        holder.avatar.loadAvatar(callLog.getPhotoContact(), callLog.getNameContact(), callLog.getNumber());
        holder.name.setText(callLog.getNameContact() == null ? callLog.getNumber() : callLog.getNameContact());

        showCallLogDetail(holder, callLog.getDetails());

        holder.date.setText(TimeUtils.getTimeFormatCallLog(callLog.getDetails().get(0).getDate()));

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickCall(callLog.getNumber());
            }
        });

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onClickAvatar(callLog, holder.avatar);
//                listener.onClickInfo(callLog);
            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // listener.onClickAvatar(callLog, holder.avatar);
            }
        });

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(!checklog){
                     listener.onClickCall(callLog.getNumber());

                 }else {
                     listener.deleteCall(callLog,callLog.getNumber(),fragment);
                     notifyDataSetChanged();
                 }
            }
        });
        holder.img.setVisibility(checklog ==true? View.VISIBLE : View.GONE);


    }
    public void checkRemoveLog(boolean check){
        checklog = check;
        notifyDataSetChanged();


    }
    public boolean check(){
        notifyDataSetChanged();
        Log.d("fdfdffsd", String.valueOf(checklog));
        return this.checklog;


    }


    @Override
    protected void convertHead(CallLogViewHolder helper, CallLogSectionEntity item) {
        helper.setText(R.id.header, item.header);
    }

    private void showCallLogDetail(CallLogViewHolder holder, List<CallLogDetail> details) {
        int maxCallDetailLog = details.size() > MAX_CALL_LOG_DETAIL ? MAX_CALL_LOG_DETAIL : details.size();
        holder.typeLog.removeAllViews();

        for (int i = 0; i < maxCallDetailLog; i++) {
            ImageView callLogView = new ImageView(holder.typeLog.getContext());
            callLogView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            CallLogDetail detail = details.get(i);
            if (detail.getType() == android.provider.CallLog.Calls.INCOMING_TYPE)
                callLogView.setImageResource(R.drawable.ic_outgoing_call);
            else if (detail.getType() == android.provider.CallLog.Calls.OUTGOING_TYPE)
                callLogView.setImageResource(R.drawable.ic_outgoing_call);
            else {

                // callLogView.setImageResource(R.drawable.ic_call_received_red_700_18dp);
            }

             holder.name.setTextColor((detail.getType()==android.provider.CallLog.Calls.INCOMING_TYPE ||detail.getType()==android.provider.CallLog.Calls.OUTGOING_TYPE)  ? Color.BLACK  : Color.RED);
//             holder.date.setTextColor((detail.getType()==android.provider.CallLog.Calls.INCOMING_TYPE ||detail.getType()==android.provider.CallLog.Calls.OUTGOING_TYPE)  ? Color.BLACK  : Color.RED);
//             holder.location.setTextColor((detail.getType()==android.provider.CallLog.Calls.INCOMING_TYPE ||detail.getType()==android.provider.CallLog.Calls.OUTGOING_TYPE)  ? Color.BLACK  : Color.RED);
            holder.typeLog.addView(callLogView);
        }

        if (details.size() > MAX_CALL_LOG_DETAIL) {
            TextView numberCallLog = new TextView(holder.typeLog.getContext());
            numberCallLog.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            numberCallLog.setText("(" + details.size() + ")");
            holder.typeLog.addView(numberCallLog);
        }
    }
}
