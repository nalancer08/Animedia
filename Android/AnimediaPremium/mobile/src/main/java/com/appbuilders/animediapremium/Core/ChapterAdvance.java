package com.appbuilders.animediapremium.Core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 07/12/17
 */

public class ChapterAdvance {

    private float advance = 0.0f;
    private int position = 0;
    private int duration = 0;

    public ChapterAdvance(float advance, int position, int duration) {

        this.advance = advance;
        this.position = position;
        this.duration = duration;
    }

    public ChapterAdvance(JSONObject serializable) {

        try {

            if (serializable.has("advance")) {
                this.advance = (float) serializable.getDouble("advance");
            }

            if (serializable.has("position")) {
                this.position = serializable.getInt("position");
            }

            if (serializable.has("duration")) {
                this.duration = serializable.getInt("duration");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public float getAdvance() {
        return advance;
    }

    public void setAdvance(float advance) {
        this.advance = advance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public JSONObject serialize() {

        JSONObject obj = new JSONObject();

        try {

            obj.put("advance", this.advance);
            obj.put("position", this.position);
            obj.put("duration", this.duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString() {

        return this.serialize().toString();
    }
}
