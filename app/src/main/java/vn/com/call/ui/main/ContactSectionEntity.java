package vn.com.call.ui.main;

import com.chad.library.adapter.base.entity.SectionEntity;

import vn.com.call.model.contact.Contact;

/**
 * Created by ngson on 07/07/2017.
 */

public class ContactSectionEntity extends SectionEntity<Contact> {

    public ContactSectionEntity(boolean isHeader, String header) {

        super(isHeader, header);
    }


    public ContactSectionEntity(Contact contact) {
        super(contact);
    }
}
