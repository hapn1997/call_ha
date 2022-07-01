package vn.com.call.adapter;

import android.app.Activity;
import android.provider.CallLog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.call.App;
import com.phone.thephone.call.dialer.R;
import vn.com.call.model.calllog.CallLogDetail;
import vn.com.call.utils.TimeUtils;

/**
 * Created by ngson on 05/07/2017.
 */

public class CallLogDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CallLogDetail> details;

    private Activity activity;
    private App application;

    public CallLogDetailAdapter(Activity activity, List<CallLogDetail> details) {
        this.details = details;
        this.activity = activity;
        this.application = (App) activity.getApplication();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new CallLogDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log_detail, parent, false));
            case 1:
                return new BannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_alone, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                CallLogDetailViewHolder callLogDetailViewHolder = (CallLogDetailViewHolder) holder;

                int realPosition = position;
                if (position >= application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai()) {
                    realPosition = position - 1 - (position - (application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai() + 1)) / (application.getBaseConfig().getThumnail_config().getOffset_video_to_show_thumbai() + 1);
                }
                CallLogDetail detail = details.get(realPosition);

                if (detail.getType() == CallLog.Calls.INCOMING_TYPE) {
                    callLogDetailViewHolder.iconTypeCall.setImageResource(R.drawable.ic_call_received_primary_color_18dp);
                    callLogDetailViewHolder.titleTypeCall.setText(R.string.incoming);
                    callLogDetailViewHolder.durationCall.setVisibility(View.VISIBLE);
                } else if (detail.getType() == CallLog.Calls.OUTGOING_TYPE) {
                    callLogDetailViewHolder.iconTypeCall.setImageResource(R.drawable.ic_call_made_primary_color_18dp);
                    callLogDetailViewHolder.titleTypeCall.setText(R.string.outgoing);
                    callLogDetailViewHolder.durationCall.setVisibility(View.VISIBLE);
                } else {
                    callLogDetailViewHolder.iconTypeCall.setImageResource(R.drawable.ic_call_received_red_700_18dp);
                    callLogDetailViewHolder.titleTypeCall.setText(R.string.missed);
                    callLogDetailViewHolder.durationCall.setVisibility(View.GONE);
                }

                callLogDetailViewHolder.timeCall.setText(TimeUtils.getTimeFormatCallLogDetail(detail.getDate()));
                callLogDetailViewHolder.durationCall.setText(detail.getDuration() + " " + callLogDetailViewHolder.durationCall.getContext().getString(R.string.second));
                break;
            case 1:
                break;
        }

    }


    @Override
    public int getItemCount() {
//        Log.d("size default: " + details.size());
        int size;
        if (application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai() == 0) {
            size = details.size() + 1;
            size += (size - (application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai() + 1)) / application.getBaseConfig().getThumnail_config().getOffset_video_to_show_thumbai();
        } else {
            if ((details.size() / application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai()) >= 1) {
                size = details.size() + 1;
                size += (size - (application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai() + 1)) / application.getBaseConfig().getThumnail_config().getOffset_video_to_show_thumbai();
            } else
                size = details.size();
        }
//        Log.v("size after add banner: " + size);
        return size;
//        return details.size();
    }

    @Override
    public int getItemViewType(int position) {
        //1 la banner
        //0 là bình thường
        if ((position - application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai()) >= 0 && (position - application.getBaseConfig().getThumnail_config().getStart_video_show_thumbai()) % (application.getBaseConfig().getThumnail_config().getOffset_video_to_show_thumbai() + 1) == 0) {
            return 1;
        }
        return 0;
    }

    static class CallLogDetailViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon_type_call)
        ImageView iconTypeCall;
        @BindView(R.id.title_tyle_call)
        TextView titleTypeCall;
        @BindView(R.id.time)
        TextView timeCall;
        @BindView(R.id.duration)
        TextView durationCall;

        public CallLogDetailViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        public BannerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
