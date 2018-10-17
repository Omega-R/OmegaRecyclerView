package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class ExpandedRecyclerView extends RecyclerView {
    public static final int STEP_START = 1;
    public static final int STEP_LAYOUT = 2;
    public static final int STEP_ANIMATIONS = 4;

    public ExpandedRecyclerView(Context context) {
        super(context);
    }

    public ExpandedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getAdapterPositionFor(ViewHolder viewHolder) {
        return super.getAdapterPositionFor(viewHolder);
    }

    public static ExpandedViewHolder getChildViewHolderInt(View child) {
        return child == null ? null : (ExpandedViewHolder) ((RecyclerView.LayoutParams) child.getLayoutParams()).mViewHolder;
    }

    public int getLayoutStep() {
        return mState.mLayoutStep;
    }

    public static class ExpandedViewHolder extends RecyclerView.ViewHolder {

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

}
