package vn.com.call.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ngson on 12/06/2017.
 */

public class AppConfig implements Parcelable {
    private String name;

    @SerializedName("package")
    private String packageName;

    private int counter;

    private long lastUsed;

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public AppConfig(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public AppConfig(String name, String packageName, int counter, long lastUsed) {
        this.name = name;
        this.packageName = packageName;
        this.counter = counter;
        this.lastUsed = lastUsed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getLastUsed() {
        Date date = new Date(counter == 0 ? System.currentTimeMillis() : lastUsed);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        String dateFormatted = formatter.format(date);

        return dateFormatted;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public AppConfig() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppConfig) return ((AppConfig) obj).getPackageName().equals(packageName);
        else if (obj instanceof String) return ((String) obj).equals(packageName);
        else return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.packageName);
        dest.writeInt(this.counter);
        dest.writeLong(this.lastUsed);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected AppConfig(Parcel in) {
        this.name = in.readString();
        this.packageName = in.readString();
        this.counter = in.readInt();
        this.lastUsed = in.readLong();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<AppConfig> CREATOR = new Creator<AppConfig>() {
        @Override
        public AppConfig createFromParcel(Parcel source) {
            return new AppConfig(source);
        }

        @Override
        public AppConfig[] newArray(int size) {
            return new AppConfig[size];
        }
    };
}
