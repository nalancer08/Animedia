package com.appbuilders.animedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appbuilders.animedia.Controller.SingleAnimeController;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.AnimeView;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfScreen;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 30/10/17
 */

public class AnimeAdapter extends RecyclerView.Adapter<AnimeView> {

    private Context context;
    private JSONArray animes;

    public AnimeAdapter(Context context, JSONArray animes) {

        this.context = context;
        this.animes = animes;
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
            String cover = anime.getString("cover");
            String name = anime.getString("name");
            final ImageView animeImage = holder.getCover();

            if (SfScreen.getInstance(this.context).getScreenAxis(SfScreen.ScreenHeight) >= 2220) {

                /*Picasso.with(this.context).load(cover).placeholder(R.drawable.placeholder).resize(1000, 1500).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.getCover().setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                        Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                        holder.getCover().setImageBitmap(image);
                    }
                });*/


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
                        animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 1000, 1500, false));

                    }
                });


            } else {
                Picasso.with(this.context).load(cover).placeholder(R.drawable.placeholder).into(holder.getCover());
            }

            holder.getName().setText(name);

            // Setting the click
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, SingleAnimeController.class);
                    intent.putExtra("anime", anime.toString());
                    context.startActivity(intent);
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
}