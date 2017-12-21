package com.appbuilders.animedia.Core;

import android.content.Context;

import com.appbuilders.credentials.Configurations;
import com.appbuilders.credentials.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 21/12/17
 */

public class Liked {

    private static Liked mInstance;
    private Context context;
    private Configurations configs;

    public static Liked getInstance(Context context) {

        if ( mInstance == null ) {
            Class clazz = Liked.class;
            synchronized (clazz) {
                mInstance = new Liked(context);
            }
        }
        return mInstance;
    }

    private Liked(Context context) {

        this.context = context;
        this.configs = Configurations.getInstance(this.context);
        this.init();
    }

    private void init() {

        if (!this.configs.exists("likes")) {
            this.configs.add("likes", new JSONObject().toString());
        }
    }

    public boolean hasFav(int anime_id) {

        String global = this.configs.getString("likes");
        JSONObject globalObj = JsonBuilder.stringToJson(global);
        return globalObj.has(String.valueOf(anime_id));
    }

    public void addFav(int anime_id) {

        String global = this.configs.getString("likes");
        JSONObject globalObj = JsonBuilder.stringToJson(global);

        try {

            globalObj.put(String.valueOf(anime_id), true);
            this.configs.add("likes", globalObj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeFav(int anime_id) {

        String global = this.configs.getString("likes");
        JSONObject globalObj = JsonBuilder.stringToJson(global);
        if(globalObj.has(String.valueOf(anime_id))) {
            globalObj.remove(String.valueOf(anime_id));
        }
    }
}
