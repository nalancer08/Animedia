package com.appbuilders.animedia.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.LatestAnimesView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestAnimes extends Fragment {


    public LatestAnimes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dynamic_animes, container, false);
        AbsoluteLayout baseLayout = (AbsoluteLayout) view.findViewById(R.id.contentPanel);
        new LatestAnimesView(getContext(), baseLayout);
        return view;
    }
}