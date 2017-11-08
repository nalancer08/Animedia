package com.appbuilders.animedia.Controller;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class ChromeWebPlayer extends AppCompatActivity {

    private WebView player;
    private Chapter chapter;
    private InterstitialAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrome_web_player);

        // Setting landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Setting fullscreen
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Getting chapter intent data
        String chapterString = getIntent().getStringExtra("media");

        // Setting chapter
        this.chapter = new Chapter(JsonBuilder.stringToJson(chapterString));

        // Getting webView
        this.player = (WebView) findViewById(R.id.player);

        // Initializing ad
        this.ad = new InterstitialAd(this);
        this.ad.setAdUnitId("ca-app-pub-8714411824921031/3504275839");
        this.ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                goBack();
            }
        });
        this.loadAd();

        if (!this.chapter.getUrl().equals("")) {

            this.player.setWebViewClient(new WebViewClient());
            this.player.getSettings().setJavaScriptEnabled(true);
            this.player.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            this.player.getSettings().setPluginState(WebSettings.PluginState.ON);
            this.player.getSettings().setMediaPlaybackRequiresUserGesture(false);
            this.player.setWebChromeClient(new WebChromeClient());
            this.player.loadUrl(this.chapter.getUrl());

        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {

        if (ad.isLoaded()) {

            this.player.stopLoading();
            this.player.setVisibility(View.GONE);
            this.player.destroy();
            ad.show();
        }


        /*new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (ad.isLoaded()) {
                            ad.show();
                        }
                    }
                });
            }
        };*/
    }

    void loadAd() {

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Load the adView object witht he request
        this.ad.loadAd(adRequest);
    }

    protected void goBack() {

        finish();
    }
}