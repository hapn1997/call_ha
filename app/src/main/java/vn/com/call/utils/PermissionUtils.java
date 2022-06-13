package vn.com.call.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngson on 30/06/2017.
 */

public class PermissionUtils {

    //public static final String PERMISSION_SMS = Manifest.permission.SEND_SMS;
    public static final String PERMISSION_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String PERMISSION_PHONE = Manifest.permission.WRITE_CALL_LOG;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;


    public static String[] getPermissionsNeedGrantAtIntro(Context context) {
        List<String> permissions = new ArrayList<>();

//        if (ContextCompat.checkSelfPermission(context, PERMISSION_SMS) != PackageManager.PERMISSION_GRANTED)
//            permissions.add(PERMISSION_SMS);
        if (ContextCompat.checkSelfPermission(context, PERMISSION_PHONE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(PERMISSION_PHONE);
        if (ContextCompat.checkSelfPermission(context, PERMISSION_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            permissions.add(PERMISSION_CONTACTS);
        if (ContextCompat.checkSelfPermission(context, PERMISSION_READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            permissions.add(PERMISSION_READ_CONTACTS);
        if (ContextCompat.checkSelfPermission(context, PERMISSION_READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(PERMISSION_READ_PHONE_STATE);
        if (ContextCompat.checkSelfPermission(context, PERMISSION_CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(PERMISSION_CALL_PHONE);
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
//            permissions.add(Manifest.permission.READ_SMS);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_CALL_LOG);

        return permissions.toArray(new String[]{});
    }

    public static boolean isAllPermissionGrantedAtIntro(Context context) {
        return
//        ContextCompat.checkSelfPermission(context, PERMISSION_SMS) == PackageManager.PERMISSION_GRANTED
                 ContextCompat.checkSelfPermission(context, PERMISSION_PHONE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, PERMISSION_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, PERMISSION_READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, PERMISSION_READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, PERMISSION_CALL_PHONE) == PackageManager.PERMISSION_GRANTED
               // && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;

    }
}
