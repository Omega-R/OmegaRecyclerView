package com.omega_r.omegarecyclerview.test_example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;

import java.util.ArrayList;

public class TestFragment extends Fragment {

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        OmegaRecyclerView omegaRecyclerView = view.findViewById(R.id.recycler_view_contacts);

        ArrayList<Contacts> contactsList = Contacts.createContactsList(20);
        ContactsAdapter adapter = new ContactsAdapter(getActivity(), contactsList);
        omegaRecyclerView.setAdapter(adapter);

        contactsList.add(20, new Contacts(null, false));
        adapter.notifyItemInserted(20);

        return view;
    }

}
