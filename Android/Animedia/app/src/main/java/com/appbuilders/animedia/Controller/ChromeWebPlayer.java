package com.appbuilders.animedia.Controller;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuilders.animedia.Controls.PlayGifView;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfScreen;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

public class ChromeWebPlayer extends AppCompatActivity {

    protected Anime anime;
    protected Chapter chapter;

    private WebView player;
    private InterstitialAd ad;
    private PlayGifView loader;
    private RelativeLayout mediaDetails;
        private ImageView animeCover;
        private TextView animeTitle;
        private TextView mediaTitle;

    private boolean isRedirected;
    private boolean firstClickInWebView = false;
    private CountDownTimer showTimer;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrome_web_player);

        // Setting landscape
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Setting fullscreen
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Getting chapter and anime intent data
        String chapterString = getIntent().getStringExtra("media");
        String animeString = getIntent().getStringExtra("anime");

        //Log.d("DXGOP", "PORFII::: " + animeString);

        // Setting anime && chapter
        this.anime = new Anime(JsonBuilder.stringToJson(animeString));
        this.chapter = new Chapter(JsonBuilder.stringToJson(chapterString));

        // Setting layout views
        this.player = (WebView) findViewById(R.id.player);
        this.mediaDetails = (RelativeLayout) findViewById(R.id.media_details);
            this.animeCover = (ImageView) findViewById(R.id.animeCover);
            this.animeTitle = (TextView) findViewById(R.id.animeTitle);
            this.mediaTitle = (TextView) findViewById(R.id.mediaTitle);

        // Setting anime and media detials
        this.setMediaDetails();

        // Setting loader
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // chromium, enable hardware acceleration
                this.player.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                // older android version, disable hardware acceleration
                this.player.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            // Setting click for media details
            this.setClickableMediaDetails();

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
        if (this.loader != null && this.loader.getParent() != null) {
            ((ViewManager) this.loader.getParent()).removeView(this.loader);
            new CountDownTimer(1000, 1000) {

                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    player.loadUrl("javascript: (function() { var e = document.getElementById('embed_dock'); e.parentNode.removeChild(e) })();");
                    player.loadUrl("javascript: (function() { var e = document.getElementById('embed_logo'); e.parentNode.removeChild(e) })();");
                    player.setVisibility(View.VISIBLE);
                }
            }.start();
        }
    }

    protected void loadAd() {

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Load the adView object witht he request
        this.ad.loadAd(adRequest);
    }

    protected void goBack() {
        finish();
    }

    @SuppressLint("SetTextI18n")
    protected void setMediaDetails() {

        // Setting anime cover
        Picasso.with(this).load(this.anime.getCover()).into(this.animeCover);

        // Setting anime title
        this.animeTitle.setText(this.anime.getName());

        // Setting media title
        this.mediaTitle.setText(this.chapter.getNumber() +  " - " + this.chapter.getName() + " (" + this.chapter.getAudio() + " )");

    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setClickableMediaDetails() {

        this.player.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (firstClickInWebView == false) {
                    firstClickInWebView = true;
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        if (mediaDetails.getVisibility() == View.INVISIBLE) {

                            mediaDetails.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.BounceInDown).duration(700).playOn(findViewById(R.id.media_details));
                            initializeTimer();

                        } else {

                            YoYo.with(Techniques.BounceInUp).duration(700).playOn(findViewById(R.id.media_details));
                            mediaDetails.setVisibility(View.INVISIBLE);
                            cancelTimer();
                        }
                    }
                }
                return false;
            }
        });
    }

    protected void initializeTimer() {

        this.showTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                YoYo.with(Techniques.BounceInUp).duration(700).playOn(findViewById(R.id.media_details));
                mediaDetails.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    protected void cancelTimer() {

        this.showTimer.cancel();
    }
}