package com.appbuilders.animedia.Controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.appbuilders.animedia.R;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import java.io.File;

import is.arontibo.library.ElasticDownloadView;

public class UpdateController extends AppCompatActivity {

    private ElasticDownloadView mElasticDownloadView;
    private String app_name = "Animedia.apk";
    private float currentProgress = 10;
    private int currentCount = 0;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_controller);

        // Getting url
        Intent intent = getIntent();
        if (intent.hasExtra("url")) {
            this.url = intent.getStringExtra("url");
        }

        // Setting status bar color
        this.setStatusBarColor();

        // Init elastic view
        this.mElasticDownloadView = (ElasticDownloadView)findViewById(R.id.elastic_download_view);

        // Download
        this.download();
    }

    protected void download() {

        //Uri downloadUri = Uri.parse("http://s3.animeflv.com/efire.php?v=cGNJV2w0L0xGdWI0TnBOcmZtY0JpdFBKWWV5eHFZRTNCU3VlYytRa3k1cz0=");
        //Uri destinationUri = Uri.parse(context.getExternalCacheDir().toString() + "/erick1234.mp4");

        // Setting timeout globally for the download network requests:
        //String url = "https://drive.google.com/uc?export=download&id=1zq3NM0zojm3jZ_DI4k_QbugK3BE_j6E0";
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder().build();
        PRDownloader.initialize(UpdateController.this, config);

        if (!this.url.equals("")) {
            int downloadId = PRDownloader.download(url, this.getExternalCacheDir().toString(), this.app_name).build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                            mElasticDownloadView.startIntro();
                            mElasticDownloadView.setProgress(10);
                            Log.d("DXGOP", "A descargar perras");
                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {
                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {
                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                            //Log.d("DXGOP", "progress ::: " + progress);

                            if (currentCount <= 120) {
                                currentCount++;
                            } else {

                                currentCount = 0;
                                if (currentProgress < 99) {
                                    currentProgress = currentProgress + 5;
                                    mElasticDownloadView.setProgress(currentProgress);
                                }
                            }
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {

                            //Log.d("DXGOP", "ACABO DE BAJAR LA PERRA");
                            mElasticDownloadView.success();
                            installApp();
                        }

                        @Override
                        public void onError(Error error) {
                            Log.d("DXGOP", "FALLO ::: " + error);
                        }
                    });
        }
    }

    private void installApp() {

        File file = new File(this.getExternalCacheDir(), this.app_name);
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(promptInstall);
        finish();
    }

    @SuppressLint("NewApi")
    protected void setStatusBarColor() {

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(196, 142, 13));
            //window.setStatusBarColor(Color.rgb(210, 208, 207));
            //window.setStatusBarTextColor(getResources().getColor(R.color.orange));
        }
    }
}