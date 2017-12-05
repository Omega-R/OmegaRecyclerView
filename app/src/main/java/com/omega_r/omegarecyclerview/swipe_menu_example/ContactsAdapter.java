package com.omega_r.omegarecyclerview.swipe_menu_example;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeViewHolder;
import com.omega_r.omegarecyclerview.R;

import java.util.List;

public class ContactsAdapter extends OmegaRecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contacts> mContactsList;

    public ContactsAdapter(List<Contacts> contactsList) {
        mContactsList = contactsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_swipe_content, SwipeViewHolder.NO_ID, R.layout.item_swipe_menu);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.updateView(mContactsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public class ViewHolder extends SwipeViewHolder {

        private TextView nameTextView;
        private Button messageButton;
        private TextView editTextView;
        private TextView deleteTextView;

        public ViewHolder(ViewGroup parent, int contentRes, int swipeLeftMenuRes, int swipeRightMenuRes) {
            super(parent, contentRes, swipeLeftMenuRes, swipeRightMenuRes);

            nameTextView = findViewById(R.id.text_contact_name);
            messageButton = findViewById(R.id.button_message);
            editTextView = findViewById(R.id.text_edit);
            deleteTextView = findViewById(R.id.text_delete);
        }

        void updateView(Contacts contact) {
            nameTextView.setText(contact.getName());
            messageButton.setText(R.string.message);
            messageButton.setVisibility(contact.isOnline() ? View.VISIBLE : View.GONE);

            editTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Edit", Toast.LENGTH_SHORT).show();
                }
            });

            deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
