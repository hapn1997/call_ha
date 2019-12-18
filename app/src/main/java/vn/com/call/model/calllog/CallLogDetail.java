package vn.com.call.model.calllog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import vn.com.call.db.cache.CallLogHelper;


/**
 * Created by ngson on 03/07/2017.
 */

public class CallLogDetail implements Parcelable {
    private String id;
    private int duration;
    private int type;
    private long date;
    private String number;

    public CallLogDetail(String id, int duration, int type, long date) {

        this.duration = duration;
        this.type = type;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void delete(Context context) {
        CallLogHelper.deleteCallLog(context, this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.duration);
        dest.writeInt(this.type);
        dest.writeLong(this.date);
        dest.writeString(this.number);
    }

    protected CallLogDetail(Parcel in) {
        this.id = in.readString();
        this.duration = in.readInt();
        this.type = in.readInt();
        this.date = in.readLong();
        this.number = in.readString();
    }

    public static final Creator<CallLogDetail> CREATOR = new Creator<CallLogDetail>() {
        @Override
        public CallLogDetail createFromParcel(Parcel source) {
            return new CallLogDetail(source);
        }

        @Override
        public CallLogDetail[] newArray(int size) {
            return new CallLogDetail[size];
        }
    };
}
