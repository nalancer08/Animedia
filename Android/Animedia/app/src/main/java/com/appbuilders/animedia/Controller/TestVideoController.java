package com.appbuilders.animedia.Controller;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.appbuilders.animedia.R;

public class TestVideoController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_video_controller);

        VideoView video = (VideoView) findViewById(R.id.video);
        Uri uri = Uri.parse("https://om154.cdn.oose.io/play/a2017121967PUlwD7A11/video.mp4?null&start=0");
        video.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video);
        video.setMediaController(mediaController);

        video.start();
    }
}
