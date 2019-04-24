package com.omega_r.libs.omegarecyclerview.swipe_menu.listener;


import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeDirection;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuLayout;

public interface SwipeSwitchListener {

    void onSwipeMenuOpened(SwipeMenuLayout swipeMenuLayout, SwipeDirection direction);

    void onSwipeMenuClosed(SwipeMenuLayout swipeMenuLayout, SwipeDirection direction);

}
