package vn.com.call.db.cache;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by ngson on 19/07/2017.
 */

public class RecipientIdsCache {
    public static class RecipientId {
        public String id;
        public String address;

        public RecipientId(String id, String address) {
            this.id = id;
            this.address = address;
        }
    }

    private final static String TAG = RecipientIdsCache.class.getSimpleName();

    private final static String BOOK = "recipient_ids";

    private final static String KEY_CACHE_RECIPIENT_IDS = "recipient_ids";

    private final static List<RecipientId> sRecipientIds = new ArrayList<>();

    private final static Object lock = new Object();

    public static void init(final Context context) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                initSync(context);
            }
        }).subscribeOn(Schedulers.newThread())
        .subscribe();
    }

    public static void initSync(Context context) {
        synchronized (lock) {
            Cursor c = context.getContentResolver().query(Uri.parse("content://mms-sms/canonical-addresses"), null, null, null, null);

            if (c != null) {
                try {
                    List<RecipientId> recipientIds = new ArrayList<>();

                    while (c.moveToNext()) {
                        String id = c.getString(c.getColumnIndex("_id"));
                        String address = c.getString(c.getColumnIndex("address"));

                        recipientIds.add(new RecipientId(id, address));
                    }

                    sRecipientIds.clear();
                    sRecipientIds.addAll(recipientIds);

                    getBook().write(KEY_CACHE_RECIPIENT_IDS, recipientIds);
                } finally {
                    c.close();
                }
            }
        }
    }

    public static List<String> getNumbersByIds(String[] ids) {
        synchronized (lock) {
            if (sRecipientIds.size() == 0) readRecipientIds();

            int size = ids.length;

            List<String> numbers = new ArrayList<>();

            for (int i = 0;i < size;i++) {
                for (RecipientId recipientId : sRecipientIds) {
                    if (recipientId.id.equals(ids[i])) {
                        numbers.add(recipientId.address);
                        break;
                    }
                }
            }

            return numbers;
        }
    }

    private static void readRecipientIds() {
        sRecipientIds.clear();

        sRecipientIds.addAll(getBook().read(KEY_CACHE_RECIPIENT_IDS, new ArrayList<RecipientId>()));
    }

    private static Book getBook() {
        return Paper.book(BOOK);
    }
}
