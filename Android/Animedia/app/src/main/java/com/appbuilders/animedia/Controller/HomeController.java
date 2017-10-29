package com.appbuilders.animedia.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.ListView;

import com.appbuilders.animedia.Adapter.LastAnimeAdapter;
import com.appbuilders.animedia.R;
import com.appbuilders.animedia.Views.HomeView;
import com.appbuilders.animedia.Views.HomeViewFixed;

import java.util.ArrayList;

public class HomeController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Remove action bar
        ((AppCompatActivity)this).getSupportActionBar().hide();

        setContentView(R.layout.activity_home_controller);

        AbsoluteLayout abs = (AbsoluteLayout) findViewById(R.id.content);

        HomeViewFixed view = new HomeViewFixed(this, abs);
    }
}
