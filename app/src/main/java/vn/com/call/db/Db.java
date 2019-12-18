package vn.com.call.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import vn.com.call.model.AppConfig;

/**
 * Created by ngson on 12/06/2017.
 */

public class Db extends SQLiteOpenHelper {
    private final static String DB_NAME = "all_mess_in_one";
    private final static int DB_VERSION = 2;

    public static final String TABLE_CONFIG_APPS = "apps_in_config";
    public static final String TABLE_INSTALLED_APPS = "apps_installed";
    public static final String TABLE_APPS_MANUAL_REMOVE = "apps_manual_remove";

    private final String COLUMN_ID = "package_name";
    private final String COLUMN_NAME = "name";
    private final String COLUMN_COUNTER = "counter";
    private final String COLUMN_LAST_USED = "last_used";
    private final String COLUMN_ORDER = "order_app";

    public Db(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONFIG_APPS + " (" + COLUMN_ID + " TEXT PRIMARY KEY, "
                                                                + COLUMN_NAME + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_INSTALLED_APPS + " (" + COLUMN_ID + " TEXT PRIMARY KEY, "
                                                                + COLUMN_NAME + " TEXT, "
                                                                + COLUMN_COUNTER + " INTEGER, "
                                                                + COLUMN_LAST_USED + " INTEGER, "
                                                                + COLUMN_ORDER + " INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE " + TABLE_APPS_MANUAL_REMOVE + " (" + COLUMN_ID + " TEXT PRIMARY KEY);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2 && oldVersion == 1) {
            db.execSQL("ALTER TABLE " + TABLE_INSTALLED_APPS + " ADD COLUMN " + COLUMN_ORDER + " INTEGER DEFAULT 0");
        }
    }

    public void addApp(String packageName, String name) {
        if (!isAppChoosed(packageName, TABLE_INSTALLED_APPS)) {
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_ID, packageName);
            cv.put(COLUMN_NAME, name);
            cv.put(COLUMN_COUNTER, 0);

            getWritableDatabase().insert(TABLE_INSTALLED_APPS, COLUMN_ID, cv);
        }
    }

    public void removeApp(String packageName, boolean manualRemove) {
        getWritableDatabase().delete(TABLE_INSTALLED_APPS, COLUMN_ID + "=?", new String[]{packageName});

        if (!isAppChoosed(packageName, TABLE_APPS_MANUAL_REMOVE) && manualRemove) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, packageName);
            getWritableDatabase().insert(TABLE_APPS_MANUAL_REMOVE, COLUMN_ID, cv);
        }
    }

    public boolean isAppChoosed(String packageName, String table) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + table + " WHERE " + COLUMN_ID + "=?", new String[]{packageName});

        c.moveToFirst();
        int size = c.getCount();
        c.close();

        return size > 0;
    }

    public List<AppConfig> getMessageApps() {
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_INSTALLED_APPS + " ORDER BY " + COLUMN_ORDER + " DESC", null);

        List<AppConfig> apps = new ArrayList<>();

        while (c.moveToNext()) {
            String packageName = c.getString(c.getColumnIndex(COLUMN_ID));
            String name = c.getString(c.getColumnIndex(COLUMN_NAME));
            int counter = c.getInt(c.getColumnIndex(COLUMN_COUNTER));
            long lastUsed = c.getLong(c.getColumnIndex(COLUMN_LAST_USED));

            apps.add(new AppConfig(name, packageName, counter, lastUsed));
        }

        c.close();

        return apps;
    }

    public AppConfig getMessageApp(String packageName) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INSTALLED_APPS + " WHERE " + COLUMN_ID + "=?", new String[]{packageName});

        c.moveToFirst();
        if (c.getCount() > 0) {
            String name = c.getString(c.getColumnIndex(COLUMN_NAME));

            return new AppConfig(name, packageName);
        }

        c.close();

        return null;
    }

    public void upCounter(String packageName) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_INSTALLED_APPS +
                " SET " + COLUMN_COUNTER + "=" + COLUMN_COUNTER + "+1" +
                " WHERE " + COLUMN_ID + "=?", new String[]{packageName});

        updateTimeLastUsed(packageName);
    }

    private void updateTimeLastUsed(String packageName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LAST_USED, System.currentTimeMillis());

        getWritableDatabase().update(TABLE_INSTALLED_APPS, contentValues, COLUMN_ID + "=?", new String[]{packageName});
    }

    public void updateIndexMessageApp(String packageName, int index) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER, index);

        getWritableDatabase().update(TABLE_INSTALLED_APPS, contentValues, COLUMN_ID + "=?", new String[]{packageName});
    }

    public void updateConfigApps(List<AppConfig> apps) {
        for (AppConfig app : apps) {
            if (!isAppChoosed(app.getPackageName(), TABLE_CONFIG_APPS)) {
                ContentValues cv = new ContentValues();

                cv.put(COLUMN_ID, app.getPackageName());
                cv.put(COLUMN_NAME, app.getName());
                getWritableDatabase().insert(TABLE_CONFIG_APPS, COLUMN_ID, cv);
            }
        }
    }

    public boolean isNeedLoadConfig() {
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_CONFIG_APPS, null);

        c.moveToFirst();
        int size = c.getCount();
        c.close();

        return size == 0;
    }

    public List<AppConfig> getAppsConfig() {
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_CONFIG_APPS, null);

        List<AppConfig> apps = new ArrayList<>();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(COLUMN_NAME));
            String packageName = c.getString(c.getColumnIndex(COLUMN_ID));

            apps.add(new AppConfig(name, packageName));
        }

        c.close();

        return apps;
    }
}
