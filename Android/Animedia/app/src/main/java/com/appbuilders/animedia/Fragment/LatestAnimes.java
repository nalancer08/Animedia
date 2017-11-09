package com.appbuilders.animedia.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.AnimesFixView;
import com.appbuilders.animedia.Views.LatestAnimesView;
import com.appbuilders.surface.SurfaceActivityView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestAnimes extends Fragment {

    AnimesFixView surface;

    @SuppressLint("ValidFragment")
    public LatestAnimes(AnimesFixView surface) {
        // Required empty public constructor
        this.surface = surface;
    }

    public LatestAnimes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dynamic_animes, container, false);
        AbsoluteLayout baseLayout = (AbsoluteLayout) view.findViewById(R.id.contentPanel);
        new LatestAnimesView(getContext(), baseLayout);

        //this.surface.onSwipe(view);

        return view;
    }
}