package com.omega_r.libs.omegarecyclerview.swipe_menu;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SwipeMenuHelper {

    private static final int INVALID_POSITION = -1;

    private Callback mCallback;

    private SwipeHorizontalMenuLayout mOldSwipedView;

    private int mOldTouchedPosition = INVALID_POSITION;

    public SwipeMenuHelper(Context context, Callback callback) {
        mCallback = callback;
        ViewConfiguration.get(context);
    }

    public boolean handleListDownTouchEvent(MotionEvent ev, boolean defaultIntercepted) {
        boolean isIntercepted = defaultIntercepted;
        View touchingView = findChildViewUnder((int) ev.getX(), (int) ev.getY());
        int touchingPosition;

        if (touchingView != null) {
            touchingPosition = mCallback.getPositionForView(touchingView);
        } else {
            touchingPosition = INVALID_POSITION;
        }

        if (touchingPosition != mOldTouchedPosition && mOldSwipedView != null) {
            if (mOldSwipedView.isMenuOpen() || mOldSwipedView.isNotInPlace()) {
                mOldSwipedView.smoothCloseMenu();
                isIntercepted = true;
            }
        }

        touchingView = mCallback.transformTouchView(touchingPosition, touchingView);

        if (touchingView instanceof ViewGroup) {
            View itemView = getSwipeMenuView((ViewGroup) touchingView);
            if (itemView instanceof SwipeHorizontalMenuLayout) {
                mOldSwipedView = (SwipeHorizontalMenuLayout) itemView;
                mOldTouchedPosition = touchingPosition;
            }
        }

        if (isIntercepted) {
            mOldSwipedView = null;
            mOldTouchedPosition = INVALID_POSITION;
        }

        return isIntercepted;
    }

    private View getSwipeMenuView(ViewGroup itemView) {
        if (itemView instanceof SwipeHorizontalMenuLayout) {
            return itemView;
        }

        List<View> unvisited = new ArrayList<>();
        unvisited.add(itemView);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);

            if (!(child instanceof ViewGroup)) continue;

            if (child instanceof SwipeHorizontalMenuLayout) return child;

            ViewGroup group = (ViewGroup) child;
            int childCount = group.getChildCount();

            for (int i = 0; i < childCount; i++) {
                unvisited.add(group.getChildAt(i));
            }
        }

        return itemView;
    }

    private View findChildViewUnder(float x, float y) {
        int count = mCallback.getRealChildCount();

        for (int i = count - 1; i >= 0; i--) {
            View child = mCallback.getRealChildAt(i);
            float translationX = child.getTranslationX();
            float translationY = child.getTranslationY();

            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {

                return child;
            }
        }

        return null;
    }

    public interface Callback {

        int getPositionForView(View view);

        int getRealChildCount();

        View getRealChildAt(int index);

        View transformTouchView(int touchPosition, View touchView);

    }
}
