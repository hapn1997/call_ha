package vn.com.call.db.cache;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import vn.com.call.db.ContactHelper;
import vn.com.call.model.calllog.CallLog;
import vn.com.call.model.calllog.CallLogDetail;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.utils.TimeUtils;


/**
 * Created by ngson on 03/07/2017.
 */

public class CallLogHelper {
    private final static String TAG = CallLogHelper.class.getSimpleName();

    private static final Uri CALL_LOG_URI = android.provider.CallLog.Calls.CONTENT_URI;

    public static Observable<List<CallLog>> queryAllCallLog(final Context context) {
        return Observable.create(new Observable.OnSubscribe<List<CallLog>>() {
            @Override
            public void call(Subscriber<? super List<CallLog>> subscriber) {
                Cursor c = context.getContentResolver().query(CALL_LOG_URI, null,
                        null, null, android.provider.CallLog.Calls.DATE + " DESC");

                List<CallLog> callLogs = new ArrayList<>();

                if (c != null) {
                    try {
                        String lastNumber = null;

                        while (c.moveToNext()) {
                            String number = c.getString(c.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                            long date = c.getLong(c.getColumnIndex(android.provider.CallLog.Calls.DATE));
                            int type = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                            int duration = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                            String id = c.getString(c.getColumnIndex(android.provider.CallLog.Calls._ID));
                            Log.d("đcdcdcdccd", String.valueOf(type));

                            if (lastNumber == null
                                    || lastNumber.equals(number)
                                    || !lastNumber.equals(number)
                                    || !TimeUtils.isSameDay(callLogs.get(callLogs.size() - 1).getDetails().get(0).getDate(), date)) {
                                lastNumber = number;

                                CallLog callLog = new CallLog(number);
                                CallLogDetail detail = new CallLogDetail(id, duration, type, date);
                                callLog.addCallLog(detail);
                                String[] dataContact = ContactHelper.retrieveContactPhotoUriAndDisplayName(context, number);
                                callLog.setIdContact(dataContact[0]);
                                callLog.setPhotoContact(dataContact[1]);
                                callLog.setNameContact(dataContact[2]);

                                callLogs.add(callLog);
                            } else {

                                CallLogDetail detail = new CallLogDetail(id, duration, type, date);
                                callLogs.get(callLogs.size() - 1).addCallLog(detail);

                            }
                        }
                    } finally {
                        c.close();
                    }
                }

                reverse(callLogs);

                CallLogCache.cacheCallLogs(callLogs);

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(callLogs);
                    subscriber.onCompleted();
                }
            }

            public void reverse(List<CallLog> callLogs) {
                if (isNeedReverse(callLogs)) Collections.reverse(callLogs);
            }

            public boolean isNeedReverse(List<CallLog> callLogs) {
                if (callLogs.size() >= 2) {
                    CallLog first = callLogs.get(0);
                    CallLog second = callLogs.get(1);

                    return first.getDetails().get(0).getDate() < second.getDetails().get(0).getDate();
                }

                return false;
            }
        });
    }
    public static Observable<List<CallLog>> queryMissCallLog(final Context context) {
        return Observable.create(new Observable.OnSubscribe<List<CallLog>>() {
            @Override
            public void call(Subscriber<? super List<CallLog>> subscriber) {
                Cursor c = context.getContentResolver().query(CALL_LOG_URI, null,
                        null, null, android.provider.CallLog.Calls.DATE + " DESC");

                List<CallLog> callLogs = new ArrayList<>();

                if (c != null) {
                    try {
                        String lastNumber = null;

                        while (c.moveToNext()) {
                            String number = c.getString(c.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                            long date = c.getLong(c.getColumnIndex(android.provider.CallLog.Calls.DATE));
                            int type = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                            int duration = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                            String id = c.getString(c.getColumnIndex(android.provider.CallLog.Calls._ID));

                            if (lastNumber == null
                                    || lastNumber.equals(number)
                                    || !lastNumber.equals(number)
                                    || !TimeUtils.isSameDay(callLogs.get(callLogs.size() - 1).getDetails().get(0).getDate(), date)) {
                                lastNumber = number;
                                      if(type ==3){
                                          CallLog callLog = new CallLog(number);
                                          CallLogDetail detail = new CallLogDetail(id, duration, type, date);
                                          callLog.addCallLog(detail);

                                          String[] dataContact = ContactHelper.retrieveContactPhotoUriAndDisplayName(context, number);
                                          callLog.setIdContact(dataContact[0]);
                                          callLog.setPhotoContact(dataContact[1]);
                                          callLog.setNameContact(dataContact[2]);

                                          callLogs.add(callLog);
                                      }

                            } else {

                                CallLogDetail detail = new CallLogDetail(id, duration, type, date);
                                callLogs.get(callLogs.size() - 1).addCallLog(detail);

                            }
                        }
                    } finally {
                        c.close();
                    }
                }

                reverse(callLogs);

                CallLogCache.cacheCallLogs(callLogs);

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(callLogs);
                    subscriber.onCompleted();
                }
            }

            public void reverse(List<CallLog> callLogs) {
                if (isNeedReverse(callLogs)) Collections.reverse(callLogs);
            }

            public boolean isNeedReverse(List<CallLog> callLogs) {
                if (callLogs.size() >= 2) {
                    CallLog first = callLogs.get(0);
                    CallLog second = callLogs.get(1);

                    return first.getDetails().get(0).getDate() < second.getDetails().get(0).getDate();
                }

                return false;
            }
        });
    }

    public static Observable<Void> deleteCallLog(final Context context, final List<CallLogDetail> details) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                for (CallLogDetail detail : details) {
                    deleteCallLog(context, detail);
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static void deleteCallLog(Context context, CallLogDetail detail) {
        context.getContentResolver().delete(CALL_LOG_URI, android.provider.CallLog.Calls.DATE + "=?",
                new String[] {Long.toString(detail.getDate())});
    }
    public static Observable<Void> deleteAllCallLog12(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {

                    deleteAllCallLog1(context);


                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }
        });
    }


    public static void deleteAllCallLog1(Context context) {
        context.getContentResolver().delete(CALL_LOG_URI, null,
                null);
    }

    public static Observable<List<CallLogDetail>> getCallLogDetailByContact(final Context context, final Contact contact) {
        return Observable.create(new Observable.OnSubscribe<List<CallLogDetail>>() {
            @Override
            public void call(Subscriber<? super List<CallLogDetail>> subscriber) {

                List<CallLogDetail> callLogDetails = new ArrayList<>();

                List<PhoneNumber> phoneNumbers = contact.getNumbers();
                if (phoneNumbers != null && phoneNumbers.size() > 0) {
                    StringBuilder selection = new StringBuilder();

                    int size = phoneNumbers.size();

                    for (int i = 0;i < size;i++) {
                        String phoneNumber = phoneNumbers.get(i).getNumber();
                        phoneNumber = phoneNumber.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");

                        if (phoneNumber.startsWith("0"))
                            phoneNumber = phoneNumber.substring(1, phoneNumber.length());

                        selection.append(i == 0 ? android.provider.CallLog.Calls.NUMBER + " LIKE '%" + phoneNumber + "'"
                                : " OR " + android.provider.CallLog.Calls.NUMBER + " LIKE '%" + phoneNumber + "'");
                    }

                    Cursor c = context.getContentResolver().query(
                            CALL_LOG_URI,
                            null,
                            selection.toString(),
                            null,
                            android.provider.CallLog.Calls.DATE + " DESC");

                    try {
                        while (c.moveToNext()) {
                            String number = c.getString(c.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                            long date = c.getLong(c.getColumnIndex(android.provider.CallLog.Calls.DATE));
                            int type = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                            int duration = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                            String id = c.getString(c.getColumnIndex(android.provider.CallLog.Calls._ID));

                            CallLogDetail callLogDetail = new CallLogDetail(id, duration, type, date);
                            callLogDetail.setNumber(number);

                            callLogDetails.add(callLogDetail);
                        }
                    } finally {
                        c.close();
                    }
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(callLogDetails);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<List<CallLogDetail>> getCallLogDetailByNumber(final Context context, final String number) {
        return Observable.create(new Observable.OnSubscribe<List<CallLogDetail>>() {
            @Override
            public void call(Subscriber<? super List<CallLogDetail>> subscriber) {
                Cursor c = context.getContentResolver().query(CALL_LOG_URI, null, android.provider.CallLog.Calls.NUMBER + "=?", new String[]{number}, null);

                List<CallLogDetail> callLogDetails = new ArrayList<>();

                try {
                    while (c.moveToNext()) {
                        String number = c.getString(c.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                        long date = c.getLong(c.getColumnIndex(android.provider.CallLog.Calls.DATE));
                        int type = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                        int duration = c.getInt(c.getColumnIndex(android.provider.CallLog.Calls.DURATION));
                        String id = c.getString(c.getColumnIndex(android.provider.CallLog.Calls._ID));

                        CallLogDetail callLogDetail = new CallLogDetail(id, duration, type, date);
                        callLogDetail.setNumber(number);

                        callLogDetails.add(callLogDetail);
                    }
                } finally {
                    c.close();
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(callLogDetails);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
