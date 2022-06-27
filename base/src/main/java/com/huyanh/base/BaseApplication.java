package com.huyanh.base;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.huyanh.base.ads.OtherAppLauncher;
import com.huyanh.base.ads.Popup;
import com.huyanh.base.dao.BaseConfig;
import com.huyanh.base.dao.BaseTypeface;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class BaseApplication extends Application {
    private OkHttpClient okHttpClient;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public Gson gson;

    private BaseTypeface baseTypeface;
    private BaseConfig baseConfig = new BaseConfig();

    private Popup popup;

    public boolean isPurchase = BuildConfig.DEBUG ? true : false;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);

        new File(getFilesDir().getPath() + "/txt/").mkdirs();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        gson = new Gson();
        FirebaseAnalytics.getInstance(getApplicationContext());

        BaseUtils.setDateInstall(getApplicationContext());

        initBaseConfigFromFile();

        loadData();
    }

    public Popup getPopup() {
        return popup;
    }

    public BaseConfig getBaseConfig() {
        return baseConfig;
    }

    private class LoadData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String resultIpInfo = "null";
//            try {
//                Request request = new Request.Builder()
//                        .url("https://ipinfo.io/json")
//                        .build();
//                okhttp3.Response response = getOkHttpClient().newCall(request).execute();
//                if (response.isSuccessful()) {
//                    resultIpInfo = response.body().string();
//                    JSONObject jsonObject = new JSONObject(resultIpInfo);
//                    BaseUtils.setCountry(getApplicationContext(), jsonObject.getString("country"));
//                }
//            } catch (Exception e) {
//                Log.e("error progress ipInfo: " + e.getMessage());
//            }

            String version_manifest = "0";
            try {
                version_manifest = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode + "";
            } catch (PackageManager.NameNotFoundException e) {
            }

//            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
//                    "global_lib", Context.MODE_PRIVATE);
//            String referrer = sharedPreferences.getString("referrer", null);

            String url = BaseConstant.URL_REQUEST + "?code=" + getResources().getString(R.string.code_app) + "&date_install="
                    + BaseUtils.getDateInstall(getApplicationContext()) + "&version=" + version_manifest + "&deviceID="
                    + BaseUtils.getDeviceID(getApplicationContext()) + "&country="
                    + BaseUtils.getCountry(getApplicationContext()) + "&referrer=null&ipInfo=" + resultIpInfo;
            Log.v("url base: " + url);
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                okhttp3.Response response = getOkHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    baseConfig = gson.fromJson(result, BaseConfig.class);

                    if (BaseConstant.isDebugging) {
                        baseConfig.getConfig_ads().setTime_start_show_popup(0);
                        baseConfig.getConfig_ads().setOffset_time_show_popup(5);

                        baseConfig.getThumnail_config().setStart_video_show_thumbai(3);
                        baseConfig.getThumnail_config().setOffset_video_to_show_thumbai(4);
                    }

                    if (baseConfig != null) {
                        baseConfig.initOtherApps(getApplicationContext());
                        BaseUtils.writeTxtFile(new File(getApplicationContext().getFilesDir().getPath() + "/txt/base.txt"), result);
                        return null;
                    }
                }
            } catch (Exception e) {
                Log.e("error request base: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MobileAds.initialize(getApplicationContext(), baseConfig.getKey().getAdmob().getAppid());
            popup = new Popup(getApplicationContext());
//            new addShorcut().execute();
        }
    }


//    private class addShorcut extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d("onPreExecute add shortcut: " + getBaseConfig().getShortcut_dynamic().size());
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            for (BaseConfig.shortcut_dynamic shortcut_dynamic : getBaseConfig().getShortcut_dynamic()) {
//                if (pref.getBoolean("pref_" + shortcut_dynamic.getId(), false) && shortcut_dynamic.getLoop() == 0)
//                    continue;
//                int size = (int) getResources().getDimension(android.R.dimen.app_icon_size);
//                try {
//                    URL url = new URL(shortcut_dynamic.getIcon());
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoInput(true);
//                    connection.connect();
//                    InputStream input = connection.getInputStream();
//                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(input), size, size, true);
//                    Intent shortcutIntent = new Intent(getApplicationContext(), OtherAppLauncher.class);
//                    shortcutIntent.setAction(Intent.ACTION_MAIN);
//                    shortcutIntent.putExtra("link", shortcut_dynamic.getLink());
//                    shortcutIntent.putExtra("package_name", shortcut_dynamic.getPackage_name());
//                    Intent addIntent = new Intent();
//                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcut_dynamic.getName_shotcut());
//                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
//                    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//                    addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
//                    getApplicationContext().sendBroadcast(addIntent);
//                    editor.putBoolean("pref_" + shortcut_dynamic.getId(), true);
//                    editor.commit();
//                } catch (IOException e) {
//                }
//            }
//            return null;
//        }
//    }

    private void initBaseConfigFromFile() {
        try {
            File fileBase = new File(getApplicationContext().getFilesDir().getPath() + "/txt/base.txt");
            if (fileBase.exists()) {
                Log.d("base file in sdcard");
                baseConfig = gson.fromJson(BaseUtils.readTxtFile(fileBase), BaseConfig.class);
            } else {
                Log.d("base file in assets");
                baseConfig = gson.fromJson(BaseUtils.readFileFromAsset(getApplicationContext(), "base.txt"), BaseConfig.class);
            }
            baseConfig.initOtherApps(getApplicationContext());
            Log.v("content baseConfig: " + gson.toJson(baseConfig));
        } catch (Exception e) {
            Log.e("error init data base file: " + e.getMessage());
        }
    }

    public BaseTypeface getBaseTypeface() {
        if (baseTypeface == null) baseTypeface = new BaseTypeface(this);
        return baseTypeface;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(BaseConstant.REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(BaseConstant.REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(BaseConstant.REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    public void loadData() {
        editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, 0);
        editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, 0);
        editor.putLong(BaseConstant.KEY_TIME_START_APP, System.currentTimeMillis());
        editor.apply();
        new LoadData().execute();
    }

}
