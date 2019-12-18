package vn.com.call.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import vn.com.call.db.cache.MessageCache;
import vn.com.call.db.cache.RecipientIdsCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.model.sms.Conversation;
import vn.com.call.model.sms.Message;


/**
 * Created by ngson on 18/07/2017.
 */

public class SmsHelper {
    private final static String TAG = SmsHelper.class.getSimpleName();

    public static Observable<List<Conversation>> getThreads(final Context context) {
        return Observable.create(new Observable.OnSubscribe<List<Conversation>>() {
            @Override
            public void call(Subscriber<? super List<Conversation>> subscriber) {
                List<Conversation> conversations = getConversationsSync(context);

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(conversations);
                    subscriber.onCompleted();
                }
            }


        });
    }

    public static Observable<Integer> getNumberConversationUnread(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                List<Conversation> conversations = getConversationsSync(context);

                int numberUnread = 0;
                for (Conversation conversation : conversations) {
                    if (conversation.isHasUnreadMessage()) numberUnread++;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(numberUnread);
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static List<Conversation> getConversationsSync(Context context) {
        ContentResolver cr = context.getContentResolver();

        List<Conversation> conversations = new ArrayList<>();

        Cursor c = cr.query(Uri.parse("content://mms-sms/conversations?simple=true"), null, null, null, "date DESC");

        if (c != null) {
            try {
                while (c.moveToNext()) {
                    Conversation conversation = getConversationFromCursor(context, c);
                    if (conversation != null) {
                        conversations.add(conversation);
                    }
                }

                MessageCache.cacheConversations(conversations);
            } finally {
                c.close();
            }
        }

        return conversations;
    }

    public static void markSeenConversation(Context context, long threadId) {
        ContentValues cv = new ContentValues();
        cv.put("read", 1);
        cv.put("seen", 1);

        context.getContentResolver().update(ContentUris.withAppendedId(Uri.parse("content://mms-sms/conversations"), threadId), cv, "(read=0 OR seen=0)", null);
        MessageCache.markConversationRead(threadId);
    }

    public static Conversation getConversationByThreadId(Context context, long threadId ) {
       ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Uri.parse("content://mms-sms/conversations?simple=true"), null, "_id=?", new String[]{threadId + ""}, "date DESC");
        c.moveToFirst();

        Conversation conversation = getConversationFromCursor(context, c);

        c.close();

        return conversation;
    }
    public Conversation deleteConversation(Context context, long thread){
        Conversation conversation = getConversationByThreadId(context,thread);
        List<Conversation>  conversations = getConversationsSync(context);
        for (Conversation c : conversations) {
            if (c.getThreadId() == conversation.getThreadId()) {
                conversations.remove(c);
                break;
            }
        }
        MessageCache.cacheConversations(conversations);

        context.getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://mms-sms/conversations"), thread), null, null);

        //  ContentResolver cr = context.getContentResolver();

      //  Conversation conversation = getConversationFromCursor(context, c);
        //Log.d("ccdcdcdcđc",conversation.getName()+conversation.getName()+"  "+threadId);
    //    c.close();

          return conversation;

    }

    private static Conversation getConversationFromCursor(Context context, Cursor c) {
        if (c.getCount() > 0) {
            String snippet = c.getString(c.getColumnIndex("snippet"));
            if (snippet != null && snippet.length() > 0) {
                long threadId = c.getLong(c.getColumnIndex("_id"));

                String recipientIds = c.getString(c.getColumnIndex("recipient_ids"));
                String type = c.getString(c.getColumnIndex("type"));
                long date = c.getLong(c.getColumnIndex("date"));
                int read = c.getInt(c.getColumnIndex("read"));

                List<String> numbers = RecipientIdsCache.getNumbersByIds(recipientIds.split(" "));
                List<Contact> contactList = new ArrayList<>();

                for (String number : numbers)
                    contactList.add(getContactByNumber(context, number));

                return new Conversation(threadId, snippet, date, read == 0, contactList);
            }
        }

        return null;
    }

    private static Contact getContactByNumber(Context context, String number) {
        String[] data = ContactHelper.retrieveContactPhotoUriAndDisplayName(context, number);

        String contactId = data[0];
        String photo = data[1];
        String name = data[2];

        Contact contact = new Contact(contactId, null, name);
        contact.setPhoto(photo);

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(number, "Mobile"));
        contact.setNumbers(phoneNumbers);

        return contact;
    }

    public static void cacheAllMessage(final Context context) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                ContentResolver cr = context.getContentResolver();

                Cursor c = cr.query(Uri.parse("content://sms/"), null, null, null, "date DESC");

                if (c != null) {
                    try {
                        long start = System.currentTimeMillis();
                        List<Message> messages = new ArrayList<>();

                        while (c.moveToNext()) {
                            messages.add(getMessageByCursor(c));
                        }

                        MessageCache.cacheMessages(messages);
                        Log.wtf(TAG, "" + (System.currentTimeMillis() - start));

                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } finally {
                        c.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .delay(500, TimeUnit.MILLISECONDS).subscribe();
    }

    public static Observable<List<Message>> getMessagesByThreadId(final Context context, final long threadId) {
        return Observable.create(new Observable.OnSubscribe<List<Message>>() {
            @Override
            public void call(Subscriber<? super List<Message>> subscriber) {
                List<Message> messages = getMessageByThreadIdSync(context, threadId);

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(messages);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static List<Message> getMessageByThreadIdSync(final Context context, final long threadId) {
        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(Uri.parse("content://sms/"), null, "thread_id =?", new String[]{threadId + ""}, "date DESC");
        List<Message> messages = new ArrayList<>();

        try {
            while (c.moveToNext()) {
                messages.add(getMessageByCursor(c));
            }
        } finally {
            c.close();
        }

        return messages;
    }

    public static void deleteMessage(Context context, String messageId) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(Uri.parse("content://sms/"), "_id=?", new String[]{messageId});
    }

    public static void removeConversation(Context context, Conversation conversation) {
        List<Conversation> conversations = getConversationsSync(context);
        for (Conversation c : conversations) {
            if (c.getThreadId() == conversation.getThreadId()) {
                conversations.remove(c);
                break;
            }
        }
        MessageCache.cacheConversations(conversations);

        context.getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://mms-sms/conversations"), conversation.getThreadId()), null, null);
    }

    public static void markReadMessage(Context context, String messageId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("read", 1);
        contentValues.put("seen", 1);

        context.getContentResolver().update(Uri.parse("content://sms/inbox"), contentValues, "_id=?", new String[]{messageId});
    }

    public static Uri insertMessageToInbox(Context context, String address, String body, long date) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put("address", address);
        cv.put("body", body);
        cv.put("date_sent", date);

        return contentResolver.insert(Uri.parse("content://sms/inbox"), cv);
    }

    public static Message getMessageByUri(Context context, Uri uri) {
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);

        try {
            c.moveToFirst();

            return getMessageByCursor(c);
        } finally {
            c.close();
        }
    }

    private static Message getMessageByCursor(Cursor c) {
        String id = c.getString(c.getColumnIndex("_id"));
        long threadId = c.getLong(c.getColumnIndex("thread_id"));
        String address = c.getString(c.getColumnIndex("address"));
        String body = c.getString(c.getColumnIndex("body"));
        long date = c.getLong(c.getColumnIndex("date"));
        long dateSent = c.getLong(c.getColumnIndex("date_sent"));
        int type = c.getInt(c.getColumnIndex("type"));
        int status = c.getInt(c.getColumnIndex("status"));

        return new Message(id, threadId, address, body, date, dateSent, type, status);
    }

    public static Observable<List<Integer>> findMessageByKeyword(final Context context, final long threadId, final String keyword) {
        return Observable.create(new Observable.OnSubscribe<List<Integer>>() {
            @Override
            public void call(Subscriber<? super List<Integer>> subscriber) {
                List<Message> messages = getMessageByThreadIdSync(context, threadId);

                List<Integer> results = new ArrayList<>();
                int size = messages.size();

                for (int i = 0;i < size;i++) {
                    Message message = messages.get(i);

                    if (message.getBody().toLowerCase().contains(keyword)) results.add(i);
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(results);
                    subscriber.onCompleted();
                }
            }
        });
    }
}
