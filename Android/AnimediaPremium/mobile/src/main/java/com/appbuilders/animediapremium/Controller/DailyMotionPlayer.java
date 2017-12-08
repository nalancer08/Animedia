package com.appbuilders.animediapremium.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.appbuilders.animediapremium.Libraries.JsonBuilder;
import com.appbuilders.animediapremium.R;
import com.dailymotion.android.player.sdk.PlayerWebView;

import org.json.JSONException;
import org.json.JSONObject;

public class DailyMotionPlayer extends AppCompatActivity {

    private PlayerWebView mVideoView;
    private String dailyMotionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Get intent
        Intent intent = getIntent();

        // Get JSONObject anime
        String animeString = intent.getStringExtra("anime");
        Log.d("AB_DEV", "Anime passed ::: " + animeString);
        JSONObject anime = JsonBuilder.stringToJson(animeString);
        this.getMedia(anime);

        mVideoView = (PlayerWebView) findViewById(R.id.dm_player_web_view);

        if (this.dailyMotionId != "") {
            mVideoView.load(this.dailyMotionId);
        }
    }

    protected void getMedia(JSONObject anime) {

        try {

            JSONObject media = anime.getJSONObject("media");
            this.dailyMotionId = media.getString("dailyId");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
