package com.huyanh.base.utils;

public class Log {
    public static boolean DEBUG = true;
    public static String TAG = "HuyAnh";

    public static void d(String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            android.util.Log.w(TAG, msg);
        }
    }

}
