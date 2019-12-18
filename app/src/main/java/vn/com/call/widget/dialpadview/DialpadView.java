package vn.com.call.widget.dialpadview;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;

import vn.com.call.R;
import vn.com.call.bus.DismissTouchbar;
import vn.com.call.model.contact.Contact;
import vn.com.call.utils.CallUtils;
import vn.com.call.utils.ScreenUtils;
import vn.com.call.utils.SmsUtils;


/**
 * Created by ngson on 11/09/2017.
 */

public class DialpadView extends FrameLayout implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    private View mDialpad;
    String contactname ;
    String contactNumber;
    private ImageButton mBackspace;
    private TextView mInputNumber;
    private ImageButton mCallSingleSim;
    private  TextView contactnametext;
    private LinearLayout mRow1;
    private LinearLayout mRow2;
    private LinearLayout mRow3;
    private LinearLayout mRow4;
    private TextView addnumber;
    private TextView threedot;
    private View mLayoutMultiSim;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
                onChangedSimState();
            }
        }
    };

    public DialpadView(@NonNull Context context) {

        super(context);

        init();
    }

    public DialpadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mDialpad = LayoutInflater.from(getContext()).inflate(R.layout.dialpad, this, false);
//         contactnametext = mDialpad.findViewById(R.id.contactname);
//
//        if(contactname.isEmpty()){
//            contactnametext.setText(contactname);
//        }
        //fragment.getLoaderManager().initLoader(0,null,this);
        mDialpad.findViewById(R.id.key_one).findViewById(R.id.dialpad_key_letters).setVisibility(View.INVISIBLE);
        View keyZero = mDialpad.findViewById(R.id.key_zero);
        View keystart = mDialpad.findViewById(R.id.key_star);
        View keypound = mDialpad.findViewById(R.id.key_pound);
        ((TextView) keyZero.findViewById(R.id.dialpad_key_number)).setText("0");
        ((TextView) keyZero.findViewById(R.id.dialpad_key_letters)).setText("+");
        ((TextView) keystart.findViewById(R.id.dialpad_key_letters)).setText("*");
        ((TextView) keypound.findViewById(R.id.dialpad_key_letters)).setText("#");

        int[] keyIds = new int[] {R.id.key_two, R.id.key_three, R.id.key_four, R.id.key_five, R.id.key_six, R.id.key_seven, R.id.key_eight, R.id.key_nine};
        int[] resourceIds = new int[] {R.string.key_two, R.string.key_three, R.string.key_four, R.string.key_five, R.string.key_six, R.string.key_seven, R.string.key_eight, R.string.key_nine};

        int size = keyIds.length;
        for (int i = 0;i < size;i++) {
            TextView letters = mDialpad.findViewById(keyIds[i]).findViewById(R.id.dialpad_key_letters);
            letters.setText(resourceIds[i]);

            TextView number = mDialpad.findViewById(keyIds[i]).findViewById(R.id.dialpad_key_number);
            number.setText("" + (i + 2));
        }
        threedot = mDialpad.findViewById(R.id.threedot);
        addnumber  =mDialpad.findViewById(R.id.addnumber);
        mInputNumber = mDialpad.findViewById(R.id.input_number);
        mBackspace = mDialpad.findViewById(R.id.backspace);
        mCallSingleSim = mDialpad.findViewById(R.id.call_single_sim);

        mLayoutMultiSim = mDialpad.findViewById(R.id.layout_multi_sim);

        mRow1 = mDialpad.findViewById(R.id.row1);
        mRow2 = mDialpad.findViewById(R.id.row2);
        mRow3 = mDialpad.findViewById(R.id.row3);
        mRow4 = mDialpad.findViewById(R.id.row4);

        mInputNumber.setRawInputType(InputType.TYPE_CLASS_PHONE);
        mInputNumber.setTextIsSelectable(true);
        mInputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mInputNumber.length()>13 && mInputNumber.length()<15){
                    mInputNumber.setTextSize(27);
                }else if(mInputNumber.length()>14 && mInputNumber.length()<16){
                    mInputNumber.setTextSize(26);
                }else if(mInputNumber.length()>15 && mInputNumber.length()<17){
                    mInputNumber.setTextSize(25);
                }else if(mInputNumber.length()>16 && mInputNumber.length()<18){
                    mInputNumber.setTextSize(24);
                }else if(mInputNumber.length()>17 && mInputNumber.length()<19){
                    mInputNumber.setTextSize(23);
                }else if(mInputNumber.length()>18 && mInputNumber.length()<20){
                    mInputNumber.setTextSize(22);
                }else if(mInputNumber.length()>19 && mInputNumber.length()<21){
                    mInputNumber.setTextSize(21);
                }
                if(mInputNumber.length()>20){
                    threedot.setVisibility(VISIBLE);
                }
                if(mInputNumber.length()<22){
                    threedot.setVisibility(GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mInputNumber.length() > 0) {
                    addnumber.setVisibility(VISIBLE);
                    mBackspace.setVisibility(View.VISIBLE);
                } else {
                    addnumber.setVisibility(GONE);
                    mBackspace.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        onChangedSimState();

        mBackspace.setOnClickListener(this);
        mDialpad.findViewById(R.id.addnumber).setOnClickListener(this);
        //mDialpad.findViewById(R.id.add_contact).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_zero).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_one).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_two).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_three).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_four).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_five).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_six).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_seven).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_eight).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_nine).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_star).setOnClickListener(this);
        mDialpad.findViewById(R.id.key_pound).setOnClickListener(this);
        mDialpad.findViewById(R.id.call_sim_1).setOnClickListener(this);
        mDialpad.findViewById(R.id.call_sim_2).setOnClickListener(this);
        mDialpad.findViewById(R.id.call_single_sim).setOnClickListener(this);

        addView(mDialpad);

        //mDialpad.setTranslationY(ScreenUtils.getHeightScreen(getContext()));

        IntentFilter intentFilter = new IntentFilter(ACTION_SIM_STATE_CHANGED);
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    public void fitParent(int parentHeight) {
        int childHeight = getHeight();

        int totalMargin = parentHeight - childHeight;

        setMarginRow(mRow1, totalMargin / 5);
        setMarginRow(mRow2, totalMargin / 5);
        setMarginRow(mRow3, totalMargin / 5);
        setMarginRow(mRow4, totalMargin / 5);
    }
    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0; // string.isEmpty() in Java 6
    }
    private void setMarginRow(LinearLayout row, int margin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) row.getLayoutParams();
        params.bottomMargin = margin;
        row.setLayoutParams(params);
    }

    public void showSetting(final Context context){
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.dialog_create_contact, null);
        final android.support.v7.app.AlertDialog deleteDialog = new android.support.v7.app.AlertDialog.Builder(context).create();


        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        deleteDialog.getWindow().setLayout(-1, -1);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.relative_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
        deleteDialogView.findViewById(R.id.txt_mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent12 = new Intent(Intent.ACTION_INSERT);
                intent12.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent12.putExtra(ContactsContract.Intents.Insert.PHONE,mInputNumber.getText().toString())
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

                context.startActivity(intent12);
                deleteDialog.dismiss();

            }
        });
        deleteDialogView.findViewById(R.id.txt_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                deleteDialog.dismiss();
            }
        });
        deleteDialogView.findViewById(R.id.txt_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });


        deleteDialog.show();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        String simSlotName[] = {
                "extra_asus_dial_use_dualsim",
                "com.android.phone.extra.slot",
                "slot",
                "simslot",
                "sim_slot",
                "subscription",
                "Subscription",
                "phone",
                "com.android.phone.DialingMode",
                "simSlot",
                "slot_id",
                "simId",
                "simnum",
                "phone_type",
                "slotId",
                "slotIdx"
        };

        switch (id) {
            case R.id.addnumber :
                showSetting(getContext());
            case R.id.backspace :
                mInputNumber.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                break;
//            case R.id.add_contact :
//
////                Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
////                intentInsertEdit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
////                intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, mInputNumber.getText().toString())
////                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
//
//               // getContext().startActivity(intentInsertEdit);
//
//                sendDismissTouchbar();
//                break;
            case R.id.key_one :
                mInputNumber.append("1");
                break;
            case R.id.key_two :
                mInputNumber.append("2");
                break;
            case R.id.key_three :
                mInputNumber.append("3");
                break;
            case R.id.key_four :
                mInputNumber.append("4");
                break;
            case R.id.key_five :
                mInputNumber.append("5");
                break;
            case R.id.key_six :
                mInputNumber.append("6");
                break;
            case R.id.key_seven :
                mInputNumber.append("7");
                break;
            case R.id.key_eight :
                mInputNumber.append("8");
                break;
            case R.id.key_nine :
                mInputNumber.append("9");
                break;
            case R.id.key_zero :
                mInputNumber.append("0");
                break;
            case R.id.key_star :
                mInputNumber.append("*");
                break;
            case R.id.key_pound :
                mInputNumber.append("#");
                break;
            case R.id.call_sim_1 :
                if (Build.VERSION.SDK_INT >= 22 && isValidNumber()) {
                    Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(getPhoneUri());
                    intent.putExtra("com.android.phone.force.slot", true);
                    intent.putExtra("Cdma_Supp", true);

                    for (String s : simSlotName)
                        intent.putExtra(s, 0);

                    getContext().startActivity(intent);

                    sendDismissTouchbar();
                }

                break;
            case R.id.call_sim_2 :
                if (Build.VERSION.SDK_INT >= 22 && isValidNumber()) {
                    Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(getPhoneUri());
                    intent.putExtra("com.android.phone.force.slot", true);
                    intent.putExtra("Cdma_Supp", true);

                    for (String s : simSlotName)
                        intent.putExtra(s, 1);

                    getContext().startActivity(intent);

                    sendDismissTouchbar();
                }

                break;
            case R.id.call_single_sim :
                if (isValidNumber()) {
                    CallUtils.makeCall(getContext(), mInputNumber.getText().toString());

                    sendDismissTouchbar();
                }

                break;

            default:
                mInputNumber.append("");
                break;
        }
    }

    private boolean isValidNumber() {
        return mInputNumber.getText().toString().trim().length() > 0;
    }

    public boolean isOpen() {
        return mDialpad.getTranslationY() == 0;
    }

    public void open() {
        setVisibility(View.VISIBLE);
        mDialpad.animate().translationY(0);
    }

    public void close() {
        mDialpad.animate().translationY(ScreenUtils.getHeightScreen(getContext())).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //setVisibility(View.GONE);
                mInputNumber.setText("");
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public boolean isMultiSim() {
        if (Build.VERSION.SDK_INT >= 22) {
            return SmsUtils.isMultiSim(getContext());
        } else return false;
    }

    private Uri getPhoneUri() {
        StringBuilder builder = new StringBuilder("tel:");

        String number = mInputNumber.getText().toString();

        for(char c : number.toCharArray()) {

            if(c == '#')
                builder.append(Uri.encode("#"));
            else
                builder.append(c);
        }

        return Uri.parse(builder.toString());
    }

    public void onChangedSimState() {
        if (isMultiSim()) {
            mLayoutMultiSim.setVisibility(View.VISIBLE);
            mCallSingleSim.setVisibility(View.GONE);
        } else {
            mLayoutMultiSim.setVisibility(View.GONE);
            mCallSingleSim.setVisibility(View.VISIBLE);
        }
    }

    private void sendDismissTouchbar() {
        EventBus.getDefault().post(new DismissTouchbar());
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mReceiver);
        super.onDetachedFromWindow();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, mInputNumber.getText().toString());

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER};
        return new CursorLoader(getContext(),uri,projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

       if(cursor.moveToFirst()){
            contactname = cursor.getString(0);
           contactNumber = cursor.getString(1);
       }else {
               contactname = null;
               contactNumber = null;
       }
//        if(contactnametext!= null){
//            this.contactnametext.setText(contactname);
//        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
