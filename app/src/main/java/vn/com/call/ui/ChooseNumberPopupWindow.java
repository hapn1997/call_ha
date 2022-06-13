package vn.com.call.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.dialer.ios.iphone.contacts.R;
import vn.com.call.adapter.listener.OnClickViewConversationListener;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.model.sms.Conversation;
import vn.com.call.ui.messenger_apps.ConversationActivity;
import vn.com.call.utils.CallUtils;

/**
 * Created by ngson on 05/09/2017.
 */

public class ChooseNumberPopupWindow extends PopupWindow {
    public enum Action {
        SEND_SMS, CALL
    }

    private Context mContext;
    private Contact mContact;
    private Action mAction;

    public ChooseNumberPopupWindow(Context context, Contact contact, Action action) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mContext = context;
        mContact = contact;
        mAction = action;
    }

    public void showPopup(View parent, final OnClickViewConversationListener onClickViewConversation) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.popup_window_choose_number, null);

        TextView title = contentView.findViewById(R.id.title);
        ListView numbers = contentView.findViewById(R.id.numbers);

        title.setText(mContext.getString(mAction == Action.CALL ? R.string.call : R.string.send_sms) + " - " + mContact.getName());
        numbers.setDividerHeight(0);
        numbers.setDivider(null);
        numbers.setAdapter(new PhoneNumberAdapter(mContext, mContact.getNumbers()));
        numbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = mContact.getNumbers().get(i).getNumber();

                if (mAction == Action.CALL) {
                    CallUtils.makeCall(mContext, number);
                } else {
                    Conversation conversation = new Conversation(mContext, new String[]{number});

                    if ((mContext instanceof Activity) || onClickViewConversation == null) {
                       //hasua ConversationActivity.launch(mContext, conversation);
                    } else onClickViewConversation.onClickConversation(conversation);
                }

                dismiss();
            }
        });

        contentView.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setContentView(contentView);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    static class PhoneNumberAdapter extends ArrayAdapter<PhoneNumber> {
        public PhoneNumberAdapter(@NonNull Context context, List<PhoneNumber> phoneNumbers) {
            super(context, R.layout.sub_item_phone_number, phoneNumbers);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
            PhoneNumberAdapter.PhoneNumberViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item_phone_number, parent, false);

                holder = new PhoneNumberAdapter.PhoneNumberViewHolder(convertView);
                convertView.setTag(holder);
            } else holder = (PhoneNumberAdapter.PhoneNumberViewHolder) convertView.getTag();

            final PhoneNumber phoneNumber = getItem(position);

            holder.number.setText(phoneNumber.getNumber());
            holder.typeNumber.setText(phoneNumber.getType());

            return convertView;
        }

        static class PhoneNumberViewHolder {
            @BindView(R.id.number)
            TextView number;
            @BindView(R.id.type_number)
            TextView typeNumber;
            @BindView(R.id.sms)
            View sms;

            public PhoneNumberViewHolder(View view) {
                ButterKnife.bind(this, view);

                sms.setVisibility(View.GONE);
            }
        }
    }
}
