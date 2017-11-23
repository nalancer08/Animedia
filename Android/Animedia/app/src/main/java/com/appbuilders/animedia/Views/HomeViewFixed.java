package com.appbuilders.animedia.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.appbuilders.animedia.Adapter.LastAnimeAdapter;
import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.Controller.ChromeWebPlayer;
import com.appbuilders.animedia.Controller.DailyMotionPlayer;
import com.appbuilders.animedia.Controls.CutListView;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Implement.LastAnimesListImp;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Listener.OnScrollListViewMiddle;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SfScreen;
import com.appbuilders.surface.SurfaceActivityView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class HomeViewFixed extends SurfaceActivityView {

    protected JSONArray animes;

    protected ImageView screenView;
    protected ListView list;
    protected View prevView = null;

    private boolean firstTime = false;

    public HomeViewFixed(Context context) {
        super(context);
    }

    public HomeViewFixed(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public HomeViewFixed(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        this.animes = JsonBuilder.stringToJsonArray(this.activity.getIntent().getStringExtra("latestAnimes"));

        // Initialize listView
        this.list = new CutListView(this.context);
        //list.setTransitionEffect(new WaveEffect());

        // Method to set the first image and initialize the imageView
        ArrayList<Anime> animesArray = Anime.getAnimesFromJson(animes);
        this.list.setAdapter(null);
        this.list.setAdapter(new LastAnimeAdapter(this.context, animesArray));
        this.setInitialBackground();

        SfPanel correction = new SfPanel().setSize(-100, -60);
        SfPanel listPanel = new SfPanel().setSize(-70, -40);
        this.subScreen.append(correction).append(listPanel);


        //list.setBackgroundResource(R.color.blackTrans);
        //this.list.setBackgroundResource(R.drawable.last_animes_background);
        this.list.setOnScrollListener(new LastAnimesListImp(new OnScrollListViewMiddle() {
            @Override
            public void onMiddle(int position) {
                setDynamicBackground(position);

            }

            @Override
            public void onScrollMove(int position) {

                setOnScrollSelectedItem(position);
                setDynamicBackground(position);
            }
        }));

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gotoAnimePlayer(i-2);
            }
        });

        listPanel.setView(this.list);
        this.addView(this.list);

        this.screen.update(this.context);
    }

    private void setInitialBackground() {

        this.screenView = new ImageView(this.context);
        this.screenView.setAdjustViewBounds(true);
        //this.screenView.setScaleType(ImageView.ScaleType.CENTER);
        this.screenView.setScaleType(ImageView.ScaleType.FIT_XY);

        this.addView(this.screenView);
        this.screen.setView(this.screenView);
        //this.screen.setAlignment(SfPanel.SF_ALIGNMENT_RIGHT);
        this.subScreen.setAlignment(SfPanel.SF_ALIGNMENT_RIGHT);

        if (this.animes.length() >= 1) {
            this.setDynamicBackground(0);
            this.setOnScrollSelectedItem(0);
        }
    }

    private void setOnScrollSelectedItem(int position) {

        if (this.prevView != null) {

            this.prevView.setBackgroundResource(R.color.trans);
            this.prevView.setPadding(0, 0, 0, 0);
        }

        View view = getViewByPosition(position, this.list);
        view.setBackgroundColor(Color.rgb(237, 178, 0));
        view.setPadding(0, 0, threeRuleX(150), 0);
        this.prevView = view;
    }

    private void setDynamicBackground(int position) {

        try {

            JSONObject firstAnime = this.animes.getJSONObject(position);
            String cover = firstAnime.getString("cover");
            if (!cover.equals("")) {
                // Download the image
                this.setImageFromUrl(cover, this.screenView);
            } else {
                // Random background color
                this.screenView.setBackgroundColor(this.randomColor());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String parseUrl(String url) {

        String ret = "";

        try {

            URL uri = new URL(url);
            ret = url.toString();

            if (BuildConfig.debugMode) {
                ret = ret.replace("localhost", "192.168.1.69");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private void setImageFromUrl(String url, final ImageView image) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        //ImageSize targetSize = new ImageSize(1000, 1600); // result Bitmap will be fit to this size
        imageLoader.loadImage(url, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

                int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                Bitmap imageB = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                image.setImageBitmap(imageB);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                image.setImageBitmap(loadedImage);

                //animeImage.setImageBitmap(loadedImage);
                /*if (SfScreen.getInstance(context).getScreenAxis(SfScreen.ScreenHeight) >= 2220) {
                    animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 1000, 1500, false));
                } else {
                    animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 512, 780, false));
                }*/
            }
        });

        /*Picasso.with(this.context).load(url).placeholder(R.drawable.placeholder).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                view.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                view.setImageBitmap(image);
            }
        });*/
    }

    public View getViewByPosition(int pos, ListView listView) {

        pos = pos + 2;

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    protected void gotoAnimePlayer(int position) {

        Intent intent;

        try {

            JSONObject anime = this.animes.getJSONObject(position);
            JSONObject media = anime.getJSONObject("media");

            intent = new Intent(context, ChromeWebPlayer.class);
            intent.putExtra("anime", anime.toString());
            intent.putExtra("media", media.toString());
            activity.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int randomColor() {

        Integer c1 = 1 + (int)(Math.random() * ((255 - 1) + 1));
        Integer c2 = 3 + (int)(Math.random() * ((254 - 3) + 3));
        Integer c3 = 5 + (int)(Math.random() * ((253 - 5) + 5));
        return Color.rgb(c1, c2, c3);
    }

    protected int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }
}