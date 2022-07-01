package vn.com.call.adapter.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import com.phone.thephone.call.dialer.R;
import vn.com.call.widget.AvatarView;

/**
 * Created by ngson on 13/07/2017.
 */

public class CallLogViewHolder extends BaseViewHolder {
    public View root;
    public AvatarView avatar;
    public TextView name;
    public LinearLayout typeLog;
    public TextView date;
    public ImageButton call;
    public ImageButton info;
    public ImageView img;
    public TextView location;

    public CallLogViewHolder(View itemView) {
        super(itemView);
        location = itemView.findViewById(R.id.location);
        root = itemView.findViewById(R.id.root);
        avatar = (AvatarView) itemView.findViewById(R.id.avatar);
        name = (TextView) itemView.findViewById(R.id.name);
        typeLog = (LinearLayout) itemView.findViewById(R.id.type_log);
        date = (TextView) itemView.findViewById(R.id.date);
        call = (ImageButton) itemView.findViewById(R.id.call);
        info = (ImageButton) itemView.findViewById(R.id.info);
         img  = (ImageView) itemView.findViewById(R.id.imageremove);
    }
}