package com.omega_r.omegarecyclerview.expandable_example.core;

import androidx.annotation.Nullable;


import com.omega_r.libs.omegarecyclerview_expandable.data.GroupProvider;

import java.util.Arrays;
import java.util.List;

public class SimpleData implements GroupProvider<QuoteGlobalInfo, Quote> {

    private QuoteGlobalInfo mQuoteGlobalInfo;
    private List<Quote> mQuotes;

    public static SimpleData from(QuoteGlobalInfo title, Quote... quotes) {
        return new SimpleData(title, Arrays.asList(quotes));
    }

    public SimpleData(QuoteGlobalInfo quoteGlobalInfo, List<Quote> quotes) {
        mQuoteGlobalInfo = quoteGlobalInfo;
        mQuotes = quotes;
    }

    public QuoteGlobalInfo getQuoteGlobalInfo() {
        return mQuoteGlobalInfo;
    }

    public void setQuoteGlobalInfo(QuoteGlobalInfo quoteGlobalInfo) {
        mQuoteGlobalInfo = quoteGlobalInfo;
    }

    public List<Quote> getQuotes() {
        return mQuotes;
    }

    public void setQuotes(List<Quote> quotes) {
        mQuotes = quotes;
    }

    @Override
    public QuoteGlobalInfo provideGroup() {
        return mQuoteGlobalInfo;
    }

    @Override
    public List<Quote> provideChilds() {
        return mQuotes;
    }

    @Nullable
    @Override
    public Integer provideStickyId() {
        return mQuoteGlobalInfo.getYear();
    }
}
