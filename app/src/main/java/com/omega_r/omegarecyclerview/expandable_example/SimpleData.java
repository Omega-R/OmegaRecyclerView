package com.omega_r.omegarecyclerview.expandable_example;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.GroupProvider;

import java.util.Arrays;
import java.util.List;

public class SimpleData implements GroupProvider<String, String> {

    private String mTitle;
    private List<String> mQuotes;

    public static SimpleData from(String title, String... quotes) {
        return new SimpleData(title, Arrays.asList(quotes));
    }

    public SimpleData(String title, List<String> quotes) {
        mTitle = title;
        mQuotes = quotes;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<String> getQuotes() {
        return mQuotes;
    }

    public void setQuotes(List<String> quotes) {
        mQuotes = quotes;
    }

    @Override
    public String provideGroup() {
        return mTitle;
    }

    @Override
    public List<String> provideChilds() {
        return mQuotes;
    }
}
