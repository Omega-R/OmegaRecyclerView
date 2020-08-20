package com.omega_r.libs.omegarecyclerview_expandable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.item_decoration.BaseSpaceItemDecoration;

import static com.omega_r.libs.omegarecyclerview_expandable.OmegaExpandableRecyclerView.Adapter.VH_TYPE_CHILD;
import static com.omega_r.libs.omegarecyclerview_expandable.OmegaExpandableRecyclerView.Adapter.VH_TYPE_GROUP;

@SuppressWarnings("rawtypes")
public class ExpandableSpaceItemDecoration extends BaseSpaceItemDecoration {

    private int mGroupItemSpace = 0;

    public ExpandableSpaceItemDecoration(int showDivider, int itemSpace) {
        super(showDivider, itemSpace);
    }

    public void setGroupSpace(int groupSpace) {
        mGroupItemSpace = groupSpace;
    }

    @Override
    protected int getItemSpace(@NonNull RecyclerView parent, int position, int itemCount) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) return super.getItemSpace(parent, position, itemCount);

        int viewType = adapter.getItemViewType(position);
        switch (viewType) {
            case VH_TYPE_GROUP:
                return mGroupItemSpace;
            case VH_TYPE_CHILD:
                if (position > 0 && adapter.getItemViewType(position - 1) == VH_TYPE_GROUP) return 0;
        }

        return super.getItemSpace(parent, position, itemCount);
    }

}
