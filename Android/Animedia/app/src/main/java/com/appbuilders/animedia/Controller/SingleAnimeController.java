package com.appbuilders.animedia.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.SingleAnimeView;

public class SingleAnimeController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        new SingleAnimeView(this);
    }
}
