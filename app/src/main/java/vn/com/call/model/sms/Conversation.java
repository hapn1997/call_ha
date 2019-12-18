package vn.com.call.model.sms;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.klinker.android.send_message.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.com.call.db.ContactHelper;
import vn.com.call.db.SmsHelper;
import vn.com.call.model.contact.Contact;

/**
 * Created by ngson on 18/07/2017.
 */

public class Conversation implements Parcelable {
    private final String TAG = Conversation.class.getSimpleName();

    private long threadId;
    private String lastMessage;
    private String address;
    private long date;
    private boolean hasUnreadMessage;
    private List<Contact> contacts;
    private String recipientIds;

    public Conversation(Context context, long conversationId) {
        createConversationFromId(context, conversationId, null);
    }

    public Conversation(Context context, String[] numbers) {
        Set<String> setNumber = new HashSet<>(Arrays.asList(numbers));
        long conversationId = Utils.getOrCreateThreadId(context, setNumber);

        createConversationFromId(context, conversationId, numbers);
    }

    public Conversation(long threadId, String lastMessage, long date, boolean hasUnreadMessage, List<Contact> contacts) {
        this.threadId = threadId;
        this.lastMessage = lastMessage;
        this.contacts = contacts;
        this.date = date;
        this.hasUnreadMessage = hasUnreadMessage;

    }

    private void createConversationFromId(Context context, long id, String[] numbers) {
        Conversation conversation = SmsHelper.getConversationByThreadId(context, id);

        if (conversation != null) {
            threadId = conversation.getThreadId();
            lastMessage = conversation.getLastMessage();
            address = conversation.getAddress();
            date = conversation.getDate();
            hasUnreadMessage = conversation.isHasUnreadMessage();
            contacts = conversation.getContacts();
        } else {
            threadId = id;

            contacts = ContactHelper.getContactsFromNumbers(context, numbers);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getAddress() {
        return address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isHasUnreadMessage() {
        return hasUnreadMessage;
    }

    public void setHasUnreadMessage(boolean hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public boolean isGroupMessage() {
        return contacts.size() > 1;
    }

    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Contact contact : contacts) {
            String name = contact.getName() != null ? contact.getName() : formatNumber(contact.getNumbers().get(0).getNumber());
            stringBuilder.append(name + ", ");
        }

        String name = stringBuilder.toString();
        if (name.endsWith(", ")) name = name.substring(0, name.length() - 2);

        return name;
    }

    private String formatNumber(String number) {
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(number, "VI");

            return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            //Not number

            return number;
        }
    }

    public String getNumber() {
        StringBuilder builder = new StringBuilder();

        for (Contact contact : contacts) {
            builder.append(contact.getNumbers().get(0).getNumber() + ", ");
        }

        String numbers = builder.toString();
        if (numbers.endsWith(", ")) numbers = numbers.substring(0, numbers.length() - 2);

        return numbers;
    }

    public void markSeenAll(final Context context) {
        SmsHelper.markSeenConversation(context, threadId);
    }

    public void remove(Context context) {
        SmsHelper.removeConversation(context, this);
    }

    public String[] getNumbers() {
        List<String> numbers = new ArrayList<>();

        for (Contact contact : contacts) {
            numbers.add(contact.getNumbers().get(0).getNumber());
        }

        return numbers.toArray(new String[]{});
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Contact contact : contacts) {
            builder.append(contact.getName() + " ");
        }

        return threadId + " " + builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.threadId);
        dest.writeString(this.lastMessage);
        dest.writeString(this.address);
        dest.writeLong(this.date);
        dest.writeByte(this.hasUnreadMessage ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.contacts);
        dest.writeString(this.recipientIds);
    }

    protected Conversation(Parcel in) {
        this.threadId = in.readLong();
        this.lastMessage = in.readString();
        this.address = in.readString();
        this.date = in.readLong();
        this.hasUnreadMessage = in.readByte() != 0;
        this.contacts = in.createTypedArrayList(Contact.CREATOR);
        this.recipientIds = in.readString();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel source) {
            return new Conversation(source);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };
}
