package com.huyanh.base.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.huyanh.base.BaseApplication;
import com.huyanh.base.custominterface.PopupListener;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.Log;

import java.util.ArrayList;
import java.util.Random;

public class Popup {
    private Context context;

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

        return false;
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

        return false;
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
        return false;
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
