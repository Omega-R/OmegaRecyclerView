package com.omega_r.libs.omegarecyclerview.pagination;

import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface PaginationViewCreator {

    @Nullable
    View createPaginationView(ViewGroup parent, LayoutInflater inflater);
    @Nullable
    View createPaginationErrorView(ViewGroup parent, LayoutInflater inflater);

}
