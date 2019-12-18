package vn.com.call.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import vn.com.call.model.contact.Contact;
import vn.com.call.model.sms.Conversation;
import vn.com.call.model.sms.Message;


/**
 * Created by ngson on 30/10/2017.
 */

public class ScheduleMessageDb extends SQLiteOpenHelper {
    private final static String DB_NAME = "ScheduleMessageDb";
    private final static int DB_VERSION = 1;

    private final static String TABLE_SCHEDULE_MESSAGE = "schedule_message";

    private final static String COLUMN_THREAD_ID = "thread_id";
    private final static String COLUMN_ADDRESS = "contacts";
    private final static String COLUMN_MESSAGE = "message";
    private final static String COLUMN_TIME = "time";

    private Context mContext;

    private static ScheduleMessageDb sDb;

    public synchronized static ScheduleMessageDb getInstance(Context context) {
        if (sDb == null) sDb = new ScheduleMessageDb(context.getApplicationContext());

        return sDb;
    }

    private ScheduleMessageDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SCHEDULE_MESSAGE + " ("
                + COLUMN_THREAD_ID + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_TIME + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(long threadId, String contacts, String message, long time) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_THREAD_ID, threadId);
        cv.put(COLUMN_ADDRESS, contacts);
        cv.put(COLUMN_MESSAGE, message);
        cv.put(COLUMN_TIME, time);

        getWritableDatabase().insert(TABLE_SCHEDULE_MESSAGE, null, cv);
    }

    public void deleteMessage(long time) {
        getWritableDatabase().delete(TABLE_SCHEDULE_MESSAGE, COLUMN_TIME + "=?", new String[]{Long.toString(time)});
    }

    public List<Message> getScheduleMessagesByThread(long threadId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_SCHEDULE_MESSAGE + " WHERE " + COLUMN_THREAD_ID + "=?" + " ORDER BY " + COLUMN_TIME + " DESC", new String[]{Long.toString(threadId)});

        try {
           return getScheduleMessageFromCursor(c);
        } finally {
            c.close();
        }
    }

    public List<Message> getAllScheduleMessage() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_SCHEDULE_MESSAGE, null);

        try {
            return getScheduleMessageFromCursor(c);
        } finally {
            c.close();
        }
    }

    private List<Message> getScheduleMessageFromCursor(Cursor c) {
        List<Message> messages = new ArrayList<>();

        while (c.moveToNext()) {
            long threadId = c.getLong(c.getColumnIndex(COLUMN_THREAD_ID));
            String address = c.getString(c.getColumnIndex(COLUMN_ADDRESS));
            String body = c.getString(c.getColumnIndex(COLUMN_MESSAGE));
            long time = c.getLong(c.getColumnIndex(COLUMN_TIME));

            Message message = new Message();
            message.setThreadId(threadId);
            message.setBody(body);
            message.setAddress(address);
            message.setDateSent(time);
            message.setScheduleMessage(true);
           // message.setType(TextBasedSmsColumns.MESSAGE_TYPE_QUEUED);
           // message.setStatus(TextBasedSmsColumns.STATUS_PENDING);

            messages.add(message);
        }

        return messages;
    }

    public List<Conversation> getConversationsScheduleMessage() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_SCHEDULE_MESSAGE
                + " GROUP BY " + COLUMN_THREAD_ID
                + " ORDER BY " + COLUMN_TIME + " DESC", null);

        try {
            List<Message> messages = getScheduleMessageFromCursor(c);

            List<Conversation> conversations = new ArrayList<>();

            for (Message message : messages) {
                String[] numbers = message.getAddress().split(" ");
                List<Contact> contacts = ContactHelper.getContactsFromNumbers(mContext, numbers);

                Conversation conversation = new Conversation(message.getThreadId(), message.getBody(), message.getDateSent(), false, contacts);
                conversations.add(conversation);
            }

            return conversations;
        } finally {
            c.close();
        }
    }

    public static List<Conversation> getConversationsScheduleMessage(Context context) {
        ScheduleMessageDb db = ScheduleMessageDb.getInstance(context);

        return db.getConversationsScheduleMessage();
    }
}
