package com.huyanh.base.tracking;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrackingReferrer {

    public static void checkSendReferrer(final Context context) {
        if (SharedPreferencesLibUtil.getValue(context, "isSendReferrer") != null && SharedPreferencesLibUtil.getValue(context, "isSendReferrer").equals("true"))
            return;

        final String referrer = SharedPreferencesLibUtil.getValue(context, "referrer");
        if (!TextUtils.isEmpty(referrer)) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        String deviceID = getDeviceId(context);
                        String versionAPP = getVersionAppName(context);
                        String versionOS = getVersionOS();
                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add("referrer", referrer)
                                .add("deviceID", deviceID)
                                .add("versionAPP", versionAPP)
                                .add("versionOS", versionOS)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://sdk.hdvietpro.com/android/apps/check_referer.php")
                                .post(formBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        String result = response.body().string();

                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = Boolean.parseBoolean(jsonObject.getString("status"));
                        if (status) {
                            SharedPreferencesLibUtil.setValue(context, "isSendReferrer", status + "");
                        }
                    } catch (Exception e) {
                    }
                }
            }.start();
        }
    }

    private static String getDeviceId(Context context) {
        try {
            String idDevice = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return idDevice;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String getVersionAppName(Context context) {
        String versionName = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e1) {
            versionName = "";
        }
        return versionName;
    }

    private static String getVersionOS() {
        return Build.VERSION.RELEASE + " SDK: "
                + Build.VERSION.SDK_INT;
    }

}
