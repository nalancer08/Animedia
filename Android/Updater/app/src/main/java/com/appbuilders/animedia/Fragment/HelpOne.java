package com.appbuilders.animedia.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbuilders.animedia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpOne extends Fragment {


    public HelpOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_one, container, false);
    }

}
