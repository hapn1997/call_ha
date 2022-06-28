package vn.com.call.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {

    private static SharedPreferences sharedPreferences;

    public static final String preference_name = "Call";
    public static final String PERMISSTION = "permisstion";

    @SuppressLint("CommitPrefEdits")
    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();

    }

    public static String getKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");

    }

    public static boolean check(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.contains(Key);

    }
    public static void remove(Context context, String Key) {
        sharedPreferences = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Key);
        editor.apply();
    }
}
