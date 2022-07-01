package vn.com.call.adapter.listener;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.phone.thephone.call.dialer.R;
import vn.com.call.adapter.HorizontalContactAdapter;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.model.calllog.CallLog;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.ui.BaseActivity;
import vn.com.call.ui.CallLogDetailActivity;
import vn.com.call.ui.ContactDetailActivity;
import vn.com.call.ui.main.CallLogFragment;
import vn.com.call.ui.main.ContactFragment;
import vn.com.call.utils.CallUtils;


/**
 * Created by ngson on 17/08/2017.
 */

public class OnClickViewCallLogListener {
    private Context context;

    public OnClickViewCallLogListener(Context context) {
        this.context = context;
    }

    public void onClickCallLog(CallLog callLog) {
        goCallLogDetail(callLog);
    }

    public void onClickAvatar(CallLog callLog, View avatar) {
        Contact contact;
        Log.d("rerer","vfvfvfvfv000");

        if (callLog.getIdContact() == null) {
            contact = new Contact(null, null, null);
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            PhoneNumber phoneNumber = new PhoneNumber(callLog.getNumber(), "Mobile");
            phoneNumbers.add(phoneNumber);
            contact.setNumbers(phoneNumbers);
        } else contact = ContactCache.getContactById(callLog.getIdContact());

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

//            ContactDetailActivity.launch(activity, avatar, contact);
            ContactDetailActivity.launch(context, contact);

        } else {
            ContactDetailActivity.launch(context, contact);
        }
    }

    public void onClickAvatar(Activity activity, CallLog callLog, View avatar, int requestCode) {
        Contact contact;
        if (callLog.getIdContact() == null) {
            contact = new Contact(null, null, null);
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            PhoneNumber phoneNumber = new PhoneNumber(callLog.getNumber(), "Mobile");
            phoneNumbers.add(phoneNumber);
            contact.setNumbers(phoneNumbers);
        } else contact = ContactCache.getContactById(callLog.getIdContact());

        if (context instanceof Activity) {

            ContactDetailActivity.launch(activity, avatar, contact, requestCode);
          // ContactDetailActivity.launch(activity, avatar, contact);
        } else {
            ContactDetailActivity.launch(activity, contact, requestCode);
        }
    }

    public void onClickCall(String number) {
        CallUtils.makeCall(context, number);
    }

    public void onClickCall(Activity activity, String number, int requestCode) {
        CallUtils.makeCall(activity, number, requestCode);
    }

    public void onClickInfo(CallLog callLog) {
        goCallLogDetail(callLog);
    }

    public void goCallLogDetail(CallLog callLog) {

        Intent intent = new Intent(context, CallLogDetailActivity.class);
        intent.putExtra(CallLogDetailActivity.EXTRA_CALL_LOG, callLog);

        if (context instanceof Service) {
            intent.putExtra(BaseActivity.EXTRA_FROM_SERVICE, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }
    public void deleteCall(CallLog callLog, String number, CallLogFragment fragment){
        callLog.delete(context, true,false,fragment);
    }

    public void goCallLogDetail(Activity activity, CallLog callLog, int requestCode) {
        Intent intent = new Intent(context, CallLogDetailActivity.class);
        intent.putExtra(CallLogDetailActivity.EXTRA_CALL_LOG, callLog);

        if (context instanceof Service) {
            intent.putExtra(BaseActivity.EXTRA_FROM_SERVICE, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        activity.startActivityForResult(intent, requestCode);
    }

}
