package vn.com.call.db.cache;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;
import rx.Observable;
import rx.Subscriber;
import vn.com.call.model.contact.Contact;
import vn.com.call.model.contact.PhoneNumber;

/**
 * Created by ngson on 07/07/2017.
 */

public class ContactCache {
    private final static String BOOK_CACHE_CONTACT = "contacts";

    private final static String KEY_LIST_CONTACT = "list_contact";
    private final static String KEY_LIST_FAVORITE_CONTACT = "list_favorite_contact";
    private static final String TAG = ContactCache.class.getSimpleName();

    private static List<Contact> sContacts;

    public static boolean existCache() {
        return getBook().exist(KEY_LIST_CONTACT);
}

    public static void cacheContact(List<Contact> contacts) {
        getBook().write(KEY_LIST_CONTACT, contacts);

        sContacts = contacts;

        List<Contact> favorites = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.isFavorite()) favorites.add(contact);
        }

        cacheFavoriteContact(favorites);
    }

    public static void cacheFavoriteContact(List<Contact> contacts) {
        getBook().write(KEY_LIST_FAVORITE_CONTACT, contacts);
    }

    public static List<Contact> getContacts() {
        if (sContacts == null) sContacts = getBook().read(KEY_LIST_CONTACT, new ArrayList<Contact>());

        return sContacts;
    }

    public static Observable<List<Contact>> getContactsAsync() {
        return Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                List<Contact> contacts = getContacts();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(contacts);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<List<Contact>> getContactsForNewConversation(final String keyword) {
        return Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                List<Contact> contacts = getContacts();
                List<Contact> results = new ArrayList<>();

                for (Contact contact : contacts) {
                    if (contact.getName().toLowerCase().contains(keyword.toLowerCase())) {
                        if (contact.getNumbers() != null && contact.getName().length() > 0) {
                            for (PhoneNumber phoneNumber : contact.getNumbers()) {
                                Contact subContact = new Contact(contact.getId(), contact.getLookupKey(), contact.getName());
                                subContact.setPhoto(contact.getPhoto());

                                List<PhoneNumber> phoneNumbers = new ArrayList<>();
                                phoneNumbers.add(phoneNumber);
                                subContact.setNumbers(phoneNumbers);
                                results.add(subContact);
                            }
                        }
                    } else {
                        List<PhoneNumber> phoneNumbers = contact.getNumbers();

                        if (phoneNumbers != null) {
                            for (PhoneNumber phoneNumber : phoneNumbers) {
                                String number = phoneNumber.getNumber()
                                        .replace("(", "")
                                        .replace(")", "")
                                        .replace("-", "")
                                        .replace(".", "")
                                        .replace(" ", "");

                                if (number.contains(keyword)) {
                                    Contact subContact = new Contact(contact.getId(), contact.getLookupKey(), contact.getName());
                                    subContact.setPhoto(contact.getPhoto());

                                    List<PhoneNumber> phones = new ArrayList<>();
                                    phones.add(phoneNumber);
                                    subContact.setNumbers(phones);
                                    results.add(subContact);
                                }
                            }
                        }
                    }
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(results);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static List<Contact> getFavoriteContacts() {
        return getBook().read(KEY_LIST_FAVORITE_CONTACT, new ArrayList<Contact>());
    }

    public static void changeFavoriteContact(String contactId, boolean favorite) {
        for (Contact contact : sContacts) {
            if (contact.getId().equals(contactId)) {
                contact.setFavorite(favorite);
                break;
            }
        }

        cacheContact(sContacts);
    }

    public static void removeContact(String contactId) {
        for (Contact contact : sContacts) {
            if (contact.getId().equals(contactId)) {
                sContacts.remove(contact);
                break;
            }
        }

        cacheContact(sContacts);
    }

    public static Contact getContactById(String contactId) {
        if (contactId != null) {
            List<Contact> contacts = getContacts();

            for (Contact contact : contacts) {
                if (contact.getId().equals(contactId)) return contact;
            }
        }

        return null;
    }

    public static Observable<List<Contact>> findContactByKeyword(final String keyword) {
        return Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                List<Contact> contacts = getContacts();

                List<Contact> results = new ArrayList<>();

                for (Contact contact : contacts) {
                    if (contact.getName().toLowerCase().contains(keyword.toLowerCase())) results.add(contact);
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(results);
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Book getBook() {
        return Paper.book(BOOK_CACHE_CONTACT);
    }
}
