package com.omega_r.omegarecyclerview.swipe_menu_example;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class SwipeMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_menu);

        OmegaRecyclerView omegaRecyclerView = findViewById(R.id.recycler_view_contacts);
        ContactsAdapter adapter = new ContactsAdapter(Contacts.createContactsList(20));
        omegaRecyclerView.setAdapter(adapter);
    }

}
