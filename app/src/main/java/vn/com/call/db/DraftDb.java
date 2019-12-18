package vn.com.call.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ngson on 11/10/2017.
 */

public class DraftDb extends SQLiteOpenHelper {
    private final static String DB_NAME = "draft";
    private final static int DB_VERSION = 1;

    private final static String TABLE_DRAFT_MESSAGE = "draft_message";

    private final static String COLUMN_ID = "id";
    private final static String COLUMN_MESSAGE = "message";
    private final static String COLUMN_DATE = "date";

    public DraftDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_DRAFT_MESSAGE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_DATE + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addDraft(long id, String message) {
        if (isExistDraft(id)) updateDraft(id, message);
        else insertDraft(id, message);
    }

    private void insertDraft(long id, String message) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_MESSAGE, message);
        cv.put(COLUMN_DATE, System.currentTimeMillis());

        getWritableDatabase().insert(TABLE_DRAFT_MESSAGE, COLUMN_ID, cv);
    }

    private void updateDraft(long id, String message) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_MESSAGE, message);
        cv.put(COLUMN_DATE, System.currentTimeMillis());

        getWritableDatabase().update(TABLE_DRAFT_MESSAGE, cv, COLUMN_ID + "=?", new String[]{Long.toString(id)});
    }

    public boolean isExistDraft(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_DRAFT_MESSAGE + " WHERE " + COLUMN_ID + "=?", new String[]{Long.toString(id)});

        c.moveToNext();

        try {
          return c.getCount() > 0;
        } finally {
            c.close();
        }
    }

    public String getDraft(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_DRAFT_MESSAGE + " WHERE " + COLUMN_ID + "=?", new String[]{Long.toString(id)});

        try {
            c.moveToFirst();

            if (c.getCount() > 0) return c.getString(c.getColumnIndex(COLUMN_MESSAGE));
            else return null;
        } finally {
            c.close();
        }
    }

    public void removeDraft(long id) {
        getWritableDatabase().delete(TABLE_DRAFT_MESSAGE, COLUMN_ID + "=?", new String[]{Long.toString(id)});
    }

    public long getDraftDate(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_DRAFT_MESSAGE + " WHERE " + COLUMN_ID + "=?", new String[]{Long.toString(id)});

        try {
            c.moveToFirst();

            if (c.getCount() > 0) return c.getLong(c.getColumnIndex(COLUMN_DATE));
            else return 0l;
        } finally {
            c.close();
        }
    }
}
