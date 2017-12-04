package com.appbuilders.chapterextractor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.appbuilders.chapterextractor.Libraries.Rester.ReSTCallback;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTClient;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTRequest;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTResponse;

import java.util.regex.Pattern;

public class AnimeYTExtratorByLola extends AppCompatActivity {

    private int lolaId = 1026;
    private int startChapter = 1;
    private int lastChapter = 175;
    private int currentChapter = 0;

    private String url = "http://s2.animeyt.tv/lola.php?cd=";
    private String[] chapters;
    //private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting current chapter to starterChapter
        this.currentChapter = this.startChapter;
        this.chapters = this.getResources().getStringArray(R.array.fairy_tail_chapters);

        // begging extratc
        this.extract();
    }

    public void extract() {

        if (this.currentChapter <= lastChapter) {

            String media = chapters[currentChapter - 1];
            final String[] mediaExplode = media.split(Pattern.quote("."));
            final String number = mediaExplode[0];
            final String name = mediaExplode[1];
            String currentLolaUrl = this.url + lolaId + "&file=" + this.currentChapter;

            // Saving media
            ReSTClient rest = new ReSTClient("https://appbuilders.com.mx/apis/animedia/media/new");
            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
            request.addParameter("token", "658da3c73c026446017d2fd583ecf1f107fe66b9415eaf9a986a5436807add6b.80f8cc918a88a6b7a5894e8c9e7859dece7a7c25");
            request.addField("anime_id", "15");
            request.addField("number", number);
            request.addField("name", name);
            request.addField("type", "chapter");
            request.addField("audio", "jp/spa");
            request.addField("url", currentLolaUrl);
            //request.addField("options", String.valueOf(successfullyUrls));

            rest.execute(request, new ReSTCallback() {
                @Override
                public void onSuccess(ReSTResponse response) {

                    Log.d("DXGOP", number + " - " + name + " [OK]" );
                    currentChapter++;
                    extract();
                }

                @Override
                public void onError(ReSTResponse reSTResponse) {
                }
            });
        }
        Log.d("DXGOP", ":::::::::::::::::::::::::::::::::::::::::::::::::::::" );
        Log.d("DXGOP", " _________________FINISHED EXTRACT___________________" );
        Log.d("DXGOP", ":::::::::::::::::::::::::::::::::::::::::::::::::::::" );
    }
}