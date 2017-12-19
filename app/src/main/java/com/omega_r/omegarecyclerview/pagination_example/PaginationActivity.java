package com.omega_r.omegarecyclerview.pagination_example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.pagination.OnPageRequestListener;
import com.omega_r.omegarecyclerview.R;

public class PaginationActivity extends AppCompatActivity implements OnPageRequestListener, ImageAdapter.Callback {

    private OmegaRecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private int mCounter;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagination);
        mRecyclerView = findViewById(R.id.recyclerview);

        mRecyclerView.setPaginationCallback(this);
        mAdapter = new ImageAdapter();
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.showProgressPagination();
    }

    @Override
    public void onPageRequest(int page) {
        mCounter++;

        if (mCounter > 6) {
            mRecyclerView.hidePagination();
            return;
        }

        if (mCounter == 3) {
            mRecyclerView.showErrorPagination();
            return;
        }

        downloadItems();
    }

    @Override
    public int getPagePreventionForEnd() {
        return 5;
    }

    @Override
    public void onRetryClicked() {
        mRecyclerView.showProgressPagination();
        downloadItems();
    }

    private void downloadItems() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addValues(Image.createImageList(10));
            }
        }, 3000);
    }
}
