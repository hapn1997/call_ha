package vn.com.call.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import vn.com.call.db.cache.ContactCache;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.EmailContact;
import vn.com.call.model.contact.PhoneNumber;
import vn.com.call.ui.main.ContactSectionEntity;

/**
 * Created by ngson on 03/07/2017.
 */

public class ContactHelper {
    private static final String TAG = ContactHelper.class.getSimpleName();

    public static final Uri CONTACT_URI = ContactsContract.Data.CONTENT_URI;

    public static String[] retrieveContactPhotoUriAndDisplayName(Context context, @NonNull String number) {
        if (!TextUtils.isEmpty(number)) {
            ContentResolver contentResolver = context.getContentResolver();
            String contactId = null;
            String photo = null;
            String displayName = null;
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

            String[] projection = new String[]{ContactsContract.Contacts.PHOTO_URI, ContactsContract.PhoneLookup._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};

            Cursor cursor =
                    contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            null);

            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                        photo = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI));
                        displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    }
                } finally {
                    cursor.close();
                }
            }

            return new String[]{contactId, photo, displayName};
        } return new String[]{"", "", ""};
    }
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
    public static String getContactIdFromNumber(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            }
        } finally {
            cursor.close();
        }

        return contactId;
    }

    public static List<Contact> getContactsFromNumbers(Context context, String[] numbers) {
        List<Contact> contacts = new ArrayList<>();

        int size = numbers.length;

        for (int i = 0;i < size;i++) {
            String contactId = getContactIdFromNumber(context, numbers[i]);
            Contact contact = ContactCache.getContactById(contactId);
            if (contact == null) {
                contact = new Contact(numbers[i]);
            }

            contacts.add(contact);
        }

        return contacts;
    }

    public static List<Contact> getRecentContact(Context context, List<Contact> contacts) {
        List<Contact> recents = new ArrayList<>();

        if (context != null) {
            RecentDb db = new RecentDb(context);

            List<String> phoneRecents = db.listNumberRecents(RecentDb.OrderBy.DESC, 10);


            try {
                for (String number : phoneRecents) {
                    Contact contact = getContactFromPhoneNumber(contacts, number);

                    if (contact == null) db.removeNumber(number);
                    else recents.add(contact);
                }
            } finally {
                db.close();
            }
        }

        return recents;
    }

    private static Contact getContactFromPhoneNumber(List<Contact> contacts, String number) {
        for (Contact contact : contacts) {
            List<PhoneNumber> phoneNumbers = contact.getNumbers();

            if (phoneNumbers != null) {
                for (PhoneNumber phoneNumber : phoneNumbers) {
                    String n = phoneNumber.getNumber().replace(" ", "").replace("-", "").replace("(", "").replace(")", "");

                    if (number.equals(n)) return contact;
                }
            }
        }

        return null;
    }

    public static Observable<List<Contact>> getListContact(final Context context) {
        return Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                List<Contact> contacts = new ArrayList<>();

                ContentResolver cr = context.getContentResolver();
                Cursor c = c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if (c != null) {
                    try {
                        while (c.moveToNext()) {
                            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                            String lookupKey = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                            String photo = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                            int starred = c.getInt(c.getColumnIndex(ContactsContract.Contacts.STARRED));
                            int hasPhoneNumber = c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            Contact contact = new Contact(id, lookupKey, name);
                            contact.setPhoto(photo);
                            contact.setFavorite(starred == 1);

                            if (hasPhoneNumber > 0)
                                contact.setNumbers(new ContactHelper().getPhoneNumbers(cr, id));

                            if (contact.getName() != null) contacts.add(contact);

                        }
                    } finally {
                        c.close();
                    }
                }

                ContactCache.cacheContact(contacts);

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(contacts);
                    subscriber.onCompleted();
                }
            }


        });
    }

    public static Observable<Contact> getInfoContact(final Context context, final Contact contact) {
        return Observable.create(new Observable.OnSubscribe<Contact>() {

            @Override
            public void call(Subscriber<? super Contact> subscriber) {
                if (contact.getId() != null) {
                    ContentResolver cr = context.getContentResolver();

                    String id = contact.getId();

                    contact.setNumbers(new ContactHelper().getPhoneNumbers(cr, id));
                    contact.setEmailContacts(getEmails(cr, id));
                    contact.setAddress(getAddress(cr, id));
                    contact.setWebsites(getWebsite(cr, id));
                    contact.setBirthday(getBirthday(cr, id));
                    contact.setNote(getNote(cr, id));
                    contact.setPhoto(getPhoto(cr, id));
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(contact);
                    subscriber.onCompleted();
                }
            }

            public List<EmailContact> getEmails(ContentResolver cr, String contactId) {
                List<EmailContact> emailContacts = new ArrayList<>();

                Cursor emailCursor = null;

                try {
                    emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                            new String[]{contactId},
                                null);

                    while (emailCursor.moveToNext()) {
                        String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        int type = emailCursor.getInt(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.TYPE));

                        String typeLabel = getEmailType(type);

                        if (type == ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM) {
                            typeLabel = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
                        }

                        emailContacts.add(new EmailContact(email, typeLabel));
                    }
                } finally {
                    if (emailCursor != null) emailCursor.close();
                }

                return emailContacts;
            }

            public String getAddress(ContentResolver cr, String contactId) {
                StringBuilder builder = new StringBuilder();

                Cursor c = null;

                try {
                    c = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                            new String[]{contactId},
                            null);

                    while (c.moveToNext()) {
                        String street = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        String city = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String country = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));

                        if (street != null) builder.append(street);
                        if (city != null) builder.append(city);
                        if (country != null) builder.append(country);

                        break;
                    }
                } finally {
                    if (c != null) c.close();
                }

                return builder.toString().replace("  ", " ");
            }

            public List<String> getWebsite(ContentResolver cr, String contactId) {
                List<String> websites = new ArrayList<>();

                Cursor c = null;

                try {
                    c = cr.query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                            new String[]{contactId, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE},
                            null);

                    while (c.moveToNext()) {
                        websites.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                    }
                } finally {
                    if (c != null) c.close();
                }

                return websites;
            }

            public String getBirthday(ContentResolver cr, String contactId) {
                Uri uri = ContactsContract.Data.CONTENT_URI;

                String[] projection = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Event.START_DATE
                };

                String where =
                        ContactsContract.Data.MIMETYPE + "= ? AND " +
                                ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
                String[] selectionArgs = new String[] {
                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
                };
                String sortOrder = null;
                Cursor c = cr.query(uri, projection, where, selectionArgs, sortOrder);

                int bDayColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
                while (c.moveToNext()) {
                    String bDay = c.getString(bDayColumn);

                    return bDay;
                }

                return null;
            }

            private String getEmailType(int type) {
                switch (type) {
                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME : return "Home";
                    case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE : return "Mobile";
                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK : return "Work";
                    case ContactsContract.CommonDataKinds.Email.TYPE_OTHER : return "Other";

                    default: return "Unknown";
                }
            }
            private String getPhoto(ContentResolver cr,String contactId) {
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
                Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
                return  displayPhotoUri.toString();
//                try {
//                    AssetFileDescriptor fd =
//                            cr.openAssetFileDescriptor(displayPhotoUri, "r");
//                    Bitmap photoAsBitmap = BitmapFactory.decodeStream(fd.createInputStream());
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
            private String getNote(ContentResolver cr,String contactId) {
//                ContentValues values = new ContentValues();
                String note = null;
//                values.clear();
                String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] noteWhereParams = new String[]{contactId,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//                values.put(ContactsContract.CommonDataKinds.Note.NOTE, "NEW NOTE HERE!!!!");
//
//                cr.update(ContactsContract.Data.CONTENT_URI, values, noteWhere, noteWhereParams);
//
                Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                if (noteCur.moveToFirst()) {

                  note =  noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    noteCur.close();
                }
                if (noteCur !=null) noteCur.close();
                return note;

            }
        });
    }

    public static void removeFavorite(Context context, String contactId) {
        changeFavorite(context, contactId, false);
    }

    public static void setFavorite(Context context, String contactId) {
        changeFavorite(context, contactId, true);
    }

    private static void changeFavorite(Context context, String contactId, boolean favorite) {
        ContentResolver cr = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Contacts.STARRED, favorite ? 1 : 0);

        cr.update(ContactsContract.Contacts.CONTENT_URI, contentValues, ContactsContract.Contacts._ID + "=?", new String[]{contactId});
    }

    public static void removeContact(Context context, String contactId, String lookupKey) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);

        cr.delete(uri, null, null);

        ContactCache.removeContact(contactId);
    }

    private List<PhoneNumber> getPhoneNumbers(ContentResolver cr, String contactId) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();

        Cursor phoneCursor = null;

        try {
            phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

            while (phoneCursor.moveToNext()) {
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
                boolean primary = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY)) == 1;

                String typeLabel = getLabelTypeOfPhone(type);

                if (type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
                    typeLabel = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                }

                phoneNumbers.add(new PhoneNumber(number, typeLabel));
            }
        } finally {
            if (phoneCursor != null) phoneCursor.close();
        }

        return phoneNumbers;
    }

    private String getLabelTypeOfPhone(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE : return "Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK : return "Work";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE : return "Work Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER : return "Work Pager";
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME : return "Home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN : return "Main";
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK : return "Work Fax";
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME : return "Home Fax";
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER : return "Pager";
            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT : return "Assistant";
            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR : return "Car";
            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN : return "Company Main";
            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN : return "ISDN";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS : return "MMS";
            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO : return "Radio";
            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX : return "Telex";
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX : return "Other Fax";
            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD : return "TTY TDD";
            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK : return "Callback";
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER : return "Other";

            default: return "Unknown";
        }
    }

    public static List<ContactSectionEntity> convertToSectionEntity(List<Contact> contacts) {
        List<ContactSectionEntity> contactSectionEntities = new ArrayList<>();

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                return contact.getName().compareTo(t1.getName());
            }
        });

        String lastHeader = null;
        for (Contact contact : contacts) {
            String header = contact.getName().substring(0, 1).toUpperCase();

            if (lastHeader == null || !header.equals(lastHeader)) {
                contactSectionEntities.add(new ContactSectionEntity(true, header));
                contactSectionEntities.add(new ContactSectionEntity(contact));

                lastHeader = header;
            } else {
                contactSectionEntities.add(new ContactSectionEntity(contact));
            }
        }

        return contactSectionEntities;
    }
}
