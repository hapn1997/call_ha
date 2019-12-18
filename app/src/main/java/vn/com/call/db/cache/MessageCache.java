package vn.com.call.db.cache;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import vn.com.call.model.sms.Conversation;
import vn.com.call.model.sms.Message;

/**
 * Created by ngson on 19/07/2017.
 */

public class MessageCache {
    private final static String TAG = MessageCache.class.getSimpleName();

    private static final String BOOK = "messages";

    private static final String CONVERSATIONS = "conversations";
    private static final String MESSAGES = "messages";

    private static List<Message> sMessages;
    private static List<Conversation> sConversations;

    public static void cacheConversations(List<Conversation> conversations) {
        sConversations = conversations;
        getBook().write(CONVERSATIONS, conversations);
    }

    public static List<Conversation> getConversations() {
        return sConversations != null ? sConversations : getBook().read(CONVERSATIONS, new ArrayList<Conversation>());
    }

    public static Observable<List<Conversation>> getConversationsAsync() {
        return Observable.create(new Observable.OnSubscribe<List<Conversation>>() {
            @Override
            public void call(Subscriber<? super List<Conversation>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(getConversations());
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static void markConversationRead(final long threadId) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                List<Conversation> conversations = getConversations();
                for (Conversation conversation : conversations) {
                    if (conversation.getThreadId() == threadId) {
                        conversation.setHasUnreadMessage(false);
                        break;
                    }
                }

                cacheConversations(conversations);

                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    public static void cacheMessages(List<Message> messages) {
        sMessages = messages;
        getBook().write(MESSAGES, messages);
    }

    public static List<Message> getMessages() {
        return sMessages != null ? sMessages : getBook().read(MESSAGES, new ArrayList<Message>());
    }

    public static Observable<List<Message>> getMessagesByThread(final long threadId) {
        return Observable.create(new Observable.OnSubscribe<List<Message>>() {
            @Override
            public void call(Subscriber<? super List<Message>> subscriber) {
                List<Message> messages = getMessages();

                List<Message> messagesByThread = new ArrayList<>();

                for (Message message : messages) {
                    if (message.getThreadId() == threadId) {
                        messagesByThread.add(message);
                    }
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(messagesByThread);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<List<Conversation>> findConversationByKeyword(final String keyword) {
        return Observable.create(new Observable.OnSubscribe<List<Conversation>>() {
            @Override
            public void call(Subscriber<? super List<Conversation>> subscriber) {
                List<Conversation> conversations = getConversations();

                List<Conversation> results = new ArrayList<>();

                for (Conversation conversation : conversations) {
                    if (conversation.getName().toLowerCase().contains(keyword.toLowerCase())) results.add(conversation);
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(results);
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Book getBook() {
        return Paper.book(BOOK);
    }
}
