package com.omega_r.libs.omegarecyclerview_fastscroll;

public interface FastScrollStateChangeListener {

    /**
     * Called when fast scrolling begins
     */
    void onFastScrollStart(OmegaFastScrollerLayout fastScrollerLayout);

    /**
     * Called when fast scrolling ends
     */
    void onFastScrollStop(OmegaFastScrollerLayout fastScrollerLayout);

}
