package com.omega_r.libs.omegarecyclerview.swipe_menu.listener;

import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuLayout;

public interface SwipeFractionListener {
    void beginMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction);

    void endMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction);
}