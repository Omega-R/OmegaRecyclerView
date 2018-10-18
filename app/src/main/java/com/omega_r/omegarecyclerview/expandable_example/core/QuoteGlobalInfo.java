package com.omega_r.omegarecyclerview.expandable_example.core;

public class QuoteGlobalInfo {
    private String mTitle;
    private int mYear;

    public QuoteGlobalInfo(String title, int year) {
        mTitle = title;
        mYear = year;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }
}
