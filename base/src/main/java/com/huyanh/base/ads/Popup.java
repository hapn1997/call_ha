package com.huyanh.base.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.huyanh.base.BaseApplication;
import com.huyanh.base.custominterface.PopupListener;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;

import java.util.ArrayList;
import java.util.Random;

public class Popup {
    private Context context;

    private InterstitialAd mInterstitialAdmob;
    private com.facebook.ads.InterstitialAd mInterstitialFacebook;
    private AdRequest admobRequest;

    private BaseApplication baseApplication;

    private Object tempObject = null;

    public Object getTempObject() {
        return tempObject;
    }

    public Popup(Context context) {
        this.context = context.getApplicationContext();
        this.baseApplication = (BaseApplication) context.getApplicationContext();
        loadAds();
    }

    private void loadAds() {
        if (baseApplication.getBaseConfig().getAds_network_new().getPopup().equals("admob")) {
            mInterstitialAdmob = new InterstitialAd(context);
            mInterstitialAdmob.setAdUnitId(baseApplication.getBaseConfig().getKey().getAdmob().getPopup());
            mInterstitialAdmob.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    mInterstitialAdmob.loadAd(admobRequest);
                    for (PopupListener popupListener : listPopupListener) {
                        if (popupListener != null) popupListener.onClose(tempObject);
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("loadded popup admob.");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    Log.e("error load popup admob: " + errorCode);
                }

                @Override
                public void onAdLeftApplication() {
                    baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, System.currentTimeMillis());
                    baseApplication.editor.apply();
                    super.onAdLeftApplication();
                }
            });

            if (BaseConstant.isDebugging) {
                String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                String deviceId = BaseUtils.md5(android_id).toUpperCase();
                admobRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice(deviceId)
                        .build();
            } else {
                admobRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();
            }
            mInterstitialAdmob.loadAd(admobRequest);
        } else {
            mInterstitialFacebook = new com.facebook.ads.InterstitialAd(context, baseApplication.getBaseConfig().getKey().getFacebook().getPopup());
            mInterstitialFacebook.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    mInterstitialFacebook.loadAd();
                    for (PopupListener popupListener : listPopupListener) {
                        if (popupListener != null) popupListener.onClose(tempObject);
                    }
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e("error load popup facebook: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("loaded popup facebook");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, System.currentTimeMillis());
                    baseApplication.editor.apply();
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
            mInterstitialFacebook.loadAd();
        }
    }

    public boolean showPopup(Fragment fragment, Object object, boolean withOutCondition) {
        if (baseApplication.isPurchase)
            return false;
        tempObject = object;
        if (!withOutCondition) {
            long temp_time_now = System.currentTimeMillis();
            if (temp_time_now - baseApplication.pref.getLong(BaseConstant.KEY_TIME_START_APP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getTime_start_show_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian start");
                return false;
            }
            if (temp_time_now
                    - baseApplication.pref.getLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getOffset_time_show_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian show before");
                return false;
            }
            if (temp_time_now
                    - baseApplication.pref.getLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getTime_hidden_to_click_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian click before");
                return false;
            }
        }
        if (new Random().nextInt(100) < baseApplication.getBaseConfig().getThumnail_config().getRandom_show_popup_hdv() && baseApplication.getBaseConfig().getMore_apps().size() > 0) {
            try {
                Log.d("show popup custom");
                fragment.startActivityForResult(new Intent(fragment.getActivity(), PopupCustom.class), BaseConstant.REQUEST_SHOW_POPUP_CUSTOM);
                baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
                baseApplication.editor.apply();
                return true;
            } catch (Exception e) {
                Log.e("error start popup custom: " + e.getMessage());
                return false;
            }
        }

        if (baseApplication.getBaseConfig().getAds_network_new().getPopup().equals("admob")) {
            Log.d("show popup admob: " + baseApplication.getBaseConfig().getKey().getAdmob().getPopup());
            if (mInterstitialAdmob.isLoaded()) {
                mInterstitialAdmob.show();
                baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
                baseApplication.editor.apply();
                return true;
            } else {
                mInterstitialAdmob.loadAd(admobRequest);
                return false;
            }
        }

        Log.d("show popup facebook: " + baseApplication.getBaseConfig().getKey().getFacebook().getPopup());
        if (mInterstitialFacebook.isAdLoaded()) {
            mInterstitialFacebook.show();
            baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
            baseApplication.editor.apply();
            return true;
        } else {
            mInterstitialFacebook.loadAd();
            return false;
        }
    }

    public boolean showPopup(Activity activity, Object object, boolean withOutCondition) {
        if (baseApplication.isPurchase)
            return false;
        tempObject = object;
        if (!withOutCondition) {
            long temp_time_now = System.currentTimeMillis();
            if (temp_time_now - baseApplication.pref.getLong(BaseConstant.KEY_TIME_START_APP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getTime_start_show_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian start");
                return false;
            }
            if (temp_time_now
                    - baseApplication.pref.getLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getOffset_time_show_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian show before");
                return false;
            }
            if (temp_time_now
                    - baseApplication.pref.getLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getTime_hidden_to_click_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian click before");
                return false;
            }
        }
        if (new Random().nextInt(100) < baseApplication.getBaseConfig().getThumnail_config().getRandom_show_popup_hdv() && baseApplication.getBaseConfig().getMore_apps().size() > 0) {
            try {
                Log.d("show popup custom");
                activity.startActivityForResult(new Intent(activity, PopupCustom.class), BaseConstant.REQUEST_SHOW_POPUP_CUSTOM);
                baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
                baseApplication.editor.apply();
                return true;
            } catch (Exception e) {
                Log.e("error start popup custom: " + e.getMessage());
                return false;
            }
        }

        if (baseApplication.getBaseConfig().getAds_network_new().getPopup().equals("admob")) {
            Log.d("show popup admob: " + baseApplication.getBaseConfig().getKey().getAdmob().getPopup());
            if (mInterstitialAdmob.isLoaded()) {
                mInterstitialAdmob.show();
                baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
                baseApplication.editor.apply();
                return true;
            } else {
                mInterstitialAdmob.loadAd(admobRequest);
                return false;
            }
        }

        Log.d("show popup facebook: " + baseApplication.getBaseConfig().getKey().getFacebook().getPopup());
        if (mInterstitialFacebook.isAdLoaded()) {
            mInterstitialFacebook.show();
            baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
            baseApplication.editor.apply();
            return true;
        } else {
            mInterstitialFacebook.loadAd();
            return false;
        }
    }


    public boolean showPopup(Object object, boolean withOutCondition, boolean noPopupCustom) {
        if (baseApplication.isPurchase)
            return false;
        tempObject = object;
        if (!withOutCondition) {
            long temp_time_now = System.currentTimeMillis();
            if (temp_time_now - baseApplication.pref.getLong(BaseConstant.KEY_TIME_START_APP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getTime_start_show_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian start");
                return false;
            }
            if (temp_time_now
                    - baseApplication.pref.getLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getOffset_time_show_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian show before");
                return false;
            }
            if (temp_time_now
                    - baseApplication.pref.getLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, 0) < (baseApplication.getBaseConfig().getConfig_ads().getTime_hidden_to_click_popup()
                    * 1000)) {
                Log.d("Chua du thoi gian click before");
                return false;
            }
        }
        if (baseApplication.getBaseConfig().getAds_network_new().getPopup().equals("admob")) {
            Log.d("show popup admob: " + baseApplication.getBaseConfig().getKey().getAdmob().getPopup());
            if (mInterstitialAdmob.isLoaded()) {
                mInterstitialAdmob.show();
                baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
                baseApplication.editor.apply();
                return true;
            } else {
                mInterstitialAdmob.loadAd(admobRequest);
                return false;
            }
        }

        Log.d("show popup facebook: " + baseApplication.getBaseConfig().getKey().getFacebook().getPopup());
        if (mInterstitialFacebook.isAdLoaded()) {
            mInterstitialFacebook.show();
            baseApplication.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, System.currentTimeMillis());
            baseApplication.editor.apply();
            return true;
        } else {
            mInterstitialFacebook.loadAd();
            return false;
        }
    }


    private ArrayList<PopupListener> listPopupListener = new ArrayList<>();

    public void addPopupListtener(PopupListener popupListener) {
        listPopupListener.add(popupListener);
    }

    public void removePopupListener(PopupListener popupListener) {
        if (listPopupListener.contains(popupListener)) listPopupListener.remove(popupListener);
    }

    public void removeAllPopupListener() {
        listPopupListener.clear();
    }
}
