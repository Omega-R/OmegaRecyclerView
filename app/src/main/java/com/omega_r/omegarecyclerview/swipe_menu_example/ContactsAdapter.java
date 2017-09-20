package com.omega_r.omegarecyclerview.swipe_menu_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;

import java.util.List;

public class ContactsAdapter extends  OmegaRecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contacts> mContactsList;
    private Context mContext;

    public ContactsAdapter(Context context, List<Contacts> contactsList) {
        mContactsList = contactsList;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public class ViewHolder extends OmegaRecyclerView.ViewHolder {

        public TextView nameTextView;
        public Button messageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.text_contact_name);
            messageButton = itemView.findViewById(R.id.button_message);
        }
    }
}
