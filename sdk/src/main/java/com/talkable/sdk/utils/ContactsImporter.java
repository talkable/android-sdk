package com.talkable.sdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

import com.talkable.sdk.models.Contact;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsImporter {
    public static ArrayList<Contact> getAllContacts(Context context) {
        HashMap<String, Contact> allContacts = new HashMap<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    Data.CONTENT_URI,
                    new String[]{Data.CONTACT_ID, Data.MIMETYPE, Email.ADDRESS, Contacts.DISPLAY_NAME, Phone.NUMBER},
                    null,
                    null,
                    Contacts.DISPLAY_NAME);

            Contact contact;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(Data.CONTACT_ID));
                    String mimeType = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));

                    if (allContacts.containsKey(id)) {
                        contact = allContacts.get(id);
                    } else {
                        contact = new Contact();
                        allContacts.put(id, contact);
                    }

                    if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
                        contact.setName(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)));
                    }
                    if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
                        contact.getPhoneNumbers().add(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
                    }
                    if (mimeType.equals(Email.CONTENT_ITEM_TYPE)) {
                        contact.getEmails().add(cursor.getString(cursor.getColumnIndex(Email.ADDRESS)));
                    }
                }
                cursor.close();
            }
        }
        return new ArrayList<>(allContacts.values());
    }
}
