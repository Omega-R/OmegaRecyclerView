package com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.omega_r.libs.omegarecyclerview.item_decoration.DividerItemDecoration.Orientation;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class DividerDecorationHelper {

    DividerDecorationHelper() {
        // nothing
    }

    private static final NormalVerticalDividerDecorationHelper sNormalVerticalDividerDecorationHelper = new NormalVerticalDividerDecorationHelper();
    private static final ReverseVerticalDividerDecorationHelper sReverseVerticalDividerDecorationHelper = new ReverseVerticalDividerDecorationHelper();
    private static final NormalHorizontalDividerDecorationHelper sNormalHorizontalDividerDecorationHelper = new NormalHorizontalDividerDecorationHelper();
    private static final ReverseHorizontalDividerDecorationHelper sReverseHorizontalDividerDecorationHelper = new ReverseHorizontalDividerDecorationHelper();

    @NonNull
    public static DividerDecorationHelper getHelper(int orientation, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            return sNormalVerticalDividerDecorationHelper;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        boolean isReversed = layoutManager.getReverseLayout();

        switch (orientation) {
            default:
            case Orientation.VERTICAL:
                if (isReversed) return sReverseVerticalDividerDecorationHelper;
                return sNormalVerticalDividerDecorationHelper;
            case Orientation.HORIZONTAL:
                if (isReversed) return sReverseHorizontalDividerDecorationHelper;
                return sNormalHorizontalDividerDecorationHelper;
        }
    }

    public abstract void setStart(Rect rect, int start);

    public abstract void setEnd(Rect rect, int end);

    public abstract int getStart(Rect rect);

    public abstract int getEnd(Rect rect);

    public int getOffset(int offset) {
        return offset;
    }

}

class NormalVerticalDividerDecorationHelper extends DividerDecorationHelper {

    NormalVerticalDividerDecorationHelper() {
        // nothing
    }

    @Override
    public void setStart(Rect rect, int start) {
        rect.top = start;
    }

    @Override
    public void setEnd(Rect rect, int end) {
        rect.bottom = end;
    }

    @Override
    public int getStart(Rect rect) {
        return rect.top;
    }

    @Override
    public int getEnd(Rect rect) {
        return rect.bottom;
    }

}

class ReverseVerticalDividerDecorationHelper extends DividerDecorationHelper {

    ReverseVerticalDividerDecorationHelper() {
        // nothing
    }

    @Override
    public void setStart(Rect rect, int start) {
        rect.bottom = start;
    }

    @Override
    public void setEnd(Rect rect, int end) {
        rect.top = end;
    }

    @Override
    public int getStart(Rect rect) {
        return rect.bottom;
    }

    @Override
    public int getEnd(Rect rect) {
        return rect.top;
    }

    @Override
    public int getOffset(int offset) {
        return -offset;
    }

}

class NormalHorizontalDividerDecorationHelper extends DividerDecorationHelper {

    NormalHorizontalDividerDecorationHelper() {
        // nothing
    }

    @Override
    public void setStart(Rect rect, int start) {
        rect.left = start;
    }

    @Override
    public void setEnd(Rect rect, int end) {
        rect.right = end;
    }

    @Override
    public int getStart(Rect rect) {
        return rect.left;
    }

    @Override
    public int getEnd(Rect rect) {
        return rect.right;
    }

}

class ReverseHorizontalDividerDecorationHelper extends DividerDecorationHelper {

    ReverseHorizontalDividerDecorationHelper() {
        // nothing
    }

    @Override
    public void setStart(Rect rect, int start) {
        rect.right = start;
    }

    @Override
    public void setEnd(Rect rect, int end) {
        rect.left = end;
    }

    @Override
    public int getStart(Rect rect) {
        return rect.right;
    }

    @Override
    public int getEnd(Rect rect) {
        return rect.left;
    }

    @Override
    public int getOffset(int offset) {
        return -offset;
    }
}
