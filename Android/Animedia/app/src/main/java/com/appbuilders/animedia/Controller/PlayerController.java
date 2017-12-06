package com.appbuilders.animedia.Controller;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appbuilders.animedia.Listener.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.afollestad.easyvideoplayer.EasyVideoProgressCallback;
import com.appbuilders.animedia.Controls.EasyClickableVideoPlayer;
import com.appbuilders.animedia.Controls.PlayGifView;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Listener.EasyCustomVideoPlayerClickListener;
import com.appbuilders.animedia.R;
import com.appbuilders.credentials.Rester.ReSTClient;
import com.appbuilders.credentials.Rester.ReSTRequest;
import com.appbuilders.credentials.Rester.ReSTCallback;
import com.appbuilders.credentials.Rester.ReSTResponse;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerController extends AppCompatActivity implements EasyVideoCallback, EasyVideoProgressCallback, EasyCustomVideoPlayerClickListener {

    public final static int PlayerPause = 0;
    public final static int PlayerMiddle = 1;
    public final static int PlayerBack = 2;

    private EasyClickableVideoPlayer mPlayer;

    protected Anime anime;
    protected Chapter chapter;
    protected String magicalUrl = "";

    private InterstitialAd ad;
    private PlayGifView loader;
    private RelativeLayout mediaDetails;
        private ImageView animeCover;
        private TextView animeTitle;
        private TextView mediaTitle;

    private int adFrom = PlayerPause;
    private boolean middleAdShowed = false;
    private boolean isDetailsShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_controller);

        // Setting player
        this.mPlayer = (EasyClickableVideoPlayer) findViewById(R.id.player);

        // Get intent data
        this.getIntentData();

        // Setting player details
        this.setPlayerDetails();

        // Setting ads
        this.setAds();

        // Setting the player
        this.initPlayer();

    }

    /**
     * Method to get intent information
     **/
    private void getIntentData() {

        String chapterString = getIntent().getStringExtra("media");
        String animeString = getIntent().getStringExtra("anime");

        // Setting anime && chapter
        this.anime = new Anime(JsonBuilder.stringToJson(animeString));
        this.chapter = new Chapter(JsonBuilder.stringToJson(chapterString));
    }

    /**
     * Method  to set media player details
     **/
    @SuppressLint("SetTextI18n")
    private void setPlayerDetails() {

        this.mediaDetails = (RelativeLayout) findViewById(R.id.media_details);
        this.animeCover = (ImageView) findViewById(R.id.animeCover);
        this.animeTitle = (TextView) findViewById(R.id.animeTitle);
        this.mediaTitle = (TextView) findViewById(R.id.mediaTitle);

        // Setting anime and media detials
        // Setting anime cover
        Picasso.with(this).load(this.anime.getCover()).into(this.animeCover);

        // Setting anime title
        this.animeTitle.setText(this.anime.getName());

        // Setting media title
        this.mediaTitle.setText(this.chapter.getNumber() +  " - " + this.chapter.getName() + " (" + this.chapter.getAudio() + " )");
    }

    /**
     * Method to set ads implementation
     **/
    private void setAds() {

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

                switch(adFrom) {

                    case PlayerController.PlayerPause:
                        loadAd();
                        break;

                    case PlayerController.PlayerMiddle:
                        loadAd();
                        mPlayer.start();
                        break;

                    case PlayerController.PlayerBack:
                        finish();
                        break;
                }
            }
        });
        this.loadAd();
    }

    private void initPlayer() {

        if (!this.chapter.getUrl().equals("")) {

            // Parsing data
            ReSTClient rest = new ReSTClient(this.chapter.getUrl());
            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_GET, "");
            rest.execute(request, new ReSTCallback() {
                @Override
                public void onSuccess(ReSTResponse response) {

                    String resp = response.body;
                    List<String> extractedUrls = extractUrls(resp);

                    for (String url : extractedUrls) {
                        if (url.contains("mediafire")) {

                            ReSTClient rest = new ReSTClient(url);
                            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_GET, "");
                            rest.execute(request, new ReSTCallback() {
                                @Override
                                public void onSuccess(ReSTResponse response) {

                                    String resp = response.body;
                                    String pag = resp.split("http://download")[1];
                                    pag = pag.split("\"")[0];
                                    String url = pag.split("'")[0];
                                    if (url.equals("")) {
                                        goBack();
                                    } else {
                                        url = "http://download" + url;
                                        setPlayer(url);
                                    }
                                }

                                @Override
                                public void onError(ReSTResponse reSTResponse) {
                                    goBack();
                                }
                            });

                        }
                    }
                }

                @Override
                public void onError(ReSTResponse reSTResponse) {
                    goBack();
                }
            });

        } else {
            this.goBack();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setPlayer(String url) {

        // Setting callbacks
        this.mPlayer.setCallback(this);
        this.mPlayer.setProgressCallback(this);
        this.mPlayer.setOnClickListener(this);

        // Setting url
        this.mPlayer.setSource(Uri.parse(url));

        // Setting configurations
        this.mPlayer.setLeftAction(EasyVideoPlayer.LEFT_ACTION_RESTART);
        this.mPlayer.setRightAction(EasyVideoPlayer.RIGHT_ACTION_NONE);
        this.mPlayer.setHideControlsOnPlay(true);
        this.mPlayer.setAutoPlay(true);
        //this.mPlayer.setInitialPosition(5);
        //this.mPlayer.setVolume(5, 5);
        this.mPlayer.setAutoFullscreen(true);
        this.mPlayer.setLoop(false);
    }

    /**
     * Method to close activity
     **/
    protected void goBack() {
        finish();
    }

    /**
     * Method to request the add
     **/
    protected void loadAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        // Load the adView object witht he request
        this.ad.loadAd(adRequest);
    }

    protected void setAdFrom(int from) {

        this.adFrom = from;
    }

    protected void showPlayerDetails() {

        this.mediaDetails.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceInDown).duration(700).playOn(findViewById(R.id.media_details));
    }

    protected void hidePlayerDetails() {

        YoYo.with(Techniques.BounceInUp).duration(700).playOn(findViewById(R.id.media_details));
        this.mediaDetails.setVisibility(View.INVISIBLE);
    }

    protected void setStatusBarColor() {

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onBackPressed() {

        if (this.ad.isLoaded()) {
            this.setAdFrom(PlayerBack);
            this.ad.show();
        }
    }

    /**
     * Auxiliar method to extract url with regex expresions
     **/
    private static List<String> extractUrls(String text) {

        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((http?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }
        return containedUrls;
    }

    /***********************************************************************************************
     *                               EasyVideoPlayer implemnt methods                              *
     ***********************************************************************************************/

    @Override
    public void onPause() {

        super.onPause();

        // Make sure the player stops playing if the user presses the home button.
        this.mPlayer.pause();
    }

    @Override
    public void onPreparing(EasyClickableVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPrepared(EasyClickableVideoPlayer player) {

        // Setting status bar
        this.setStatusBarColor();
    }

    @Override
    public void onBuffering(int percent) {
        // TODO handle if needed
    }

    @Override
    public void onError(EasyClickableVideoPlayer player, Exception e) {
        // TODO handle
    }

    @Override
    public void onCompletion(EasyClickableVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onRetry(EasyClickableVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onSubmit(EasyClickableVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onStarted(EasyClickableVideoPlayer player) {

        Log.d("DXGOP", "Empezando vdideo");
    }

    @Override
    public void onPaused(EasyClickableVideoPlayer player) {

        // Presenting ad
        this.setAdFrom(PlayerPause);
        this.ad.show();
    }

    @Override
    public void onVideoProgressUpdate(int position, int duration) {

        if (position >= (duration / 2) && !this.middleAdShowed) {

            this.mPlayer.pause();
            this.setAdFrom(PlayerMiddle);
            this.ad.show();
            this.middleAdShowed = true;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick() {

        if (!this.isDetailsShowing) {
            this.showPlayerDetails();
            this.isDetailsShowing = true;
        } else {
            this.hidePlayerDetails();
            this.isDetailsShowing = false;
        }
    }
}