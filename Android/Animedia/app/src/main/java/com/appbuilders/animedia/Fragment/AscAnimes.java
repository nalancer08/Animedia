package com.appbuilders.animedia.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.AscAnimesView;
import com.appbuilders.animedia.Views.LatestAnimesView;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 02/11/17
 */

public class AscAnimes extends Fragment {

    Context context;


    public AscAnimes() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public AscAnimes(Context context) {

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dynamic_animes, container, false);
        AbsoluteLayout baseLayout = (AbsoluteLayout) view.findViewById(R.id.contentPanel);
        new AscAnimesView(this.context != null ? this.context : getContext(), baseLayout);
        return view;
    }
}
