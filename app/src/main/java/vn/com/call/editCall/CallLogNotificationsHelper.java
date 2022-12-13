package vn.com.call.editCall;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallLogNotificationsHelper {
    NewCallsQuery newCallsQuery;
    private static CallLogNotificationsHelper sInstance;
    private Instants instants;
    public CallLogNotificationsHelper(NewCallsQuery newCallsQuery) {
        this.newCallsQuery = newCallsQuery;

    }

    public interface NewCallsQuery {
        List<NewCall> query(Integer num);
    }
    public static final class NewCall {
        private final String accountComponentName;
        private final String accountId;
        private final Uri callsUri;
        private final String countryIso;
        private final long dateMs;
        private final String number;
        private final int numberPresentation;
        private final String transcription;
        private final Uri voicemailUri;

        public NewCall(Uri uri, Uri uri2, String number, int numberPresentation, String accountComponentName, String accountId, String transcription, String countryIso, long dateMs) {

            this.callsUri = uri;
            this.voicemailUri = uri2;
            this.number = number;
            this.numberPresentation = numberPresentation;
            this.accountComponentName = accountComponentName;
            this.accountId = accountId;
            this.transcription = transcription;
            this.countryIso = countryIso;
            this.dateMs = dateMs;
        }

        public final String getAccountComponentName() {
            return this.accountComponentName;
        }

        public final String getAccountId() {
            return this.accountId;
        }

        public final Uri getCallsUri() {
            return this.callsUri;
        }

        public final String getCountryIso() {
            return this.countryIso;
        }

        public final long getDateMs() {
            return this.dateMs;
        }

        public final String getNumber() {
            return this.number;
        }

        public final int getNumberPresentation() {
            return this.numberPresentation;
        }

        public final String getTranscription() {
            return this.transcription;
        }

        public final Uri getVoicemailUri() {
            return this.voicemailUri;
        }
    }
    public final List<NewCall> getNewMissedCalls() {
        return this.newCallsQuery.query(3);
    }

    public static final class Instants {
        private Instants() {
        }


        private static final NewCallsQuery createNewCallsQuery(Context context, ContentResolver contentResolver) {
            return new DefaultNewCallsQuery(context, contentResolver);
        }

        public static final CallLogNotificationsHelper getInstance(Context context) {
            if (CallLogNotificationsHelper.sInstance == null) {
                ContentResolver contentResolver = context.getContentResolver();
                CallLogNotificationsHelper.sInstance = new CallLogNotificationsHelper(createNewCallsQuery(context, contentResolver));
            }
            return CallLogNotificationsHelper.sInstance;
        }

        public static final void removeMissedCallNotifications(Context context) {
            SharedPreferences sharedPreferences;
            if (context != null) {
                try {
                    TelecomUtils.cancelMissedCallsNotification(context);
                } catch (Exception unused) {
                    return;
                }
            }
            SharedPreferences.Editor editor = null;
            if (!(context == null || (sharedPreferences = context.getSharedPreferences(Constants.MAIN_PREFS, 0)) == null)) {
                editor = sharedPreferences.edit();
            }
            if (editor != null) {
                editor.putInt("MissedCount", 0);
            }
            if (editor != null) {
                editor.apply();
            }
        }
    }
    public static final class DefaultNewCallsQuery implements NewCallsQuery {
        private final ContentResolver mContentResolver;
        private final Context mContext;

        public DefaultNewCallsQuery(Context context, ContentResolver contentResolver) {
            this.mContext = context;
            this.mContentResolver = contentResolver;
        }

        private final NewCall createNewCallsFromCursor(Cursor cursor) {
            String string = cursor.getString(2);
            Uri withAppendedId = ContentUris.withAppendedId(CallLog.Calls.CONTENT_URI_WITH_VOICEMAIL, cursor.getLong(0));
            Uri parse = string == null ? null : Uri.parse(string);
            String string2 = cursor.getString(1);
            int i6 = cursor.getInt(3);
            String string3 = cursor.getString(4);
            String string4 = cursor.getString(5);
            String string5 = cursor.getString(6);
            String string6 = cursor.getString(7);
            return new NewCall(withAppendedId, parse, string2, i6, string3, string4, string5, string6, cursor.getLong(8));
        }

        @Override
        public List<NewCall> query(Integer num) {
            if ( ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED && num != null) {
                String format = String.format("%s = 1 AND %s = ?", Arrays.copyOf(new Object[]{"new", "type"}, 2));
                String[] strArr = {num.toString()};
                String[] strArr2 = {"_id", "number", "voicemail_uri", "presentation", "subscription_component_name", "subscription_id", "transcription", "countryiso", "date"};
                try {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Cursor query = this.mContentResolver.query(CallLog.Calls.CONTENT_URI_WITH_VOICEMAIL, strArr2, format, strArr, "date DESC");
                        if (query == null) {
                           query.close();
                            return null;
                        }
                        ArrayList arrayList = new ArrayList();
                        while (query.moveToNext()) {
                            arrayList.add(createNewCallsFromCursor(query));
                        }
                       query.close();
                        return arrayList;
                    }
                } catch (RuntimeException unused) {
                }
            }
            return null;
        }
    }



}
