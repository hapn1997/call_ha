package vn.com.call.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngson on 21/07/2017.
 */

public class RecentEmoji extends SQLiteOpenHelper {
    private static final String DB_NAME = "recent_emoji";
    private static final int DB_VERSION = 1;

    private static final String TABLE_EMOJI_RECENT = "emoji_recent";

    private static final String COLUMN_EMOJI = "emoji";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_LAST_UPDATE = "last_update";

    public RecentEmoji(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EMOJI_RECENT + " (" + COLUMN_EMOJI + " TEXT PRIMARY KEY, "
                + COLUMN_COUNT + " INTEGER, "
                + COLUMN_LAST_UPDATE + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void upCount(String emoji) {
        if (exists(emoji)) update(emoji);
        else insert(emoji);
    }

    private boolean exists(String emoji) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EMOJI_RECENT + " WHERE " + COLUMN_EMOJI + "=?", new String[]{emoji});

        c.moveToFirst();
        int count = c.getCount();
        c.close();

        return count > 0;
    }

    private void insert(String emoji) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EMOJI, emoji);
        cv.put(COLUMN_COUNT, 1);
        cv.put(COLUMN_LAST_UPDATE, System.currentTimeMillis());

        getWritableDatabase().insert(TABLE_EMOJI_RECENT, null, cv);
    }

    private void update(String emoji) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_EMOJI_RECENT +
                " SET " + COLUMN_COUNT + "=" + COLUMN_COUNT + "+1" +
                " WHERE " + COLUMN_EMOJI + "=?", new String[]{emoji});
    }

    public List<String> getEmojis() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EMOJI_RECENT + " ORDER BY " + COLUMN_COUNT + " DESC", null);

        List<String> emojis = new ArrayList<>();

        try {
            while (c.moveToNext()) {
                emojis.add(c.getString(c.getColumnIndex(COLUMN_EMOJI)));
            }
        } finally {
            c.close();
        }

        return emojis;
    }

    public static void upCounterEmoji(Context context, String emoji) {
        RecentEmoji db = new RecentEmoji(context);
        db.upCount(emoji);
        db.close();
    }
}
