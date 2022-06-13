package vn.com.call.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;

import vn.com.call.db.RecentDb;


/**
 * Created by ngson on 07/07/2017.
 */

public class CallUtils {
    public static void makeCall(Context context, String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, getPhoneUri(number));

        if (!(context instanceof Activity)) callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(callIntent);
        RecentDb.upCounter(context, number);
    }

    public static void makeCall(Activity activity, String number, int requestCode) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, getPhoneUri(number));

        if (!(activity instanceof Activity))
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            context.startActivity(callIntent);
            return;
        }
        activity.startActivityForResult(callIntent, requestCode);
        RecentDb.upCounter(activity, number);
    }

    public static void goDialer(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);

        if (context instanceof Service) {
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //hasua
            //context.sendBroadcast(new Intent(TouchBarService.ACTION_DISMISS_TOUCHBAR));
        }

        context.startActivity(callIntent);
    }

    private static Uri getPhoneUri(String number) {
        StringBuilder builder = new StringBuilder("tel:");

        for (char c : number.toCharArray()) {

            if (c == '#')
                builder.append(Uri.encode("#"));
            else
                builder.append(c);
        }

        return Uri.parse(builder.toString());
    }
}
