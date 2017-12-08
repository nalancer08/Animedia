package com.appbuilders.animediapremium.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.appbuilders.animediapremium.R;
import com.appbuilders.animediapremium.Views.AnimesFixView;
import com.appbuilders.animediapremium.Views.AscAnimesView;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 02/11/17
 */

public class AscAnimes extends Fragment {

    AnimesFixView surface;
    Context context;

    public AscAnimes() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public AscAnimes(AnimesFixView surface) {

        this.surface = surface;
        this.context = surface.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dynamic_animes, container, false);
        AbsoluteLayout baseLayout = (AbsoluteLayout) view.findViewById(R.id.contentPanel);
        new AscAnimesView(this.context != null ? this.context : getContext(), baseLayout);
        //this.surface.onSwipe(view);
        return view;
    }
}