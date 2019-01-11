package com.omega_r.omegarecyclerview.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.omega_r.libs.omegarecyclerview.viewpager.OmegaPagerRecyclerView;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.*;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.pagination_example.Image;
import com.omega_r.omegarecyclerview.pagination_example.ImageAdapter;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ViewPagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private OmegaPagerRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        initSpinner();
        initRecycler();
    }

    private void initRecycler() {
        mRecyclerView = findViewById(R.id.recyclerview);
        ImageAdapter adapter = new ImageAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.addValues(Image.createImageList(10));
    }

    private void initSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<DefaultTransformers> adapter = new Adapter(this, R.layout.item_spinner, DefaultTransformers.values());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DefaultTransformers transformer = DefaultTransformers.values()[position];
        ItemTransformer itemTransformer = null;
        switch (transformer) {
            case None:
                itemTransformer = null;
                break;
            case Accordion:
                itemTransformer = new AccordionTransformer();
                break;
            case BackgroundToForeground:
                itemTransformer = new BackgroundToForegroundTransformer();
                break;
            case CubeIn:
                itemTransformer = new CubeInTransformer();
                break;
            case CubeOut:
                itemTransformer = new CubeOutTransformer();
                break;
            case DepthPage:
                itemTransformer = new DepthPageTransformer();
                break;
            case Fade:
                itemTransformer = new FadeTransformer();
                break;
            case Flip:
                itemTransformer = new FlipTransformer();
                break;
            case ForegroundToBackground:
                itemTransformer = new ForegroundToBackgroundTransformer();
                break;
            case RotateDown:
                itemTransformer = new RotateDownTransformer();
                break;
            case RotateUp:
                itemTransformer = new RotateUpTransformer();
                break;
            case Stack:
                itemTransformer = new StackTransformer();
                break;
            case Tablet:
                itemTransformer = new TabletTransformer();
                break;
            case ZoomIn:
                itemTransformer = new ZoomInTransformer();
                break;
            case ZoomOutSlide:
                itemTransformer = new ZoomOutSlideTransformer();
                break;
        }
        mRecyclerView.setItemTransformer(itemTransformer);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing
    }

    private class Adapter extends ArrayAdapter<DefaultTransformers> {

        private final DefaultTransformers[] array;

        Adapter(@NonNull Context context, int resource, @NonNull DefaultTransformers[] array) {
            super(context, resource, array);
            this.array = array;
        }

        @NonNull
        @Override
        @SuppressLint("ViewHolder")
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.item_spinner, parent, false);
            TextView textView = view.findViewById(R.id.textview);
            textView.setText(array[position].getTitle());
            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.item_spinner, parent, false);
            TextView textView = view.findViewById(R.id.textview);
            textView.setText(array[position].getTitle());
            return view;
        }
    }


    private enum DefaultTransformers {
        None("None"),
        Accordion("AccordionTransformer"),
        BackgroundToForeground("BackgroundToForeground"),
        CubeIn("CubeIn"),
        CubeOut("CubeOut"),
        DepthPage("DepthPage"),
        Fade("Fade"),
        Flip("Flip"),
        ForegroundToBackground("ForegroundToBackground"),
        RotateDown("RotateDown"),
        RotateUp("RotateUp"),
        Stack("Stack"),
        Tablet("Tablet"),
        ZoomIn("ZoomIn"),
        ZoomOutSlide("ZoomOutSlide");

        private String title;

        DefaultTransformers(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

}

