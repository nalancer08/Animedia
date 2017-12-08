package com.appbuilders.animediapremium.Adapter;

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

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.appbuilders.animediapremium.Controller.ChromeWebPlayer;
import com.appbuilders.animediapremium.Controller.PlayerController;
import com.appbuilders.animediapremium.Controls.AutoResizeTextView;
import com.appbuilders.animediapremium.Core.Chapter;
import com.appbuilders.animediapremium.Core.ChapterAdvance;
import com.appbuilders.animediapremium.Core.WatchedChapters;
import com.appbuilders.animediapremium.Libraries.JsonBuilder;
import com.appbuilders.animediapremium.R;

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

    /** Version 3.0 **/
    public WatchedChapters watchedChapters;

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
        this.watchedChapters = new WatchedChapters(this.context, this.anime);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Chapter chapter = getItem(position);

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

            if (this.watchedChapters.hasRecords()) {
                ChapterAdvance advance = this.watchedChapters.getRecord(chapter.getId());
                if (advance != null) {
                    holder.setProgress(advance.getAdvance());
                }
            }

            if (this.anime != null) {

                holder.getPlayButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent;
                        try {

                            JSONObject tempChapterString = chapters.getJSONObject(position);
                            Chapter tempChapter = new Chapter(tempChapterString);

                            if (tempChapter.getUrl().contains("animeflv")) {

                                intent = new Intent(context, PlayerController.class);
                                intent.putExtra("chapters", chapters.toString());

                                if (watchedChapters.hasRecords()) {
                                    ChapterAdvance advance = watchedChapters.getRecord(chapter.getId());
                                    if (advance != null)
                                        intent.putExtra("advance", advance.toString());
                                }

                            } else {
                                intent = new Intent(context, ChromeWebPlayer.class);
                            }

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
        private RoundCornerProgressBar progressChapter = null;

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

        public void setProgress(float progress) {

            if (this.progressChapter == null) {
                this.progressChapter = row.findViewById(R.id.chapter_progress);
                this.progressChapter.setVisibility(View.VISIBLE);
            }
            this.progressChapter.setProgress(progress);
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