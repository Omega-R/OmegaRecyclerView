package com.omega_r.omegarecyclerview.expandable_example.core;

import androidx.annotation.Nullable;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.GroupProvider;

import java.util.Arrays;
import java.util.List;

public class SimpleData implements GroupProvider<QuoteGlobalInfo, String> {

    private QuoteGlobalInfo mQuoteGlobalInfo;
    private List<String> mQuotes;

    public static SimpleData from(QuoteGlobalInfo title, String... quotes) {
        return new SimpleData(title, Arrays.asList(quotes));
    }

    public SimpleData(QuoteGlobalInfo quoteGlobalInfo, List<String> quotes) {
        mQuoteGlobalInfo = quoteGlobalInfo;
        mQuotes = quotes;
    }

    public QuoteGlobalInfo getQuoteGlobalInfo() {
        return mQuoteGlobalInfo;
    }

    public void setQuoteGlobalInfo(QuoteGlobalInfo quoteGlobalInfo) {
        mQuoteGlobalInfo = quoteGlobalInfo;
    }

    public List<String> getQuotes() {
        return mQuotes;
    }

    public void setQuotes(List<String> quotes) {
        mQuotes = quotes;
    }

    @Override
    public QuoteGlobalInfo provideGroup() {
        return mQuoteGlobalInfo;
    }

    @Override
    public List<String> provideChilds() {
        return mQuotes;
    }

    @Nullable
    @Override
    public Integer provideStickyId() {
        return mQuoteGlobalInfo.getYear();
    }
}
