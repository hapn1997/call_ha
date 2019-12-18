package vn.com.call.adapter.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import vn.com.call.R;
import vn.com.call.widget.AvatarView;

/**
 * Created by ngson on 13/07/2017.
 */

public class ContactViewHolder extends BaseViewHolder {
    public View root;
    public AvatarView avatar;
    public TextView name;
    public TextView address;

    public View expand;
    public ImageButton call;
    public ImageButton sendMessage;
    public ImageButton goDetail;
    public ImageButton deleteContact;

    public ContactViewHolder(View view) {
        super(view);

        root = view.findViewById(R.id.root);
        avatar = view.findViewById(R.id.avatar);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        expand = view.findViewById(R.id.expand);
        call = view.findViewById(R.id.call);
        sendMessage = view.findViewById(R.id.sms);
        goDetail = view.findViewById(R.id.detail);
        deleteContact = view.findViewById(R.id.delete_contact);
    }
}