package com.omega_r.omegarecyclerview.swipe_menu_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;

import java.util.ArrayList;

public class SwipeMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_menu);

        OmegaRecyclerView omegaRecyclerView = findViewById(R.id.recycler_view_contacts);
        ArrayList<Contacts> contactsList = Contacts.createContactsList(20);
        ContactsAdapter adapter = new ContactsAdapter(this, contactsList);

        omegaRecyclerView.setAdapter(adapter);
        omegaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        contactsList.add(20, new Contacts(null, false));
        adapter.notifyItemInserted(20);
    }
}
