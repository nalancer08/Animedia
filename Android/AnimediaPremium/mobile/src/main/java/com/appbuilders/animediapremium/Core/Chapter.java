package com.appbuilders.animediapremium.Core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 06/11/17
 */

public class Chapter {

    protected int id = 0;
    //protected Anime anime;
    protected int number = 0;
    protected String name = "";
    protected String description = "";
    protected String audio = "";
    protected String thumbnail = "";
    protected String url = "";

    public Chapter() {

        this.id = 0;
        this.number = 0;
        this.name = "";
        this.description = "";
        this.audio = "";
        this.thumbnail = "";
    }

    public Chapter(JSONObject object) {

        this.thumbnail = "";

        try {

            this.setId(object.getInt("id"));
            this.setNumber(object.getInt("number"));
            this.setName(object.getString("name"));
            this.setAudio(object.getString("audio"));

            if (object.has("description")) {
                this.setDescription(object.getString("description"));
            }

            if (object.has("url")) {
                this.setUrl(object.getString("url"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public static ArrayList<Chapter> getChaptersFromJson(JSONArray array) {

        ArrayList<Chapter> chapters = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            try {
                chapters.add(new Chapter(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return chapters;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}