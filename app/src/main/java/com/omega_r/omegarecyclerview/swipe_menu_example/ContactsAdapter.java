package com.omega_r.omegarecyclerview.swipe_menu_example;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeViewHolder;
import com.omega_r.omegarecyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends OmegaRecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Contacts> mContactsList = new ArrayList<>();

    public ContactsAdapter(List<Contacts> contactsList) {
        mContactsList = contactsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.updateView(mContactsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public class ViewHolder extends SwipeViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private Button messageButton;
        private TextView editTextView;
        private TextView deleteTextView;
        private ImageButton voiceMailButton;
        private ImageButton bubbleChartButton;

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_swipe_content, R.layout.item_left_swipe_menu, R.layout.item_right_swipe_menu);

            nameTextView = findViewById(R.id.text_contact_name);
            messageButton = findViewById(R.id.button_message);
            editTextView = findViewById(R.id.text_edit);
            editTextView.setOnClickListener(this);
            deleteTextView = findViewById(R.id.text_delete);
            deleteTextView.setOnClickListener(this);
            voiceMailButton = findViewById(R.id.imagebutton_voicemail);
            voiceMailButton.setOnClickListener(this);
            bubbleChartButton = findViewById(R.id.imagebutton_bubble);
            bubbleChartButton.setOnClickListener(this);

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smoothOpenLeftMenu();
//                    return true;
                }
            });
        }

        void updateView(Contacts contact) {
            nameTextView.setText(contact.getName());
            messageButton.setVisibility(contact.isOnline() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_edit:
                    showToast("Edit");
                    break;
                case R.id.text_delete:
                    showToast("Delete");
                    break;
                case R.id.imagebutton_voicemail:
                    showToast("Voice mail");
                    break;
                case R.id.imagebutton_bubble:
                    showToast("Bubble");
                    break;
            }
        }

        private void showToast(String message) {
            Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
