package vn.com.call.model.sms;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import vn.com.call.App;
import vn.com.call.db.ScheduleMessageDb;
import vn.com.call.db.SmsHelper;

/**
 * Created by ngson on 21/07/2017.
 */

public class Message implements Parcelable {
    private String id;
    private long threadId;
    private String address;
    private String body;
    private long date;
    private long dateSent;
    private int type;
    private int status;
    private boolean scheduleMessage;

    public Message() {
    }

    public Message(Context context, Uri uri) {
        Message message = SmsHelper.getMessageByUri(context, uri);

        id = message.getId();
        threadId = message.getThreadId();
        address = message.getAddress();
        body = message.getBody();
        date = message.getDate();
        dateSent = message.getDateSent();
        type = message.getType();
        status = message.getStatus();
    }

    public Message(String id, long threadId, String address, String body, long date, long dateSent, int type, int status) {
        this.id = id;
        this.threadId = threadId;
        this.address = address;
        this.body = body;
        this.date = date;
        this.dateSent = dateSent;
        this.type = type;
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public long getDate() {
        return date;
    }

    public long getDateSent() {
        return dateSent;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public void setScheduleMessage(boolean scheduleMessage) {
        this.scheduleMessage = scheduleMessage;
    }

    public boolean isScheduleMessage() {
        return scheduleMessage;
    }

    public void deleteMessage(Context context) {
        if (isScheduleMessage()) {
            ScheduleMessageDb scheduleMessageDb = ScheduleMessageDb.getInstance(context);
            scheduleMessageDb.deleteMessage(dateSent);

            App app = (App) context.getApplicationContext();
            app.rescheduleMessage();
        } else SmsHelper.deleteMessage(context, id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.threadId);
        dest.writeString(this.address);
        dest.writeString(this.body);
        dest.writeLong(this.date);
        dest.writeLong(this.dateSent);
        dest.writeInt(this.type);
        dest.writeInt(this.status);
        dest.writeByte(this.scheduleMessage ? (byte) 1 : (byte) 0);
    }

    protected Message(Parcel in) {
        this.id = in.readString();
        this.threadId = in.readLong();
        this.address = in.readString();
        this.body = in.readString();
        this.date = in.readLong();
        this.dateSent = in.readLong();
        this.type = in.readInt();
        this.status = in.readInt();
        this.scheduleMessage = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
