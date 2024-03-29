package com.appbuilders.animediapremium.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsoluteLayout;

import com.appbuilders.animediapremium.R;
import com.appbuilders.animediapremium.Views.HomeViewFixed;

public class MenuController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Remove action bar
        ((AppCompatActivity)this).getSupportActionBar().hide();

        setContentView(R.layout.activity_menu_controller);

        AbsoluteLayout abs = (AbsoluteLayout) findViewById(R.id.content);

        HomeViewFixed view = new HomeViewFixed(this, abs);
    }
}
