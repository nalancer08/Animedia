package com.appbuilders.animedia.Core;

import com.appbuilders.animedia.Libraries.JsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class Anime {

    private int id;
    private String name;
    private int year;
    private String description;
    private ArrayList<String> genres;
    private String cover;

    public Anime() {

        this.id = 0;
        this.name = "";
        this.year = 0;
        this.description = "";
        this.genres = new ArrayList<>();
        this.cover = "";
    }

    public Anime(JSONObject object) {

        this.genres = new ArrayList<>();

        try {

            this.setId(object.getInt("id"));
            this.setName(object.getString("name"));
            this.setYear(object.getInt("year"));
            this.setDescription(object.getString("description"));
            this.setCover(object.getString("cover"));

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public static ArrayList<Anime> getAnimesFromJson(JSONArray array) {

        ArrayList<Anime> animes = new ArrayList<>();

        animes.add(new Anime());
        animes.add(new Anime());

        for (int i = 0; i < array.length(); i++) {

            try {
                animes.add(new Anime(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        animes.add(new Anime());
        animes.add(new Anime());
        return animes;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}