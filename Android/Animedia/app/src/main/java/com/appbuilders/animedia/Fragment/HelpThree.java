package com.appbuilders.animedia.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appbuilders.animedia.Controller.UpdateController;
import com.appbuilders.animedia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpThree extends Fragment {


    public HelpThree() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_help_three, container, false);

        // Setting download button
        Button dw = view.findViewById(R.id.update_app);
        dw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), UpdateController.class));
                getActivity().finish();
            }
        });

        return view;
    }
}
