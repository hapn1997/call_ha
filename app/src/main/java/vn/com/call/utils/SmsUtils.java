package vn.com.call.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.klinker.android.send_message.Transaction;

import java.util.List;

import vn.com.call.db.ConversationSim;
import vn.com.call.db.Settings;

/**
 * Created by ngson on 01/08/2017.
 */

public class SmsUtils {
    public static void sendSms(Context context, String text, long conversationId, String[] numbers) {
        Settings settingsApp = Settings.getInstance(context);

        com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
        sendSettings.setDeliveryReports(true);
        sendSettings.setSendLongAsMms(false);
        sendSettings.setGroup(false);
        sendSettings.setUseSystemSending(true);
        sendSettings.setStripUnicode(settingsApp.isUseSimpleCharacters());

        if (isMultiSim(context)) sendSettings.setSubscriptionId(getSubcriptionId(context, conversationId));

        Transaction sendTransaction = new Transaction(context, sendSettings);
        if (!settingsApp.enableSmsDeliveryReport()) {
            sendTransaction.setExplicitBroadcastForDeliveredSms(new Intent("disable_delivery_report"));
        }

        com.klinker.android.send_message.Message message = new com.klinker.android.send_message.Message(text, numbers);
        sendTransaction.sendNewMessage(message, conversationId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static boolean isMultiSim(Context context) {
        if (Build.VERSION.SDK_INT >= 22) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(context);

            return subscriptionManager.getActiveSubscriptionInfoCount() > 1;
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private static int getSubcriptionId(Context context, long threadId) {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);

        ConversationSim db = new ConversationSim(context);
        String sim = db.getSim(threadId);

        try {
            if (sim == null && !isMultiSim(context))
                return subscriptionManager.getActiveSubscriptionInfoList().get(0).getSubscriptionId();
            else {
                List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
                for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {
                    if (subscriptionInfo.getIccId().equals(sim))
                        return subscriptionInfo.getSubscriptionId();
                }

                return subscriptionManager.getActiveSubscriptionInfoList().get(0).getSubscriptionId();
            }
        } finally {
            db.close();
        }
    }
}
