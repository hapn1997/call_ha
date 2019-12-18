package vn.com.call.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.call.R;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.utils.CallUtils;

/**
 * Created by ngson on 18/08/2017.
 */

public class ChooseNumberDialogFragment extends BaseDialogFragment {
    public interface OnChooseNumberListener {
        void onChoosePhoneNumber(String number);
    }

    private static final String EXTEA_TITLE = "title";
    private static final String EXTRA_NUMBERS = "numbers";

    @BindView(R.id.title)
    TextView mTitleView;
    @BindView(R.id.numbers)
    ListView mListNumberView;

    private String mTitle;
    private List<PhoneNumber> mPhoneNumbers;

    private OnChooseNumberListener mOnChooseNumberListener;

    public static ChooseNumberDialogFragment newInstance(String title, List<PhoneNumber> phoneNumbers) {
        Bundle args = new Bundle();

        args.putString(EXTEA_TITLE, title);
        args.putParcelableArrayList(EXTRA_NUMBERS, (ArrayList<PhoneNumber>) phoneNumbers);

        ChooseNumberDialogFragment fragment = new ChooseNumberDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mTitle = args.getString(EXTEA_TITLE);
        mPhoneNumbers = getArguments().getParcelableArrayList(EXTRA_NUMBERS);

        if (mPhoneNumbers == null) mPhoneNumbers = new ArrayList<>();
    }

    public void setOnChooseNumberListener(OnChooseNumberListener listener) {
        mOnChooseNumberListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_choose_number_to_send_sms;
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {
        mTitleView.setText(mTitle);

        mListNumberView.setAdapter(new PhoneNumberAdapter(getContext(), mPhoneNumbers, mOnChooseNumberListener));
        mListNumberView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CallUtils.makeCall(getContext(), mPhoneNumbers.get(i).getNumber());
                Log.wtf("click", mPhoneNumbers.get(i).getNumber());
                dismiss();
            }
        });
    }

    static class PhoneNumberAdapter extends ArrayAdapter<PhoneNumber> {
        private OnChooseNumberListener onChooseNumberListener;

        public PhoneNumberAdapter(@NonNull Context context, List<PhoneNumber> phoneNumbers, OnChooseNumberListener onChooseNumberListener) {
            super(context, R.layout.sub_item_phone_number, phoneNumbers);
            this.onChooseNumberListener = onChooseNumberListener;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
            PhoneNumberViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item_phone_number, parent, false);

                holder = new PhoneNumberViewHolder(convertView);
                convertView.setTag(holder);
            } else holder = (PhoneNumberViewHolder) convertView.getTag();

            final PhoneNumber phoneNumber = getItem(position);

            holder.number.setText(phoneNumber.getNumber());
            holder.typeNumber.setText(phoneNumber.getType());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onChooseNumberListener != null) onChooseNumberListener.onChoosePhoneNumber(phoneNumber.getNumber());
                }
            });

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
