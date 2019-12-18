package vn.com.call.adapter.listener;

import android.app.Activity;
import android.content.Context;

import vn.com.call.model.sms.Conversation;


/**
 * Created by ngson on 24/08/2017.
 */

public class OnClickViewConversationListener {
    private Context context;

    public OnClickViewConversationListener(Context context) {
        this.context = context;
    }

    public void onClickConversation(Conversation conversation) {
        //ConversationActivity.launch(context, conversation);
    }

    public void onClickConversation(Activity activity, Conversation conversation, int requestCode) {
       // ConversationActivity.launch(activity, conversation, requestCode);
    }
}
