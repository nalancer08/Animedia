package com.appbuilders.animedia.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.AnimeView;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfScreen;
import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(AnimeView holder, int position) {

        try {

            JSONObject anime = this.animes.getJSONObject(position);
            String cover = anime.getString("cover");
            String name = anime.getString("name");

            Picasso.with(this.context).load(cover).placeholder(R.drawable.placeholder).into(holder.getCover());
            holder.getName().setText(name);

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