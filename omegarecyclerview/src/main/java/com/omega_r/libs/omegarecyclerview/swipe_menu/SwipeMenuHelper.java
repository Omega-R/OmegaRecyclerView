package com.omega_r.libs.omegarecyclerview.swipe_menu;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SwipeMenuHelper {

    private static final int INVALID_POSITION = -1;

    private final Callback mCallback;
    @Nullable
    private SwipeHorizontalMenuLayout mOldSwipedView;
    private int mOldTouchedPosition = INVALID_POSITION;

    public SwipeMenuHelper(Callback callback) {
        mCallback = callback;
    }

    public boolean handleTouchEventForSecurity(MotionEvent ev, boolean defaultResult) {
        if (mOldSwipedView == null ||
                ev.getActionIndex() == 0 ||
                ev.getActionMasked() != MotionEvent.ACTION_POINTER_DOWN) return defaultResult;

        int pointerId = ev.getPointerId(ev.getActionIndex());
        SwipeHorizontalMenuLayout touchingView = getTouchingView(ev.getX(pointerId), ev.getY(pointerId));
        return mOldSwipedView.equals(touchingView) && defaultResult;
    }

    public boolean handleInterceptTouchEvent(MotionEvent ev, boolean defaultResult) {
        if (ev.getActionIndex() != 0 || ev.getAction() != MotionEvent.ACTION_DOWN) return defaultResult;

        boolean isIntercepted = defaultResult;
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

        touchingView = extractSwipeMenuView(mCallback.transformTouchView(touchingPosition, touchingView));
        if (touchingView != null) {
            mOldSwipedView = (SwipeHorizontalMenuLayout) touchingView;
            mOldTouchedPosition = touchingPosition;
        }
        if (isIntercepted) {
            mOldSwipedView = null;
            mOldTouchedPosition = INVALID_POSITION;
        }

        return isIntercepted;
    }

    private SwipeHorizontalMenuLayout getTouchingView(float eventX, float eventY) {
        View touchingView = findChildViewUnder(eventX, eventY);
        if (touchingView != null) {
            int touchingPosition = mCallback.getPositionForView(touchingView);
            touchingView = mCallback.transformTouchView(touchingPosition, touchingView);
        }
        return extractSwipeMenuView(touchingView);
    }

    @Nullable
    private SwipeHorizontalMenuLayout extractSwipeMenuView(@Nullable View view) {
        if (view instanceof SwipeHorizontalMenuLayout) return (SwipeHorizontalMenuLayout) view;

        List<View> unvisited = new ArrayList<>();
        if (view != null) unvisited.add(view);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);

            if (child instanceof SwipeHorizontalMenuLayout) return (SwipeHorizontalMenuLayout) child;
            if (!(child instanceof ViewGroup)) continue;

            ViewGroup group = (ViewGroup) child;
            int childCount = group.getChildCount();

            for (int i = 0; i < childCount; i++) {
                unvisited.add(group.getChildAt(i));
            }
        }

        return null;
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
