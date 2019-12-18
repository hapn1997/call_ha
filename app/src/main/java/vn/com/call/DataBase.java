package vn.com.call;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBase extends SQLiteOpenHelper {


    public String DB_PATH = "//data//data//%s//databases//";
    private static String DB_NAME = "database.db";
    public SQLiteDatabase database;
    private final Context mContext;

    // đây là Class hỗ trợ việc coppy cái file sqlite từ thư mục
    // asset vào trong thư mục bộ nhớ trong của ứng dụng

    public DataBase(Context con) {
        super(con, DB_NAME, null, 2);
        DB_PATH = String.format(DB_PATH, con.getPackageName());
        this.mContext = con;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public boolean isCreatedDatabase() throws IOException {
        // Default là đã có DB

        boolean result = true;
        // Nếu chưa tồn tại DB thì copy từ Asses vào Data
        if (!checkExistDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
                result = false;
            } catch (Exception e) {
                throw new Error("Error copying database");
            }
        }

        return result;
    }


    private boolean checkExistDataBase() {
        // Kiểm tra cái file Sqlite đã được coppy vào bộ nhớ trong hay chưa
        try {
            String myPath = DB_PATH + DB_NAME;
            File fileDB = new File(myPath);
            if (fileDB.exists()) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void copyDataBase() throws IOException {
        // Copy file Sqlite từ trong thư mục assets
        // vào trong thư mục bộ nhớ trong của ứng dụng
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);
        Log.d("dcdcdc", String.valueOf(myOutput));
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    public void openDataBase() throws SQLException {
        database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (database != null)
            database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
