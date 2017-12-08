package com.appbuilders.animediapremium.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.appbuilders.animediapremium.Views.AnimesFixView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AnimesController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_animes_controller);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        AnimesFixView view = new AnimesFixView(this);
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
