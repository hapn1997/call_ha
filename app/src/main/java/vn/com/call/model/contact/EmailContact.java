package vn.com.call.model.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ngson on 07/07/2017.
 */

public class EmailContact implements Parcelable {
    private String email;
    private String type;

    public EmailContact(String email, String type) {
        this.email = email;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.type);
    }

    protected EmailContact(Parcel in) {
        this.email = in.readString();
        this.type = in.readString();
    }

    public static final Creator<EmailContact> CREATOR = new Creator<EmailContact>() {
        @Override
        public EmailContact createFromParcel(Parcel source) {
            return new EmailContact(source);
        }

        @Override
        public EmailContact[] newArray(int size) {
            return new EmailContact[size];
        }
    };
}
