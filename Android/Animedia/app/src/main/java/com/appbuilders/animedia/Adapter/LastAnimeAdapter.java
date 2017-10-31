package com.appbuilders.animedia.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.appbuilders.surface.SfScreen;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class LastAnimeAdapter extends ArrayAdapter<Anime> {

    public JSONArray data;
    public Context context;
    public ArrayList<Anime> animes;
    public ArrayList<View> addedViews;

    public LastAnimeAdapter(Context context, ArrayList<Anime> animes ) {

        super(context, R.layout.last_anime_adapter, animes);
        this.context = context;
        this.animes = animes;
        this.addedViews = new ArrayList<>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Anime anime = getItem(position);

        ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(this.context);
        // LayoutInflater inflater = getLayoutInflater(); // In activity

        convertView = null;

        if ( convertView == null ) {

            convertView = inflater.inflate(R.layout.last_anime_adapter, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

            holder.getText().setText(anime.getName());

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public class ViewHolder {

        private View row;
        private TextView text = null;

        public ViewHolder(View row) {
            this.row = row;
        }

        public TextView getText() {

            if ( this.text == null ) {
                this.text = (TextView) row.findViewById(R.id.texto);
                this.text.setHeight(SfScreen.getInstance(getContext()).getDpY(200));
                this.text.setMinimumHeight(SfScreen.getInstance(getContext()).getDpY(200));
            }
            return this.text;
        }
    }
}