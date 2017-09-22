package com.omega_r.omegarecyclerview.swipe_menu_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;

import java.util.List;

public class ContactsAdapter extends OmegaRecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contacts> mContactsList;

    public ContactsAdapter(List<Contacts> contactsList) {
        mContactsList = contactsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_swipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contacts contacts = mContactsList.get(position);

        TextView textView = holder.nameTextView;
        textView.setText(contacts.getName());
        Button button = holder.messageButton;
        button.setText(R.string.message);

        if (contacts.isOnline()) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }

        holder.editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), "Edit", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), "Delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public class ViewHolder extends OmegaRecyclerView.ViewHolder {

        TextView nameTextView;
        Button messageButton;
        TextView editTextView;
        TextView deleteTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = findViewById(R.id.text_contact_name);
            messageButton = findViewById(R.id.button_message);
            editTextView = findViewById(R.id.text_edit);
            deleteTextView = findViewById(R.id.text_delete);
        }
    }
}
