package vn.com.call.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.dialer.ios.iphone.contacts.R;


/**
 * Created by ngson on 29/06/2017.
 */

public class Settings {
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String REQUESTED_PERMISSION_INTRO = "requested_permission_intro";
    private static final String SMS_DELIVERY_REPORT = "sms_delivery_report";
    private static final String USE_SIMPLE_CHARACTERS = "use_simple_characters";
    private static final String CURRENT_CONVERSATION = "current_conversation";
    private static final String NOTIFICATION = "notification";
    private static final String VIBRATE = "vibrate";
    private static final String SOUND = "sound";
    private static final String LIGHT = "light";
    private static final String COLOR = "color";
    private static final String PLAY_SOUND_OUTGOING_MESSAGE = "sound_outgoing_message";
    private static final String CHAT_HEADS = "chat_heads";
    private static final String USE_DEVICE_LANGUAGE = "device_language";
    private static final String LANGUAGE = "language";
    private static final String LOCKAPP = "lockapp";
    private static final String DEFAULT_COLOR = "default_color_conversation";
    private static final String TEXT_SIZE_CONVERSATION = "text_size_conversation";
    private static final String CHATHEAD_SIZE = "chathead_size";
    private static final String PRIVACY_MODE = "privacy_mode";
    private static final String PUSH_NOTIFICATION_WHEN_SEND_SCHEDULE_MESSAGE = "push_notification_when_send_schedule_message";
    private static final String DONT_SHOW_CHATHEAD_ICON = "dont_show_chathead_icon";

    private static SharedPreferences sSharedPreferences;
    private static Settings sSettings;
    private static SharedPreferences.Editor sEditor;

    private Context mContext;

    public enum FontSize {
        SMALL {
            @Override
            public int getFontSizeResourceId() {
                return R.dimen.text_size_sms_16;
            }

            @Override
            public int getLabelResourceId() {
                return R.string.text_size_small;
            }
        },
        NORMAL {
            @Override
            public int getFontSizeResourceId() {
                return R.dimen.text_size_sms_18;
            }

            @Override
            public int getLabelResourceId() {
                return R.string.text_size_normal;
            }
        },
        LARGE {
            @Override
            public int getFontSizeResourceId() {
                return R.dimen.text_size_sms_20;
            }

            @Override
            public int getLabelResourceId() {
                return R.string.text_size_large;
            }
        },
        EXTRA_LARGE {
            @Override
            public int getFontSizeResourceId() {
                return R.dimen.text_size_sms_22;
            }

            @Override
            public int getLabelResourceId() {
                return R.string.text_size_extra_large;
            }
        };

        public abstract int getFontSizeResourceId();
        public abstract int getLabelResourceId();
    }

    public enum ChatheadSize {
        SMALL {
            @Override
            public int getLabelResourceId() {
                return R.string.text_size_small;
            }

            @Override
            public int getSizeChathead() {
                return R.dimen.size_icon_chathead_small;
            }
        },
        NORMAL {
            @Override
            public int getLabelResourceId() {
                return R.string.text_size_normal;
            }

            @Override
            public int getSizeChathead() {
                return R.dimen.size_icon_chathead_normal;
            }
        },
        LARGE {
            @Override
            public int getLabelResourceId() {
                return R.string.text_size_large;
            }

            @Override
            public int getSizeChathead() {
                return R.dimen.size_icon_chathead_large;
            }
        },
        EXTRA_LARGE {
            @Override
            public int getLabelResourceId() {
                return R.string.text_size_extra_large;
            }

            @Override
            public int getSizeChathead() {
                return R.dimen.size_icon_chathead_extra_large;
            }
        };

        public abstract int getLabelResourceId();
        public abstract int getSizeChathead();
    }

    private Settings() {
    }

    public static synchronized Settings getInstance(Context context) {
        if (sSettings == null) {
            sSettings = new Settings();
            sSettings.setContext(context);
            sSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            sEditor = sSharedPreferences.edit();
        }

        return sSettings;
    }

    private void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean isFirstLaunch() {
        return sSharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        sEditor.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch);
        sEditor.apply();
    }

    public boolean isRequestedPermissionIntro() {
        return sSharedPreferences.getBoolean(REQUESTED_PERMISSION_INTRO, false);
    }

    public void setRequestedPermissionIntro(boolean requestedPermissionIntro) {
        sEditor.putBoolean(REQUESTED_PERMISSION_INTRO, requestedPermissionIntro);
        sEditor.apply();
    }

    public long getCurrentConversation() {
        return sSharedPreferences.getLong(CURRENT_CONVERSATION, -1l);
    }

    public void setCurrentConversation(long conversation) {
        sEditor.putLong(CURRENT_CONVERSATION, conversation);
        sEditor.apply();
    }

    public boolean enableSmsDeliveryReport() {
        return sSharedPreferences.getBoolean(SMS_DELIVERY_REPORT, true);
    }

    public void setSmsDeliveryReport(boolean smsDeliveryReport) {
        sEditor.putBoolean(SMS_DELIVERY_REPORT, smsDeliveryReport);
        sEditor.apply();
    }

    public boolean isUseSimpleCharacters() {
        return sSharedPreferences.getBoolean(USE_SIMPLE_CHARACTERS, false);
    }

    public void setUseSimpleCharacters(boolean useSimpleCharacters) {
        sEditor.putBoolean(USE_SIMPLE_CHARACTERS, useSimpleCharacters);
    }

    public boolean enableNotification() {
        return sSharedPreferences.getBoolean(NOTIFICATION, true);
    }

    public void setEnableNotification(boolean enableNotification) {
        sEditor.putBoolean(NOTIFICATION, enableNotification);
        sEditor.apply();
    }

    public boolean enableVibrate() {
        return sSharedPreferences.getBoolean(VIBRATE, true);
    }

    public void setEnableVibrate(boolean enableVibrate) {
        sEditor.putBoolean(VIBRATE, enableVibrate);
        sEditor.apply();
    }

    public boolean enableNotificationConversation(long threadId) {
        return sSharedPreferences.getBoolean(NOTIFICATION + threadId, true);
    }

    public void setEnableNotificationConversation(long threadId, boolean enableNotification) {
        sEditor.putBoolean(NOTIFICATION + threadId, enableNotification);
    }

    public boolean enableVibrate(long threadId) {
        return sSharedPreferences.getBoolean(VIBRATE + threadId, true);
    }

    public void setEnableVibrate(long threadId, boolean enableVibrate) {
        sEditor.putBoolean(VIBRATE + threadId, enableVibrate);
    }

    public String getSourceUri() {
        return sSharedPreferences.getString(SOUND, null);
    }

    public void setSourceUri(String sourceUri) {
        sEditor.putString(SOUND, sourceUri);
        sEditor.apply();
    }

    public String getSourceUri(long threadId) {
        return sSharedPreferences.getString(SOUND + threadId, null);
    }

    public void setSourceUri(@Nullable String threadId, String sourceUri) {
        sEditor.putString(threadId == null ? SOUND : SOUND + threadId, sourceUri);
    }

    public boolean enableLight() {
        return sSharedPreferences.getBoolean(LIGHT, true);
    }

    public void setEnableLight(boolean enableLight) {
        sEditor.putBoolean(LIGHT, enableLight);
        sEditor.apply();
    }

    public boolean enableLight(long threadId) {
        return sSharedPreferences.getBoolean(LIGHT + threadId, true);
    }

    public void setEnableLight(long threadId, boolean enableLight) {
        sEditor.putBoolean(LIGHT + threadId, enableLight);
        sEditor.apply();
    }

    public int getColor(long threadId, @Deprecated int defaultColor) {
        return sSharedPreferences.getInt(COLOR + threadId, getDefaultColorConversation(defaultColor));
    }

    public void setColor(long threadId, int color) {
        sEditor.putInt(COLOR + threadId, color);
    }

    public boolean isPlaySoundForOutgoingMessage() {
        return sSharedPreferences.getBoolean(PLAY_SOUND_OUTGOING_MESSAGE, true);
    }

    public void setPlaySoundOutgoingMessage(boolean playSoundOutgoingMessage) {
        sEditor.putBoolean(PLAY_SOUND_OUTGOING_MESSAGE, playSoundOutgoingMessage);
        sEditor.apply();
    }

    public boolean isEnableChatHeads() {
        return sSharedPreferences.getBoolean(CHAT_HEADS, true);
    }

    public void setEnableChatHeads(boolean enableChatHeads) {
        sEditor.putBoolean(CHAT_HEADS, enableChatHeads);
        sEditor.apply();
    }

    public boolean isUseDeviceLanguage() {
        return sSharedPreferences.getBoolean(USE_DEVICE_LANGUAGE, true);
    }

    public void setUseDeviceLanguage(boolean useDeviceLanguage) {
        sEditor.putBoolean(USE_DEVICE_LANGUAGE, useDeviceLanguage);
        sEditor.apply();
    }

    public String getLanguage() {
        return sSharedPreferences.getString(LANGUAGE, "en");
    }

    public void setLanguage(String language) {
        sEditor.putString(LANGUAGE, language);
        sEditor.apply();
    }

    public boolean enableLockApp() {
        return sSharedPreferences.getBoolean(LOCKAPP, false);
    }

    public void setEnableLockApp(boolean enableLockApp) {
        sEditor.putBoolean(LOCKAPP, enableLockApp);
        sEditor.apply();
    }

    public void setDefaultColorConversation(int color) {
        sEditor.putInt(DEFAULT_COLOR, color);
        sEditor.apply();
    }

    public int getDefaultColorConversation(int defaultColor) {
        return sSharedPreferences.getInt(DEFAULT_COLOR, defaultColor);
    }

    public FontSize getTextSizeConversation() {
        return FontSize.values()[sSharedPreferences.getInt(TEXT_SIZE_CONVERSATION, FontSize.SMALL.ordinal())];
    }

    public void setTextSizeConversation(FontSize fontSize) {
        sEditor.putInt(TEXT_SIZE_CONVERSATION, fontSize.ordinal());
        sEditor.apply();
    }

    public ChatheadSize getChatheadSize() {
        return ChatheadSize.values()[sSharedPreferences.getInt(CHATHEAD_SIZE, ChatheadSize.NORMAL.ordinal())];
    }

    public void setChatheadSize(ChatheadSize chatheadSize) {
        sEditor.putInt(CHATHEAD_SIZE, chatheadSize.ordinal());
        sEditor.apply();
    }

    public boolean isEnablePrivacyMode() {
        return sSharedPreferences.getBoolean(PRIVACY_MODE, false);
    }

    public void setPrivacyMode(boolean enablePrivacyMode) {
        sEditor.putBoolean(PRIVACY_MODE, enablePrivacyMode);
        sEditor.apply();
    }

    public boolean isPushNotificationWhenSentScheduleMessage() {
        return sSharedPreferences.getBoolean(PUSH_NOTIFICATION_WHEN_SEND_SCHEDULE_MESSAGE, false);
    }

    public void setPushNotificationWhenSentScheduleMessage(boolean push) {
        sEditor.putBoolean(PUSH_NOTIFICATION_WHEN_SEND_SCHEDULE_MESSAGE, push);
        sEditor.apply();
    }

    public boolean dontShowChatheadIcon() {
        return sSharedPreferences.getBoolean(DONT_SHOW_CHATHEAD_ICON, false);
    }

    public void setDontShowChatheadIcon(boolean dontShowChatheadIcon) {
        sEditor.putBoolean(DONT_SHOW_CHATHEAD_ICON, dontShowChatheadIcon);
        sEditor.apply();
    }
}
