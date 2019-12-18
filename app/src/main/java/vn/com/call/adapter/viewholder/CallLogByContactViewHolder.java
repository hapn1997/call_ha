package vn.com.call.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import vn.com.call.R;

/**
 * Created by ngson on 13/07/2017.
 */

public class CallLogByContactViewHolder extends BaseViewHolder {
    public TextView number;
    public ImageView typeCallLog;
    public TextView date;
    public TextView duration;

    public CallLogByContactViewHolder(View itemView) {
        super(itemView);

        number = (TextView) itemView.findViewById(R.id.number);
        typeCallLog = (ImageView) itemView.findViewById(R.id.type_call_log);
        date = (TextView) itemView.findViewById(R.id.date);
        duration = (TextView) itemView.findViewById(R.id.duration);
    }
}
