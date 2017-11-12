package com.appbuilders.animedia.Controller;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.R;

public class MainActivity extends AppCompatActivity implements EasyVideoCallback {

    //private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //private static final String TEST_URL = "http://www.dailymotion.com/embed/video/x1ade3x";
    //private static final String TEST_URL = "//www.dailymotion.com/embed/video/k4mTupurPBY66DpeM8d?autoPlay=1";
    //private static final String TEST_URL = "http://s3.animeyt.tv/mega.php?v=ZDB3VUFlTjhZRS9xT2dJc1Z6Z3JLQjd3ZUhrdld2WFpuenc5S09oRWF0SUlteko5L2NaQVBzTFIzbWplZlJreA==";

    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting chapter intent data
        String chapterString = getIntent().getStringExtra("media");

        // Setting chapter
        Chapter chapter = new Chapter(JsonBuilder.stringToJson(chapterString));

        // Grabs a reference to the player view
        player = (EasyVideoPlayer) findViewById(R.id.player);

        // Sets the callback to this Activity, since it inherits EasyVideoCallback
        player.setCallback(this);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(chapter.getUrl()));

        player.setAutoPlay(true);

        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    // Methods for the implemented EasyVideoCallback

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        // TODO handle
    }

    @Override
    public void onBuffering(int percent) {
        // TODO handle if needed
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        // TODO handle
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        // TODO handle if needed
    }
}