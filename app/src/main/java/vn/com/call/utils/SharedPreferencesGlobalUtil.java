package vn.com.call.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import vn.com.call.global.Constant;


public class SharedPreferencesGlobalUtil {

    public static String getValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.SHARED_PREFERENCES_GLOBAL,
                Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    public static void setValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.SHARED_PREFERENCES_GLOBAL,
                Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.commit();
    }
}
