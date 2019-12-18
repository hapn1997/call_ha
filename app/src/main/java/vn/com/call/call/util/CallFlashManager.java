package vn.com.call.call.util;

import android.content.Context;

import com.google.gson.Gson;

import vn.com.call.call.model.CallFlashItem;
import vn.com.call.utils.SharedPreferencesGlobalUtil;

import static vn.com.call.global.Constant.KEY_ENABLE_CALL_FLASH;
import static vn.com.call.global.Constant.KEY_ITEM_CALL_FLASH;

public class CallFlashManager {

    public static void saveItemCallFlash(Context context, CallFlashItem item) {
        Gson gson = new Gson();
        String str = gson.toJson(item);
        SharedPreferencesGlobalUtil.setValue(context, KEY_ITEM_CALL_FLASH, str);
    }

    public static CallFlashItem getItemCallFlash(Context context) {
        try {
            Gson gson = new Gson();
            String str = SharedPreferencesGlobalUtil.getValue(context, KEY_ITEM_CALL_FLASH);
            CallFlashItem item = gson.fromJson(str, CallFlashItem.class);
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isEnableCallFlash(Context context) {
        try {
            String str = SharedPreferencesGlobalUtil.getValue(context, KEY_ENABLE_CALL_FLASH);
            boolean isOn = Boolean.parseBoolean(str);
            return isOn;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setEnableCallFlash(Context context, boolean isEnable) {
        SharedPreferencesGlobalUtil.setValue(context, KEY_ENABLE_CALL_FLASH, String.valueOf(isEnable));
    }

}
