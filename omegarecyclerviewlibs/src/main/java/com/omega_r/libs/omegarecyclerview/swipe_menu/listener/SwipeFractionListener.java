package com.omega_r.libs.omegarecyclerview.swipe_menu.listener;

import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeDirection;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuLayout;

public interface SwipeFractionListener {

    void onSwipeMenuFraction(SwipeMenuLayout swipeMenuLayout, SwipeDirection direction, float fraction);

}