package vn.com.call.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ngson on 26/07/2017.
 */

public class ConversationSim extends SQLiteOpenHelper {
    private static final String DB_NAME = "sim.db";
    private static final int DB_VERSION = 1;

    private final String TABLE_SIM = "sim";

    private final String COLUMN_THREAD_ID = "thread_id";
    private final String COLUMN_SIM = "sim";

    public ConversationSim(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SIM + " (" + COLUMN_THREAD_ID + " TEXT PRIMARY KEY, " + COLUMN_SIM + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(long threadId, String sim) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_THREAD_ID, threadId);
        cv.put(COLUMN_SIM, sim);

        if (exists(threadId)) {
            getWritableDatabase().update(TABLE_SIM, cv, COLUMN_THREAD_ID + "=?", new String[]{threadId + ""});
        } else {
            getWritableDatabase().insert(TABLE_SIM, null, cv);
        }
    }

    private boolean exists(long threadId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_SIM + " WHERE " + COLUMN_THREAD_ID + "=?", new String[]{threadId + ""});

        try {
            return c.getCount() > 0;
        } finally {
            c.close();
        }
    }

    public String getSim(long threadId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_SIM + " WHERE " + COLUMN_THREAD_ID + "=?", new String[]{threadId + ""});

        try {
            c.moveToFirst();

            return c.getCount() > 0 ? c.getString(c.getColumnIndex(COLUMN_SIM)) : null;
        } finally {
            c.close();
        }
    }
}
