package com.appbuilders.animedia.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.appbuilders.animedia.FragmentViews.AllAnimesView;
import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.AnimesFixView;
import com.appbuilders.animedia.Views.GenresAnimesView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllAnimes extends Fragment {

    AnimesFixView surface;
    Context context;

    public AllAnimes() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public AllAnimes(AnimesFixView surface) {

        this.surface = surface;
        this.context = surface.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_dynamic_animes, container, false);
        AbsoluteLayout baseLayout = (AbsoluteLayout) view.findViewById(R.id.contentPanel);
        new AllAnimesView(this.context != null ? this.context : getContext(), baseLayout);
        //this.surface.onSwipe(view);
        return view;
    }
}