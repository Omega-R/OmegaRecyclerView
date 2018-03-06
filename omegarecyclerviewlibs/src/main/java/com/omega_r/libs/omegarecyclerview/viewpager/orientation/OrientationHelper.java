package com.omega_r.libs.omegarecyclerview.viewpager.orientation;

import android.graphics.Point;

import com.omega_r.libs.omegarecyclerview.viewpager.Direction;
import com.omega_r.libs.omegarecyclerview.viewpager.ViewPagerLayoutManager;

public interface OrientationHelper {

    int getViewEnd(int recyclerWidth, int recyclerHeight);

    int getDistanceToChangeCurrent(int childWidth, int childHeight);

    void setCurrentViewCenter(Point recyclerCenter, int scrolled, Point outPoint);

    void shiftViewCenter(Direction direction, int shiftAmount, Point outCenter);

    int getFlingVelocity(int velocityX, int velocityY);

    int getPendingDx(int pendingScroll);

    int getPendingDy(int pendingScroll);

    void offsetChildren(int amount, ViewPagerLayoutManager lm);

    float getDistanceFromCenter(Point center, int viewCenterX, int viewCenterY);

    boolean isViewVisible(Point center, int halfWidth, int halfHeight, int endBound, int extraSpace);

    boolean hasNewBecomeVisible(ViewPagerLayoutManager lm);

    boolean canScrollVertically();

    boolean canScrollHorizontally();
}