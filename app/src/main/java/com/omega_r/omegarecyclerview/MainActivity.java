package com.omega_r.omegarecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.adapter.ContactsAdapter;
import com.omega_r.omegarecyclerview.model.Contact;

import java.util.ArrayList;

public class MainActivity extends Activity {

    ArrayList<Contact> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OmegaRecyclerView omegaRecyclerView = (OmegaRecyclerView) findViewById(R.id.recycler_view_contacts);

        mContacts = Contact.createContactsList(20);
        ContactsAdapter adapter = new ContactsAdapter(this, mContacts);
        omegaRecyclerView.setAdapter(adapter);
        omegaRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mContacts.add(20, new Contact(null, false));
        adapter.notifyItemInserted(20);
    }
}
