package com.appbuilders.chapterextractor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appbuilders.chapterextractor.Libraries.Rester.ReSTCallback;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTClient;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTRequest;
import com.appbuilders.chapterextractor.Libraries.Rester.ReSTResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 01/12/17
 */

public class AnimtYTMeLaPela extends AppCompatActivity {


    private String url = "http://s2.animeyt.tv/lola.php?cd=1025&file=231";
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.webview = findViewById(R.id.lol);
        webview.getSettings().setJavaScriptEnabled(true);
        //webview.addJavascriptInterface(new MainActivity.MyJavaScriptInterface(), "HTMLOUT");
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                Log.d("DXGOP", "LALALA ::: " + url);
                if (url.equals("http://s2.animeyt.tv/minha_animeyt.php")) {

                    //return new WebResourceResponse("text/json", "utf-8", magicallyGetSomeInputStream());
                    //return new WebResourceResponse("text/json", "utf-8", magicallyGetSomeInputStream());

                    URL aURL = null;
                    try {
                        aURL = new URL(url);
                        URLConnection conn = aURL.openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();

                        BufferedReader r = new BufferedReader(new InputStreamReader(is));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line).append('\n');
                        }

                        Log.d("DXGOP", "MIERDA :::: " + total);


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //if (magicallyMatch(url))
                //    return new WebResourceResponse("text/json", "utf-8", magicallyGetSomeInputStream());
                return null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                Log.d("DXGOP", "REQUEST ::: " + request.getUrl());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("DXGOP", "URL ::: " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("DXGOP", "URL ::: " + url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                /* This call inject JavaScript into the page which just finished loading. */
                //webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });
        webview.loadUrl(url);

    }

}