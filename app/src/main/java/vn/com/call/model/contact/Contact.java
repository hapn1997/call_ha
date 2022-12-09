package vn.com.call.model.contact;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import com.phone.thephone.call.dialer.R;
import vn.com.call.db.ContactHelper;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.utils.ClipboardUtils;

/**
 * Created by ngson on 07/07/2017.
 */

public class Contact implements Parcelable {
    private String id;
    private String lookupKey;
    private String name;
    private String photo;
    private String address;
    private String birthday;
    private boolean favorite;
    private List<PhoneNumber> numbers;
    private List<EmailContact> emailContacts;
    private List<String> websites;
    private boolean selected;
    private String callorrsms;
    private String note;

    public String getCallorrsms() {
        return callorrsms;
    }

    public void setCallorrsms(String callorrsms) {
        this.callorrsms = callorrsms;
    }

    public Contact(String id, String lookupKey, String name) {
        this.id = id;
        this.name = name;
        this.lookupKey = lookupKey;
    }
    public Contact(String id, String callorsms, String name,String phone,String photo) {
        this.id = id;
        this.name = name;
        this.callorrsms = callorsms;
        PhoneNumber phoneNumber = new PhoneNumber(phone, "mobile");
        numbers = new ArrayList<>();
        numbers.add(phoneNumber);
        this.photo = photo;
    }

    public Contact(String number) {
        PhoneNumber phoneNumber = new PhoneNumber(number, "mobile");
        numbers = new ArrayList<>();
        numbers.add(phoneNumber);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public List<PhoneNumber> getNumbers() {
        return numbers == null ? new ArrayList<PhoneNumber>() : numbers;
    }

    public void setNumbers(List<PhoneNumber> numbers) {
        this.numbers = numbers;
    }

    public List<EmailContact> getEmailContacts() {
        return emailContacts;
    }

    public void setEmailContacts(List<EmailContact> emailContacts) {
        this.emailContacts = emailContacts;
    }

    public List<String> getWebsites() {
        return websites;
    }

    public void setWebsites(List<String> websites) {
        this.websites = websites;
    }

    public boolean hasPhoneNumber() {
        return numbers != null && numbers.size() > 0;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void delete(final Context context, boolean showConfirm, final boolean finishAfterDelete) {
        if (showConfirm) {
            new MaterialDialog.Builder(context)
                    .backgroundColor(Color.WHITE)
                    .titleColor(Color.BLACK)
                    .content(R.string.content_dialog_confirm_delete_contact)
                    .contentColor(Color.BLACK)
                    .negativeText(R.string.cancel)
                    .negativeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .positiveText(R.string.delete)
                    .positiveColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            deleteContact(context, finishAfterDelete);
                        }
                    }).show();
        } else deleteContact(context, finishAfterDelete);
    }

    private void deleteContact(Context context, boolean finishAfterDelete) {
        ContactHelper.removeContact(context, id, lookupKey);
        if (finishAfterDelete && context instanceof Activity) ((Activity) context ).finish();
    }

    public void changeFavorite(Context context) {
        if (isFavorite()) {
            setFavorite(false);
            ContactHelper.removeFavorite(context, id);

            String toast = context.getString(R.string.detail_activity_title_toast_remove_favorite).replace("{name}", getName());
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        } else {
            setFavorite(true);
            ContactHelper.setFavorite(context, id);

            String toast = context.getString(R.string.detail_activity_title_toast_add_favorite).replace("{name}", getName());
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }

        ContactCache.changeFavoriteContact(id, favorite);
    }

    public void copyName(Context context) {
        ClipboardUtils.copyToClipboard(context, "name", getName());
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    public void copyNumber(Context context) {
        ClipboardUtils.copyToClipboard(context, "numbers", getNumbersAsString());
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    public void copyContact(Context context) {
        ClipboardUtils.copyToClipboard(context, "contact", getName() + ": " + getNumbersAsString());
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    public String getNumbersAsString() {
        StringBuilder numbersBuilder = new StringBuilder();
        for (PhoneNumber phoneNumber : getNumbers()) {
            numbersBuilder.append(phoneNumber.getNumber());
            numbersBuilder.append(", ");
        }

        String numbers = numbersBuilder.toString();

        if (numbers.endsWith(", ")) numbers = numbers.substring(0, numbers.length() - 2);

        return numbers;
    }

    public String getNameDisplay() {
        return name == null ? numbers.get(0).getNumber() : name;
    }

    @Override
    public String toString() {
        return getNameDisplay();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.lookupKey);
        dest.writeString(this.name);
        dest.writeString(this.photo);
        dest.writeString(this.address);
        dest.writeString(this.birthday);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.numbers);
        dest.writeTypedList(this.emailContacts);
        dest.writeStringList(this.websites);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.callorrsms);
        dest.writeString(this.note);

    }

    protected Contact(Parcel in) {
        this.id = in.readString();
        this.lookupKey = in.readString();
        this.name = in.readString();
        this.photo = in.readString();
        this.address = in.readString();
        this.birthday = in.readString();
        this.favorite = in.readByte() != 0;
        this.numbers = in.createTypedArrayList(PhoneNumber.CREATOR);
        this.emailContacts = in.createTypedArrayList(EmailContact.CREATOR);
        this.websites = in.createStringArrayList();
        this.selected = in.readByte() != 0;
        this.callorrsms = in.readString();
        this.note = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

}
