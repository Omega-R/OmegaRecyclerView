package com.omega_r.omegarecyclerview.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.adapter.ContactsAdapter;
import com.omega_r.omegarecyclerview.model.Contact;

import java.util.ArrayList;


public class TestFragment extends android.app.Fragment {


    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        OmegaRecyclerView omegaRecyclerView = (OmegaRecyclerView) view.findViewById(R.id.recycler_view_contacts);

        ArrayList<Contact> contacts = Contact.createContactsList(20);
        ContactsAdapter adapter = new ContactsAdapter(getActivity(), contacts);
        omegaRecyclerView.setAdapter(adapter);
        omegaRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contacts.add(20, new Contact(null, false));
        adapter.notifyItemInserted(20);

        return view;
    }

}
