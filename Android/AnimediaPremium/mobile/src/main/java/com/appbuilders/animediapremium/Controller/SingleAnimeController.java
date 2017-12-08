package com.appbuilders.animediapremium.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.appbuilders.animediapremium.Views.SingleAnimeView;

public class SingleAnimeController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        new SingleAnimeView(this);
    }
}
