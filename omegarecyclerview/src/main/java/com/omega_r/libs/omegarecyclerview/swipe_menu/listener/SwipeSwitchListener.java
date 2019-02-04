package com.omega_r.libs.omegarecyclerview.swipe_menu.listener;


import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuLayout;

public interface SwipeSwitchListener {

    void beginMenuClosed(SwipeMenuLayout swipeMenuLayout);

    void beginMenuOpened(SwipeMenuLayout swipeMenuLayout);

    void endMenuClosed(SwipeMenuLayout swipeMenuLayout);

    void endMenuOpened(SwipeMenuLayout swipeMenuLayout);

}
