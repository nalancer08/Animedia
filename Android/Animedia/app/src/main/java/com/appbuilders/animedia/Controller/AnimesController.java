package com.appbuilders.animedia.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.AnimesView;

public class AnimesController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_animes_controller);
        AnimesView view = new AnimesView(this);
    }
}
