package com.appbuilders.chapterextractor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appbuilders.chapterextractor.Libraries.Rester.ReSTCallback;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTClient;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTRequest;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String animeId = "";
    private int beggingChapter = 1;
    private int lastChapter = 1;
    private int currentChapter = 0;
    //private String url = "https://animeflv.net/ver/2744/fairy-tail-";
    //private String url = "https://animeflv.net/ver/1149/darker-than-black-";
    private String url = "";
    private String[] chapters;
    private WebView webview;

    /** Support for efire && embed **/
    private String source = "efire";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.currentChapter = this.beggingChapter;

        this.animeId = this.getResources().getString(R.string.sao_id);
        this.url = this.getResources().getString(R.string.sao_url);
        this.chapters = this.getResources().getStringArray(R.array.sao_chapters);
        this.lastChapter = this.chapters.length;
        //this.lastChapter = 12;

        this.webview = findViewById(R.id.lol);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                /* This call inject JavaScript into the page which just finished loading. */
                webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });

        this.beganExtract(this.url);
    }

    @SuppressLint("JavascriptInterface")
    private void beganExtract(String url) {

        if (this.currentChapter <= this.lastChapter) {

            Log.d("DXGOP", "______ BEGINNING EXTRACT FOR CHAPTER :: " + currentChapter + "  ::: " + this.chapters[this.currentChapter-1] +  " _________");
            url = url + this.currentChapter;


            webview.loadUrl(url);
            //webview.loadUrl("about:blank");

        } else {
            Log.d("DXGOP", "______ END EXTRACTION _________");
        }
    }

    private void beganSimpleExtract(String url) {

        //ReSTClient rest = new ReSTClient(url);
        /*ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        rest.execute(request, new ReSTCallback() {
                    @Override
                    public void onSuccess(ReSTResponse response) {

                        String resp = response.body;
                        Log.d("DXGOP", "Extract step 1 :: " + resp);
                        List<String> extractedUrls = extractUrls(resp);

                        for (String url : extractedUrls) {

                            Log.d("DXGOP", "Url finded :: " + url);
                        }

                    }

                    @Override
                    public void onError(ReSTResponse reSTResponse) {}
                });*/
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    class MyJavaScriptInterface {

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {

            List<String> extractedUrls = extractUrls(html);
            final ArrayList<String> successfullyUrls = new ArrayList<>();
            String bestOne = "";

            for (String url : extractedUrls) {

                Log.d("DXGO", "url ::: " + url);

                if (url.contains("s3.animeflv.com")) {
                    url = url.replace("&quot;,", "");
                    switch (source) {

                        case "efire":

                            if (!successfullyUrls.contains(url)) {

                                if (bestOne.equals("")) {
                                    bestOne = url;
                                } else {
                                    successfullyUrls.add(url);
                                }
                                Log.d("DXGOP", "Url best option :: " + url);
                            }

                            break;

                        case "embed":
                            if (url.contains("yourupload")) {
                                if (!successfullyUrls.contains(url)) {

                                    if (bestOne.equals("")) {
                                        bestOne = url;
                                    } else {
                                        successfullyUrls.add(url);
                                    }
                                    Log.d("DXGOP", "Url best option :: " + url);
                                }
                            } else if (url.contains("izanagi")) {

                                if (!successfullyUrls.contains(url)) {
                                    successfullyUrls.add(url);
                                    Log.d("DXGOP", "Url options :: " + url);
                                }
                            }
                            break;
                    }
                } else if (url.contains("https://www.rapidvideo.com")) {

                    url = url.replace("720p", "420p");
                    if (!successfullyUrls.contains(url)) {
                        successfullyUrls.add(url);
                        Log.d("DXGOP", "Url options :: " + url);
                    }

                } else if (url.contains("https://streamango.com/embed")) {
                    if (!successfullyUrls.contains(url)) {
                        successfullyUrls.add(url);
                        Log.d("DXGOP", "Url options :: " + url);
                    }
                } else if (url.contains("https://www.mp4upload.com/")) {
                    if (!successfullyUrls.contains(url)) {
                        successfullyUrls.add(url);
                        Log.d("DXGOP", "Url options :: " + url);
                    }
                } else if (url.contains("https://openload.co/embed/")) {
                    if (!successfullyUrls.contains(url)) {
                        successfullyUrls.add(url);
                        //bestOne = url;
                        Log.d("DXGOP", "Url options :: " + url);
                    }
                } /*else if (url.contains("https://hqq.tv/player/")) {
                    if (!successfullyUrls.contains(url)) {
                        successfullyUrls.add(url);
                        Log.d("DXGOP", "Url options :: " + url);
                    }
                }*/
            }

            final String finalBestOne = bestOne;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Code for WebView goes here

                    if (!finalBestOne.equals("")) {

                        // Getting chapter name and number
                        String media = chapters[currentChapter-1];
                        String[] mediaExplode = media.split(Pattern.quote("."));
                        String mediaNumber = mediaExplode[0];
                        String mediaName = mediaExplode[1];

                        Log.d("DXGOP", "URL BUENA :: " + finalBestOne);
                        Log.d("DXGOP", "URLS EXTRAS :: " + String.valueOf(successfullyUrls));

                        // Saving media
                        ReSTClient rest = new ReSTClient("https://appbuilders.com.mx/apis/animedia/media/new");
                        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
                        request.addParameter("token", "658da3c73c026446017d2fd583ecf1f107fe66b9415eaf9a986a5436807add6b.80f8cc918a88a6b7a5894e8c9e7859dece7a7c25");
                        request.addField("anime_id", animeId);
                        request.addField("number", mediaNumber);
                        request.addField("name", mediaName);
                        request.addField("type", "chapter");
                        request.addField("audio", "jp/spa");
                        request.addField("url", finalBestOne);
                        request.addField("options", String.valueOf(successfullyUrls));

                        rest.execute(request, new ReSTCallback() {
                            @Override
                            public void onSuccess(ReSTResponse response) {

                                currentChapter++;
                                beganExtract(url);
                            }

                            @Override
                            public void onError(ReSTResponse reSTResponse) {}
                        });
                    }
                }
            });
        }
    }
}