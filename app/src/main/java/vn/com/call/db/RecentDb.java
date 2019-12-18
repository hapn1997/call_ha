package vn.com.call.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngson on 06/07/2017.
 */

public class RecentDb extends SQLiteOpenHelper {
    private static final String TAG = RecentDb.class.getSimpleName();

    private final static String DB_NAME = "recents";
    private final static int DB_VERSION = 1;

    private final String COLUMN_NUMBER = "number";
    private final String COLUMN_COUNTER = "counter";

    private final String TABLE_RECENT_SMS_AND_CALL = "recent_sms_call";

    public enum OrderBy {
        DESC {
            @Override
            public String toString() {
                return "DESC";
            }
        }, ASC {
            @Override
            public String toString() {
                return "ASC";
            }
        }
    }

    public RecentDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_RECENT_SMS_AND_CALL + " (" + COLUMN_NUMBER + " TEXT PRIMARY KEY, " +
                                                                                    COLUMN_COUNTER + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void insert(String number) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NUMBER, number);
        cv.put(COLUMN_COUNTER, 1);

        getWritableDatabase().insert(TABLE_RECENT_SMS_AND_CALL, COLUMN_NUMBER, cv);

        Log.wtf(TAG, "insert " + number);
    }

    private boolean isHasNumber(String number) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_RECENT_SMS_AND_CALL + " WHERE " + COLUMN_NUMBER + "=?", new String[]{number});

        c.moveToFirst();
        int count = c.getCount();
        c.close();

        return count > 0;
    }

    public void upCounter(String number) {
        if (isHasNumber(number)) {
            getWritableDatabase().execSQL("UPDATE " + TABLE_RECENT_SMS_AND_CALL +
                    " SET " + COLUMN_COUNTER + "=" + COLUMN_COUNTER + "+1" +
                    " WHERE " + COLUMN_NUMBER + "=?", new String[]{number});
            Log.wtf(TAG, "update +1 : " + number);
        } else insert(number);
    }

    public List<String> listNumberRecents(OrderBy orderBy, int limit) {
        List<String> numbers = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_RECENT_SMS_AND_CALL
                + " ORDER BY " + COLUMN_COUNTER + " " + orderBy.toString()
                + " LIMIT " + limit, null);

        while (c.moveToNext()) {
            String number = c.getString(c.getColumnIndex(COLUMN_NUMBER));
            numbers.add(number);
        }

        c.close();

        return numbers;
    }

    public static void upCounter(Context context, String number) {
        RecentDb db = new RecentDb(context);
        try {
            db.upCounter(number);
        } finally {
            db.close();
        }
    }

    public void removeNumber(String number) {
        getWritableDatabase().delete(TABLE_RECENT_SMS_AND_CALL, COLUMN_NUMBER + "=?", new String[]{number});
    }
}
