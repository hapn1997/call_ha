package vn.com.call.db.cache;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;
import rx.Observable;
import rx.Subscriber;
import vn.com.call.model.calllog.CallLog;

/**
 * Created by ngson on 12/07/2017.
 */

public class CallLogCache {
    private static final String BOOK_CALL_LOG = "call_log";

    private static final String KEY_CACHE_CALL_LOG = "calllogs";

    private static Book getBook() {
        return Paper.book(BOOK_CALL_LOG);
    }

    public static List<CallLog> getCallLogs() {
        return getBook().read(KEY_CACHE_CALL_LOG, new ArrayList<CallLog>());
    }

    public static Observable<List<CallLog>> getCallLogsAsync() {
        return Observable.create(new Observable.OnSubscribe<List<CallLog>>() {
            @Override
            public void call(Subscriber<? super List<CallLog>> subscriber) {
                List<CallLog> callLogs = getCallLogs();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(callLogs);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static void cacheCallLogs(List<CallLog> callLogs) {
        getBook().write(KEY_CACHE_CALL_LOG, callLogs);
    }

    public static Observable<List<CallLog>> findCalllogByKeyword(final String keyword) {
        return Observable.create(new Observable.OnSubscribe<List<CallLog>>() {
            @Override
            public void call(Subscriber<? super List<CallLog>> subscriber) {
                List<CallLog> callLogs = getCallLogs();

                List<CallLog> results = new ArrayList<>();
                for (CallLog callLog : callLogs) {
                    if (callLog.getNameContact() != null && callLog.getNameContact().toLowerCase().contains(keyword.toLowerCase())) results.add(callLog);
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(results);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
