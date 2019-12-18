package vn.com.call.model.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ngson on 07/07/2017.
 */

public class PhoneNumber implements Parcelable {
    private String number;
    private String type;
    private boolean primary;
    private boolean superPrimary;

    public PhoneNumber(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public PhoneNumber(String number, String type, boolean primary, boolean superPrimary) {
        this.number = number;
        this.type = type;
        this.primary = primary;
        this.superPrimary = superPrimary;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isSuperPrimary() {
        return superPrimary;
    }

    public void setSuperPrimary(boolean superPrimary) {
        this.superPrimary = superPrimary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.type);
        dest.writeByte(this.primary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.superPrimary ? (byte) 1 : (byte) 0);
    }

    protected PhoneNumber(Parcel in) {
        this.number = in.readString();
        this.type = in.readString();
        this.primary = in.readByte() != 0;
        this.superPrimary = in.readByte() != 0;
    }

    public static final Creator<PhoneNumber> CREATOR = new Creator<PhoneNumber>() {
        @Override
        public PhoneNumber createFromParcel(Parcel source) {
            return new PhoneNumber(source);
        }

        @Override
        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[size];
        }
    };
}
