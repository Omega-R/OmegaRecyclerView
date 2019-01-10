package androidx.recyclerview.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

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
