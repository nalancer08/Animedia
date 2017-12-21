package com.appbuilders.animediapremium.Controller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.afollestad.easyvideoplayer.EasyVideoProgressCallback;
import com.appbuilders.animediapremium.Controls.EasyClickableVideoPlayer;
import com.appbuilders.animediapremium.Core.Anime;
import com.appbuilders.animediapremium.Core.Chapter;
import com.appbuilders.animediapremium.Core.ChapterAdvance;
import com.appbuilders.animediapremium.Core.Credentials;
import com.appbuilders.animediapremium.Core.WatchedChapters;
import com.appbuilders.animediapremium.Libraries.JsonBuilder;
import com.appbuilders.animediapremium.Libraries.JsonFileManager;
import com.appbuilders.animediapremium.Listener.EasyCustomVideoPlayerClickListener;
import com.appbuilders.animediapremium.Listener.EasyVideoCallback;
import com.appbuilders.animediapremium.R;
import com.appbuilders.credentials.Rester.ReSTCallback;
import com.appbuilders.credentials.Rester.ReSTClient;
import com.appbuilders.credentials.Rester.ReSTRequest;
import com.appbuilders.credentials.Rester.ReSTResponse;
import com.brouding.simpledialog.SimpleDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerController extends AppCompatActivity implements EasyVideoCallback, EasyVideoProgressCallback, EasyCustomVideoPlayerClickListener {

    public final static int PlayerPause = 0;
    public final static int PlayerMiddle = 1;
    public final static int PlayerFinish = 2;
    public final static int PlayerBack = 3;

    public final static int PlayerUserPause = 0;
    public final static int PlayerReportPause = 1;

    private EasyClickableVideoPlayer mPlayer;

    protected Anime anime;
    protected Chapter chapter;

    protected ArrayList<Chapter> chapters = null;
    protected ChapterAdvance advance = null;

    private RelativeLayout mediaDetails;
        private ImageView animeCover;
        private TextView animeTitle;
        private TextView mediaTitle;
        private Button mediaReport;

    private int adFrom = PlayerPause;
    private int pauseType = PlayerUserPause;
    private boolean middleAdShowed = false;
    private boolean isDetailsShowing = false;

    private boolean showingFinishDetails = false;
    private Dialog finishDetails;

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

        // Optional data
        if (getIntent().hasExtra("chapters")) {
            String chaptersString = getIntent().getStringExtra("chapters");
            JSONArray chaptersArray = JsonBuilder.stringToJsonArray(chaptersString);
            this.chapters = Chapter.getChaptersFromJson(chaptersArray);
        }

        if (getIntent().hasExtra("advance")) {
            String advanceString = getIntent().getStringExtra("advance");
            JSONObject advanceObj = JsonBuilder.stringToJson(advanceString);
            this.advance = new ChapterAdvance(advanceObj);
        }
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
        this.mediaReport = (Button) findViewById(R.id.btn_report);

        // Setting anime and media detials
        // Setting anime cover
        Picasso.with(this).load(this.anime.getCover()).into(this.animeCover);

        // Setting anime title
        this.animeTitle.setText(this.anime.getName());

        // Setting media title
        this.mediaTitle.setText(this.chapter.getNumber() +  " - " + this.chapter.getName() + " (" + this.chapter.getAudio() + " )");

        // Setting listner for report button
        this.mediaReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = String.format("Estas apunto de reportar el capitulo numero %s de %s, si observamos que el capitulo que reportas funciona a la perfección " +
                        "y repites este comportamiento constantemente, seras expulsado de la plataforma.\n Gracias por tu preferencia.", chapter.getNumber(), anime.getName());

                new SimpleDialog.Builder(PlayerController.this)
                        .setTitle("Reportar capitulo")
                        .setContent(message, 3)
                        .setBtnConfirmText("Reportar")
                        .setBtnCancelText("Cancelar")
                        .onConfirm(new SimpleDialog.BtnCallback() {
                            @Override
                            public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                                reportChapter();
                                mediaReport.setEnabled(false);
                            }
                        })
                        .onCancel(new SimpleDialog.BtnCallback() {
                            @Override
                            public void onClick(@NonNull SimpleDialog simpleDialog, @NonNull SimpleDialog.BtnAction btnAction) {
                                mPlayer.hideControls();
                                hidePlayerDetails();
                                mPlayer.start();
                            }
                        })
                        .show();

                mPlayer.pause();
            }
        });
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
        //this.mPlayer.setVolume(5, 5);
        this.mPlayer.setAutoFullscreen(true);
        this.mPlayer.setLoop(false);

        if (this.advance != null)
            this.mPlayer.setInitialPosition(this.advance.getPosition());
    }

    /**
     * Method to close activity
     **/
    protected void goBack() {
        finish();
    }

    protected void showPlayerDetails() {

        this.mediaDetails.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceInDown).duration(700).playOn(findViewById(R.id.media_details));
        this.isDetailsShowing = true;
    }

    protected void hidePlayerDetails() {

        YoYo.with(Techniques.BounceInUp).duration(700).playOn(findViewById(R.id.media_details));
        this.mediaDetails.setVisibility(View.INVISIBLE);
        this.isDetailsShowing = false;
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

        // Setting advance
        this.setRecordAdvance();
        goBack();
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
        this.setRecordAdvance();
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

        // Cleaning passed advance, if it's exists
        if (this.advance != null) {
            this.advance = null;
        }

        // Setting advance
        this.setRecordAdvance();

        // Checling it can show extra details
        if (this.chapters != null) {

            this.showingFinishDetails = true;
            this.finishDetails = new Dialog(PlayerController.this);
            finishDetails.setContentView(R.layout.dialog_finish_controls);
            finishDetails.setCanceledOnTouchOutside(false);
            finishDetails.show();

            // Setting callbacks
            ImageView prevButton = finishDetails.findViewById(R.id.prev_chapter);
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPrevChapter();
                }
            });

            ImageView replayButton = finishDetails.findViewById(R.id.replay_chapter);
            replayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rePlayChapter();
                }
            });

            ImageView nextButton = finishDetails.findViewById(R.id.next_chapter);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setNextChapter();
                }
            });

            Button closeButton = finishDetails.findViewById(R.id.close_details);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goBack();
                }
            });
        }
    }

    @Override
    public void onRetry(EasyClickableVideoPlayer player, Uri source) {

        if (this.mPlayer.isControlsShown()) {
            this.mPlayer.hideControls();
        }
    }

    @Override
    public void onSubmit(EasyClickableVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onStarted(EasyClickableVideoPlayer player) {

        if (this.mPlayer.isControlsShown()) {
            this.mPlayer.hideControls();
        }

        if (this.mediaDetails.getVisibility() == View.VISIBLE) {
            this.hidePlayerDetails();
        }
    }

    @Override
    public void onPaused(EasyClickableVideoPlayer player) {
    }

    @Override
    public void onVideoProgressUpdate(int position, int duration) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick() {

        if (!this.isDetailsShowing) {
            this.showPlayerDetails();
        } else {
            this.hidePlayerDetails();
        }
    }

    /***********************************************************************************************
     *                                     V.3.0 Finish Details                                    *
     **********************************************************************************************/

    private void setPrevChapter() {

        int currentChapter = this.chapter.getNumber();
        if (currentChapter > 1) {

            this.chapter = this.findChapter(currentChapter - 1);

            if (this.chapter != null) {

                this.setPlayerDetails();
                this.initPlayer();

                if (this.showingFinishDetails) {

                    this.showingFinishDetails = false;
                    this.finishDetails.hide();
                }

            } else {
                Toast.makeText(PlayerController.this, "Ha ocurrido un error, intentalo manualmente", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(PlayerController.this, "Estas en el primer capitulo", Toast.LENGTH_SHORT).show();
        }
    }

    private void rePlayChapter() {

        mPlayer.seekTo(0);
        mPlayer.start();

        if (this.showingFinishDetails) {

            this.showingFinishDetails = false;
            this.finishDetails.hide();
        }
    }

    private void setNextChapter() {

        int currentChapter = this.chapter.getNumber();
        if (currentChapter < this.chapters.size()) {

            this.chapter = this.findChapter(currentChapter + 1);

            if (this.chapter != null) {

                this.setPlayerDetails();
                this.initPlayer();

                if (this.showingFinishDetails) {

                    this.showingFinishDetails = false;
                    this.finishDetails.hide();
                }

            } else {
                Toast.makeText(PlayerController.this, "Ha ocurrido un error, intentalo manualmente", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(PlayerController.this, "Estas en el ultimo capitulo", Toast.LENGTH_SHORT).show();
        }
    }

    private Chapter findChapter(int number) {

        if (this.chapters != null) {
            for (Chapter locslChapter : this.chapters) {
                if (locslChapter.getNumber() == number) {
                    return locslChapter;
                }
            }
        }
        return null;
    }

    private void setRecordAdvance() {

        int position = this.mPlayer.getCurrentPosition();
        int duration = this.mPlayer.getDuration();
        float percent = ( (position * 100) / duration );
        WatchedChapters watchedChapters = new WatchedChapters(PlayerController.this, this.anime.getId());
        watchedChapters.addRecord(this.chapter.getId(), percent, position, duration);
    }

    /************************************************************************************************
     *                                              V.3.0                                           *
     ***********************************************************************************************/

    private void reportChapter() {

        final Credentials credentials = Credentials.getInstance(PlayerController.this);

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/anime/report/new");
        ReSTRequest request = new ReSTRequest(com.appbuilders.animediapremium.Libraries.Rester.ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        request.addField("user_id", credentials.getUserId());
        request.addField("bearer", credentials.getBearer());
        request.addField("uuid", credentials.getUserUuid());
        request.addField("bit", credentials.getBit());

        request.addField("media_id", String.valueOf(this.chapter.getId()));

        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                JSONObject res = JsonFileManager.stringToJSON(response.body);

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {
                        Toast.makeText(PlayerController.this, "Capitulo reportado", Toast.LENGTH_SHORT).show();
                    } else {
                        //showErrorAlert("Error", "Problemas de conexión");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ReSTResponse response) {
                Toast.makeText(PlayerController.this, "Intentalo de nuevo, porfavor!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}