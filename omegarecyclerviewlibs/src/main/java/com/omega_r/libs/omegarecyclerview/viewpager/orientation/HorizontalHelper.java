package com.omega_r.libs.omegarecyclerview.viewpager.orientation;

import android.graphics.Point;
import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.Direction;
import com.omega_r.libs.omegarecyclerview.viewpager.ViewPagerLayoutManager;


public class HorizontalHelper implements OrientationHelper {

    @Override
    public int getViewEnd(int recyclerWidth, int recyclerHeight) {
        return recyclerWidth;
    }

    @Override
    public int getDistanceToChangeCurrent(int childWidth, int childHeight) {
        return childWidth;
    }

    @Override
    public void setCurrentViewCenter(Point recyclerCenter, int scrolled, Point outPoint) {
        int newX = recyclerCenter.x - scrolled;
        outPoint.set(newX, recyclerCenter.y);
    }

    @Override
    public void shiftViewCenter(Direction direction, int shiftAmount, Point outCenter) {
        int newX = outCenter.x + direction.applyTo(shiftAmount);
        outCenter.set(newX, outCenter.y);
    }

    @Override
    public boolean isViewVisible(Point viewCenter, int halfWidth, int halfHeight,
                                 int endBound, int extraSpace) {
        int viewLeft = viewCenter.x - halfWidth;
        int viewRight = viewCenter.x + halfWidth;
        return viewLeft < (endBound + extraSpace) && viewRight > -extraSpace;
    }

    @Override
    public boolean hasNewBecomeVisible(ViewPagerLayoutManager lm) {
        View firstChild = lm.getFirstChild(), lastChild = lm.getLastChild();
        int leftBound = -lm.getExtraLayoutSpace();
        int rightBound = lm.getWidth() + lm.getExtraLayoutSpace();
        boolean isNewVisibleFromLeft = lm.getDecoratedLeft(firstChild) > leftBound
                && lm.getPosition(firstChild) > 0;
        boolean isNewVisibleFromRight = lm.getDecoratedRight(lastChild) < rightBound
                && lm.getPosition(lastChild) < lm.getItemCount() - 1;
        return isNewVisibleFromLeft || isNewVisibleFromRight;
    }

    @Override
    public void offsetChildren(int amount, ViewPagerLayoutManager lm) {
        lm.offsetChildrenHorizontal(amount);
    }

    @Override
    public float getDistanceFromCenter(Point center, int viewCenterX, int viewCenterY) {
        return viewCenterX - center.x;
    }

    @Override
    public int getFlingVelocity(int velocityX, int velocityY) {
        return velocityX;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public int getPendingDx(int pendingScroll) {
        return pendingScroll;
    }

    @Override
    public int getPendingDy(int pendingScroll) {
        return 0;
    }
}
