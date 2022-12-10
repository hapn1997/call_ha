package vn.com.call.editCall;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import static vn.com.call.editCall.Constants.ACCEPT_DECLINE_NOTIFICATION_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.telecom.Call;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.phone.thephone.call.dialer.BuildConfig;
import com.phone.thephone.call.dialer.R;

import java.util.Arrays;

import vn.com.call.call.util.CallUtil;
import vn.com.call.db.ContactHelper;
import vn.com.call.ui.CallActivity;
import vn.com.call.utils.CallUtils;

public class NotificationUtils {

    public final static void createAcceptDeclineNotification(Call call, Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), (int) R.layout.layout_incoming_call_notification);

        int NOTIFICATIONID = 1234;
        // Uri sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.message_sent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
            String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
            assert notificationManager != null;

            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setSound(null, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
                mChannel.enableLights(true);
                mChannel.setLightColor(-65536);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                mChannel.setSound(sound, audioAttributes);
                notificationManager.createNotificationChannel(mChannel);
            }

            Call.Details details2 = call.getDetails();
            Uri handle5 = details2.getHandle() ;
            String contactNameEmptyIfNotAvailable = ContactHelper.getContactName(context, (details2 == null || (handle5) == null) ? null : handle5.getSchemeSpecificPart());
            Call.Details details3 = call.getDetails();
            Uri handle4 = details3.getHandle() ;


            String schemeSpecificPart = (details3 == null || (handle4) == null) ? null : handle4.getSchemeSpecificPart();
            Intent action = new Intent(context, NotificationActionService.class).setAction(Constants.NOTIFICATION_ACTION_DECLINE);
            Intent action2 = new Intent(context, NotificationActionService.class).setAction(Constants.NOTIFICATION_ACTION_ACCEPT);
            PendingIntent service = PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent service2 = PendingIntent.getService(context, 0, action2, PendingIntent.FLAG_UPDATE_CURRENT);



            remoteViews.setTextViewText(R.id.textViewContactNameNumber,
                    contactNameEmptyIfNotAvailable == null || contactNameEmptyIfNotAvailable.length() == 0 ? schemeSpecificPart : contactNameEmptyIfNotAvailable);


            remoteViews.setTextViewText(R.id.textViewContactDetail, ((handle5) == null) ? "" : handle5.getSchemeSpecificPart());


            Intent in = new Intent();
            in.setClass(context, CallActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            in.putExtra(TelecomUtils.OnIncomingCallReceived,TelecomUtils.OnIncomingCallReceived);
            // The PendingIntent to launch activity.
            PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                    in, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageViewAnswer, service2);
            remoteViews.setOnClickPendingIntent(R.id.imageViewDecline, service);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

            builder.setSmallIcon(R.drawable.logo)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(remoteViews)
                    .setDefaults(Notification.DEFAULT_ALL)
//                    .addAction(R.drawable.ic_call_end_gray_24dp, getActionText(context, R.string.decline, R.color.red), service)
//                    .addAction(R.drawable.ic_call_gray_24dp, getActionText(context, R.string.decline, R.color.green), service2)
                    .setContentIntent(activityPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(sound)
                    .setOngoing(true);
            android.app.Notification notification = builder.build();
            notification.flags =notification.flags | Notification.FLAG_INSISTENT | Notification.FLAG_ONGOING_EVENT;

            notificationManager.notify(ACCEPT_DECLINE_NOTIFICATION_ID, notification);
        }
    }
    private static final Spannable getActionText(Context context, int title, int color) {
        SpannableString spannableString = new SpannableString(context.getText(title));
        if (Build.VERSION.SDK_INT >= 25) {
            spannableString.setSpan(new ForegroundColorSpan(context.getColor(color)), 0, spannableString.length(), 0);
        }
        return spannableString;
    }
    public static void removeNotificationFromID(Context context, int i6) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancel(null, i6);
        notificationManager.cancelAll();
    }

}
