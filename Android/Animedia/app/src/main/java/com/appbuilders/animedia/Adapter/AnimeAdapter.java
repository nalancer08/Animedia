package com.appbuilders.animedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appbuilders.animedia.Controller.SingleAnimeController;
import com.appbuilders.animedia.Core.AnimeView;
import com.appbuilders.animedia.Core.Liked;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfScreen;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 30/10/17
 */

public class AnimeAdapter extends RecyclerView.Adapter<AnimeView> {

    private Context context;
    private JSONArray animes;
    private InterstitialAd ad;
    private Intent lastIntent;
    private boolean typeOfLucky;
        private int luckyIntent = 0;

    /** V.3.5 **/

    public AnimeAdapter(final Context context, JSONArray animes) {

        this.context = context;
        this.animes = animes;

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
    }

    @Override
    public AnimeView onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.masonry_anime_item, parent, false);
        AnimeView animeView = new AnimeView(layoutView);
        return animeView;
    }

    @Override
    public void onBindViewHolder(final AnimeView holder, int position) {

        try {

            final JSONObject anime = this.animes.getJSONObject(position);
            final int id =  anime.getInt("id");
            String cover = anime.getString("cover");
            String name = anime.getString("name");
            final ImageView animeImage = holder.getCover();

            // Setting image
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.placeholder)
                    .resetViewBeforeLoading(false)
                    .cacheInMemory(true)
                    .cacheOnDisk(true).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            //ImageSize targetSize = new ImageSize(1000, 1600); // result Bitmap will be fit to this size
            imageLoader.loadImage(cover, options, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                    int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                    Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                    holder.getCover().setImageBitmap(image);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    //animeImage.setImageBitmap(loadedImage);
                    if (SfScreen.getInstance(context).getScreenAxis(SfScreen.ScreenHeight) >= 2220) {
                        animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 1000, 1500, false));
                    } else {
                        animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 512, 780, false));
                    }
                }
            });

            // Setting name
            holder.getName().setText(name);

            // Setting the click
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /**Intent intent = new Intent(context, SingleAnimeController.class);
                    intent.putExtra("anime", anime.toString());
                    context.startActivity(intent);*/

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return this.animes == null ? 0 : animes.length();
    }

    public void addNews(JSONArray animes) {

        this.animes = animes;
        this.notifyItemRangeInserted(0, this.animes.length() - 1);
    }

    public void clear() {

        int size = this.animes.length();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.animes.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void loadAd() {

        //Log.d("DXGOP", "Loading ad ....");
        AdRequest adRequest = new AdRequest.Builder().build();
        this.ad.loadAd(adRequest);
    }
}