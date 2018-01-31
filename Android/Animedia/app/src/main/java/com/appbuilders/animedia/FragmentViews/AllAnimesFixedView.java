package com.appbuilders.animedia.FragmentViews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.AnimeAdapter;
import com.appbuilders.animedia.Controller.SingleAnimeController;
import com.appbuilders.animedia.Controls.SpacesItemDecoration;
import com.appbuilders.animedia.Core.AnimeView;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SfScreen;
import com.appbuilders.surface.SurfaceActivityView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 04/11/17
 */

public class AllAnimesFixedView extends SurfaceActivityView {

    private Credentials credentials;
    private SfPanel contentPanel;
        private SfPanel subContentPanel;
            private SfPanel searchButtonPanel;
        private SfPanel masonryPanel;

    private InterstitialAd ad;
    private Intent lastIntent;
    private boolean typeOfLucky;
    private int luckyIntent = 0;

    private int current = 0;
    private JSONArray animes;

    /** V.3.5 **/
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public AllAnimesFixedView(Context context) {
        super(context);
    }

    public AllAnimesFixedView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public AllAnimesFixedView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Initializing panels
        this.contentPanel = new SfPanel();

        // Calling to the lucky
        this.init();

        // Creating masonry layout
        //this.createMasonryLayout();

        // Asking for projects
        this.askForLatestAnimes();

    }

    private void init() {

        // Initializing ad
        this.ad = new InterstitialAd(this.context);
        this.ad.setAdUnitId("ca-app-pub-8714411824921031/9634722849");
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
                context.startActivity(lastIntent);
                loadAd();
            }
        });
        this.loadAd();

        // Randoms numbers for lucky ads :3
        Random r = new Random();
        int i1 = r.nextInt(5 - 1 + 1) + 1;
        this.typeOfLucky = (i1 % 2 == 0) ? true : false;

        // Init ImageLoader
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        this.imageLoader = ImageLoader.getInstance();
    }

    protected void askForLatestAnimes() {

        this.credentials = Credentials.getInstance(this.context);

        if (!credentials.existsPreference("allAnimes")) {
            if (credentials.existsPreviousLogin()) {

                ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes");
                ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
                request.addParameter("token", credentials.getToken());
                request.addField("user_id", credentials.getUserId());
                request.addField("bearer", credentials.getBearer());
                request.addField("uuid", credentials.getUserUuid());
                request.addField("bit", credentials.getBit());
                rest.execute(request, new ReSTCallback() {

                    @Override
                    public void onSuccess(ReSTResponse response) {

                        JSONObject res = JsonFileManager.stringToJSON(response.body);
                        Log.d("DXGOP", "All fragment animes ::: " + res.toString());

                        try {

                            if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                                final JSONArray array = res.getJSONArray("data");
                                createGrid(array);
                                credentials.savePreference("allAnimes", array.toString());

                            } else {
                                //showErrorAlert("Error", "Problemas de conexión");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ReSTResponse response) {

                        String errorMessage;
                        if (response.statusCode == 404) {
                            errorMessage = "HUMAN used SEARCH\nBut, it failed!";
                        } else {
                            errorMessage = "Error " + Integer.toString(response.statusCode);
                        }
                        Toast.makeText(context, "Try again!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            this.createGrid(JsonBuilder.stringToJsonArray(credentials.getPreference("allAnimes")));
        }
    }
    
    private void createGrid(JSONArray animes) {

        this.contentPanel.setSize(-100, -82);
        this.makeItScrollable(this.contentPanel, "allAnimesPanel");
        this.animes = animes;
        this.createItem();
    }

    private void createItem() {

        if (this.current < this.animes.length()) {

            try {

                final JSONObject anime = animes.getJSONObject(this.current);
                String cover = anime.getString("cover");
                String name = anime.getString("name");

                // View handle
                View layoutView = LayoutInflater.from(this.context).inflate(R.layout.masonry_anime_item, null, false);
                AnimeView animeView = new AnimeView(layoutView);

                // Holder content
                final ImageView animeImage = animeView.getCover();
                animeImage.setAdjustViewBounds(true);
                animeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Setting image
                downloadImage(cover, animeImage);

                // Setting name
                animeView.getName().setText(name);
                animeView.getName().setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                animeView.getName().setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

                // Setting the click
                animeView.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        lastIntent = new Intent(context, SingleAnimeController.class);
                        lastIntent.putExtra("anime", anime.toString());

                        if (!typeOfLucky) {
                            ad.show();
                        } else {
                            if (luckyIntent == 1) {
                                luckyIntent = 0;
                                context.startActivity(lastIntent);
                            } else {
                                luckyIntent = 1;
                                ad.show();
                            }
                        }
                    }
                });

                // Surface logiv
                SfPanel activitiePanel = new SfPanel().setSize(-46, -58);
                activitiePanel.setMargin(threeRuleY(35), (current % 2 == 0) ? threeRuleX(25) : 0, 0, 0);
                activitiePanel.setView(layoutView);
                // Addings and appends
                this.contentPanel.append(activitiePanel);
                this.addToScroll("allAnimesPanel", layoutView);
                this.contentPanel.update(this.context);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadImage(String url, final ImageView imageView) {

        this.imageLoader.loadImage(url, this.options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

                int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                imageView.setImageBitmap(image);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                current++;
                createItem();
            }
        });
    }

    public void loadAd() {

        //Log.d("DXGOP", "Loading ad ....");
        AdRequest adRequest = new AdRequest.Builder().build();
        this.ad.loadAd(adRequest);
    }

    protected int threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    protected int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }
}