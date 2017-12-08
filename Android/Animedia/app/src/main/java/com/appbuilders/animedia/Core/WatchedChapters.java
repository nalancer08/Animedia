package com.appbuilders.animedia.Core;

import android.content.Context;
import android.util.Log;

import com.appbuilders.credentials.Configurations;
import com.appbuilders.credentials.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 07/12/17
 */

public class WatchedChapters {

    private Context context;
    private String tag = "";
    private Configurations configs;

    public WatchedChapters(Context context, int anime_id) {

        this.context = context;
        this.tag = String.valueOf(anime_id);
        this.configs = Configurations.getInstance(this.context);
        this.hasGlobal();
    }

    public WatchedChapters(Context context, JSONObject animeObj) {

        this.context = context;
        Anime anime = new Anime(animeObj);
        this.tag = String.valueOf(anime.getId());
        this.configs = Configurations.getInstance(this.context);
        this.hasGlobal();
    }

    private void hasGlobal() {

        if (!this.configs.exists("advances")) {
            this.configs.add("advances", new JSONObject().toString());
        }
    }

    public boolean hasRecords() {

        String global = this.configs.getString("advances");
        JSONObject globalObj = JsonBuilder.stringToJson(global);
        return globalObj.has(this.tag);
    }

    public void addRecord(int chapter_id, float advance, int position, int duration) {

        String global = this.configs.getString("advances");
        JSONObject globalObj = JsonBuilder.stringToJson(global);
        ChapterAdvance chapterAdvance = new ChapterAdvance(advance, position, duration);

        try {

            JSONObject animeObj = new JSONObject();

            if (!this.hasRecords()) {
                globalObj.put(this.tag, animeObj.toString());
            } else {
                animeObj = globalObj.getJSONObject(this.tag);
            }

            animeObj.put(String.valueOf(chapter_id), chapterAdvance.serialize());
            globalObj.put(this.tag, animeObj);
            this.configs.add("advances", globalObj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ChapterAdvance getRecord(int chapter_id) {


        String global = this.configs.getString("advances");
        JSONObject globalObj = JsonBuilder.stringToJson(global);

        try {

            JSONObject animeObj = globalObj.getJSONObject(this.tag);
            if (animeObj.has(String.valueOf(chapter_id))) {
                JSONObject chapterObj = animeObj.getJSONObject(String.valueOf(chapter_id));
                return new ChapterAdvance(chapterObj);
            }
            return null;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}