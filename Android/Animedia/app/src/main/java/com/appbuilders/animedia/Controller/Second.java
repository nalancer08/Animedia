package com.appbuilders.animedia.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appbuilders.animedia.R;
import com.dailymotion.android.player.sdk.PlayerWebView;

public class Second extends AppCompatActivity {

    private PlayerWebView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mVideoView = (PlayerWebView) findViewById(R.id.dm_player_web_view);
        mVideoView.load("k4mTupurPBY66DpeM8d");


    }
}
