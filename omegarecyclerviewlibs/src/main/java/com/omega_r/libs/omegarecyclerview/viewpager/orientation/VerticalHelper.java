package com.omega_r.libs.omegarecyclerview.viewpager.orientation;

import android.graphics.Point;
import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.Direction;
import com.omega_r.libs.omegarecyclerview.viewpager.ViewPagerLayoutManager;

public class VerticalHelper implements OrientationHelper {

    @Override
    public int getViewEnd(int recyclerWidth, int recyclerHeight) {
        return recyclerHeight;
    }

    @Override
    public int getDistanceToChangeCurrent(int childWidth, int childHeight) {
        return childHeight;
    }

    @Override
    public void setCurrentViewCenter(Point recyclerCenter, int scrolled, Point outPoint) {
        int newY = recyclerCenter.y - scrolled;
        outPoint.set(recyclerCenter.x, newY);
    }

    @Override
    public void shiftViewCenter(Direction direction, int shiftAmount, Point outCenter) {
        int newY = outCenter.y + direction.applyTo(shiftAmount);
        outCenter.set(outCenter.x, newY);
    }

    @Override
    public void offsetChildren(int amount, ViewPagerLayoutManager lm) {
        lm.offsetChildrenVertical(amount);
    }

    @Override
    public float getDistanceFromCenter(Point center, int viewCenterX, int viewCenterY) {
        return viewCenterY - center.y;
    }

    @Override
    public boolean isViewVisible(
            Point viewCenter, int halfWidth, int halfHeight, int endBound,
            int extraSpace) {
        int viewTop = viewCenter.y - halfHeight;
        int viewBottom = viewCenter.y + halfHeight;
        return viewTop < (endBound + extraSpace) && viewBottom > -extraSpace;
    }

    @Override
    public boolean hasNewBecomeVisible(ViewPagerLayoutManager lm) {
        View firstChild = lm.getFirstChild(), lastChild = lm.getLastChild();
        int topBound = -lm.getExtraLayoutSpace();
        int bottomBound = lm.getHeight() + lm.getExtraLayoutSpace();
        boolean isNewVisibleFromTop = lm.getDecoratedTop(firstChild) > topBound
                && lm.getPosition(firstChild) > 0;
        boolean isNewVisibleFromBottom = lm.getDecoratedBottom(lastChild) < bottomBound
                && lm.getPosition(lastChild) < lm.getItemCount() - 1;
        return isNewVisibleFromTop || isNewVisibleFromBottom;
    }

    @Override
    public int getFlingVelocity(int velocityX, int velocityY) {
        return velocityY;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int getPendingDx(int pendingScroll) {
        return 0;
    }

    @Override
    public int getPendingDy(int pendingScroll) {
        return pendingScroll;
    }
}
