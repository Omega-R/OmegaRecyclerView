package com.omega_r.omegarecyclerview.swipe_menu_example;


import java.util.ArrayList;
import java.util.List;

public class Contacts {

    private String mName;
    private boolean mOnline;

    public Contacts(String name, boolean online) {
        mName = name;
        mOnline = online;
    }

    public String getName() {
        return mName;
    }

    public boolean isOnline() {
        return mOnline;
    }

    private static int lastContactId = 0;

    public static List<Contacts> createContactsList(int numContacts) {
        List<Contacts> contactsArrayList = new ArrayList<>();

        for (int i = 0; i < numContacts; i++) {
            contactsArrayList.add(new Contacts("Person " + ++lastContactId, i <= numContacts / 2));
        }

        return contactsArrayList;
    }

}
