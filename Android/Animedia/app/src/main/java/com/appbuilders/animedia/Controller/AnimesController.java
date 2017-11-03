package com.appbuilders.animedia.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.AnimesFixView;
import com.appbuilders.animedia.Views.AnimesView;

public class AnimesController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_animes_controller);
        AnimesFixView view = new AnimesFixView(this);
    }
}
