package com.huyanh.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connectivity {
//    public static NetworkInfo getNetworkInfo(Context context) {
//        try {
//            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static boolean isConnected(Context context) {
//        NetworkInfo info = getNetworkInfo(context);
//        return info != null && info.isConnected();
//    }
//
//    public static boolean isConnectedWifi(Context context) {
//        NetworkInfo info = getNetworkInfo(context);
//        if (info != null && info.isConnected() && info.getType() == 1) {
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isConnectedMobile(Context context) {
//        NetworkInfo info = getNetworkInfo(context);
//        return info != null && info.isConnected() && info.getType() == 0;
//    }
//
//    public static boolean isConnectedFast(Context context) {
//        NetworkInfo info = getNetworkInfo(context);
//        return info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype());
//    }
//
//    public static boolean isConnectionFast(int type, int subType) {
//        if (type == 1) {
//            return true;
//        }
//        if (type != 0) {
//            return false;
//        }
//        switch (subType) {
//            case 1 /* 1 */:
//                return false;
//            case 2 /* 2 */:
//                return false;
//            case 3 /* 3 */:
//            case 5 /* 5 */:
//            case 6 /* 6 */:
//            case 8 /* 8 */:
//            case 9 /* 9 */:
//            case 10 /* 10 */:
//            case 12 /* 12 */:
//            case 13 /* 13 */:
//            case 14 /* 14 */:
//            case 15 /* 15 */:
//                return true;
//            case 4 /* 4 */:
//                return false;
//            case 7 /* 7 */:
//                return false;
//            case 11 /* 11 */:
//                return false;
//            default:
//                return false;
//        }
//    }
}
