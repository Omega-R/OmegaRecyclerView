package com.omega_r.omegarecyclerview.expandable_example.core;

public class Quote {
    private String mQuote;

    public Quote(String quote) {
        mQuote = quote;
    }

    public String getQuote() {
        return mQuote;
    }

    public void setQuote(String quote) {
        mQuote = quote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return mQuote.equals(quote.mQuote);
    }

    @Override
    public int hashCode() {
        return mQuote.hashCode();
    }
}
