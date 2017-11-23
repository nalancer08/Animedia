package com.appbuilders.animedia.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbuilders.animedia.Controller.ChromeWebPlayer;
import com.appbuilders.animedia.Controls.AutoResizeTextView;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class ChapterAdapter extends ArrayAdapter<Chapter> {

    public JSONArray data;
    public Context context;
    public ArrayList<Chapter> chaptersArray;
    public JSONArray chapters;
    public ArrayList<View> addedViews;
    public JSONObject anime;

    public ChapterAdapter(Context context, ArrayList<Chapter> chapters) {

        super(context, R.layout.chapter_adapter, chapters);
        this.context = context;
        this.chaptersArray = chapters;
        this.addedViews = new ArrayList<>();
    }

    public ChapterAdapter(Context context, JSONArray chapters, String animeString) {

        super(context, R.layout.chapter_adapter, Chapter.getChaptersFromJson(chapters));
        this.context = context;
        this.chapters = chapters;
        this.chaptersArray = Chapter.getChaptersFromJson(chapters);
        this.addedViews = new ArrayList<>();
        this.anime = JsonBuilder.stringToJson(animeString);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Chapter chapter = getItem(position);

        ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(this.context);
        // LayoutInflater inflater = getLayoutInflater(); // In activity

        convertView = null;

        if ( convertView == null ) {

            convertView = inflater.inflate(R.layout.chapter_adapter, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

            holder.getText().setText(chapter.getName());
            holder.getChapternumber().setText("" + chapter.getNumber());

            if (this.anime != null) {

                holder.getPlayButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {

                            Intent intent = new Intent(context, ChromeWebPlayer.class);
                            intent.putExtra("media", chapters.getJSONObject(position).toString());
                            intent.putExtra("anime", anime.toString());
                            context.startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public class ViewHolder {

        private View row;
        private AutoResizeTextView text = null;
        private AutoResizeTextView chapterNumber = null;
        private ImageView playButton = null;

        public ViewHolder(View row) {
            this.row = row;
        }

        public AutoResizeTextView getText() {

            if (this.text == null) {
                this.text = row.findViewById(R.id.chapterName);
                //this.text.setHeight(threeRuleY(200));
                //this.text.setMinimumHeight(threeRuleY(200));
            }
            return this.text;
        }

        public AutoResizeTextView getChapternumber() {

            if (this.chapterNumber == null) {
                this.chapterNumber = row.findViewById(R.id.chapterNumber);
            }
            return this.chapterNumber;
        }

        public ImageView getPlayButton() {

            if (this.playButton == null) {
                this.playButton = row.findViewById(R.id.playButton);
            }
            return this.playButton;
        }

        protected int threeRuleY(int value) {

            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int heigth = size.y;

            return (heigth * value) / 1794;
        }

    }
}