package android.support.v7.widget;

import android.support.annotation.NonNull;
import android.view.View;

public class ExpandedViewHolder extends RecyclerView.ViewHolder {

    public ExpandedViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public boolean isAddedInPreLayout() {
        return (mFlags & FLAG_APPEARED_IN_PRE_LAYOUT) != 0;
    }

    public boolean isAttachedScrap() {
        return super.isScrap() && !mInChangeScrap;
    }

}
