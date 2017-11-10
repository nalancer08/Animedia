package com.appbuilders.animedia.Controller;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appbuilders.animedia.Controls.PlayGifView;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;

public class ChromeWebPlayer extends AppCompatActivity {

    private WebView player;
    private InterstitialAd ad;
    private PlayGifView loader;

    private boolean isRedirected;

    @SuppressLint("SetJavaScriptEnabled")
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
        Chapter chapter = new Chapter(JsonBuilder.stringToJson(chapterString));

        // Getting webView
        this.player = (WebView) findViewById(R.id.player);

        // Getting loader
        this.loader = (PlayGifView) findViewById(R.id.loader);
        this.loader.setImageResource(R.drawable.circular_loader);

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

        if (!chapter.getUrl().equals("")) {

            this.player.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    if (!isRedirected) {
                        //Do something you want when starts loading
                    }

                    isRedirected = false;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    view.loadUrl(url);
                    isRedirected = true;
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    if (!isRedirected) {

                        removeLoader();
                        //player.loadUrl("javascript:(function() { " + "var e = document.getElementById('embed_dock'); e.parentNode.removeChild(e);" + "})()");
                        //player.loadUrl("javascript:(function() { " + "var e = document.getElementById('embed_dock'); e.parentElement.removeChild(e);" + "})()");
                        //player.loadUrl("javascript:(function() { " + "var element = document.getElementsByClassName('button')[0];" + "element.parentNode.removeChild(element);" + "})()");
                        //player.loadUrl("javascript: jQuery('.button').remove();");
                        //player.loadUrl("javascript:alert('hello')");
                        //player.loadUrl("javascript:var e = document.getElementsByClassName('button')[0]; e.parentElement.removeChild(e);");
                        //player.loadUrl("javascript:var e = document.getElementById('embed_dock'); consol e.parentNode.removeChild(e);");
                        //player.loadUrl("javascript:var e = document.getElementById('embed_dock'); e.parentElement.removeChild(e);");
                        //player.loadUrl("javascript:alert('hello')");


                        player.loadUrl("javascript: (function() { var e = document.getElementById('embed_dock'); e.parentNode.removeChild(e) })();");
                    }
                }
            });
            this.player.getSettings().setJavaScriptEnabled(true);
            this.player.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            this.player.getSettings().setPluginState(WebSettings.PluginState.ON);
            this.player.getSettings().setMediaPlaybackRequiresUserGesture(false);
            this.player.setWebChromeClient(new WebChromeClient());
            this.player.loadUrl(chapter.getUrl());
            this.player.setVisibility(View.INVISIBLE);

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

    protected void removeLoader() {
        if (this.loader != null) {
            ((ViewManager) this.loader.getParent()).removeView(this.loader);
            new CountDownTimer(400, 1000) {

                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    player.loadUrl("javascript: (function() { var e = document.getElementById('embed_dock'); e.parentNode.removeChild(e) })();");
                    player.loadUrl("javascript: (function() { var e = document.getElementById('embed_logo'); e.parentNode.removeChild(e) })();");
                    player.setVisibility(View.VISIBLE);
                }
            }.start();
        }
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