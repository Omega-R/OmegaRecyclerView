package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class ExpandedRecyclerView extends RecyclerView {

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

}
