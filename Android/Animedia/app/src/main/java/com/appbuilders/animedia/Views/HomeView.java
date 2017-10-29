package com.appbuilders.animedia.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.appbuilders.animedia.Adapter.LastAnimeAdapter;
import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.Controller.MenuController;
import com.appbuilders.animedia.Controls.CutListView;
import com.appbuilders.animedia.Controls.RelativeLayoutLeftCut;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Implement.LastAnimesListImp;
import com.appbuilders.animedia.Listener.OnScrollListViewMiddle;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SfScreen;
import com.appbuilders.surface.SurfaceActivityView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class HomeView extends SurfaceActivityView {

    protected JSONArray animes;

    protected ImageView screenView;
    protected ListView list;
    protected View prevView;

    public HomeView(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {

        this.animes = JsonBuilder.stringToJsonArray(this.activity.getIntent().getStringExtra("latestAnimes"));

        // Initialize listView
        this.list = new CutListView(this.context);
        //list.setTransitionEffect(new WaveEffect());

        // Method to set the first image and initialize the imageView
        ArrayList<Anime> animesArray = Anime.getAnimesFromJson(animes);
        this.list.setAdapter(null);
        this.list.setAdapter(new LastAnimeAdapter(this.context, animesArray));
        this.setInitialBackground();

        SfPanel correction = new SfPanel().setSize(-100, -60);
        SfPanel listPanel = new SfPanel().setSize(-70, -40);
        this.screen.append(correction).append(listPanel);


        // Adding menu panel
        correction.setAlignment(SfPanel.SF_ALIGNMENT_LEFT);
        SfPanel menuPanel = new SfPanel().setSize(-20, -18);
        menuPanel.setMargin(SfScreen.getInstance(this.context).getDpY(70), 0, 0, SfScreen.getInstance(this.context).getDpX(50));
        correction.append(menuPanel);

        SfPanel subMenuPanel = new SfPanel().setSize(-60, -60);
        ImageView subMenuView = new ImageView(this.context);
        int id = this.context.getResources().getIdentifier("menu", "drawable", this.context.getPackageName());
        Bitmap image = BitmapFactory.decodeStream(this.context.getResources().openRawResource(id));
        subMenuView.setImageBitmap(image);
        subMenuView.setBackgroundResource(R.color.blackTrans);
        subMenuPanel.setView(subMenuView);
        menuPanel.append(subMenuPanel);
        this.addView(subMenuView);

        subMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MenuController.class);
                intent.putExtra("latestAnimes", activity.getIntent().getStringExtra("latestAnimes"));
                activity.startActivity(intent);
            }
        });



        //list.setBackgroundResource(R.color.blackTrans);
        //this.list.setBackgroundResource(R.drawable.last_animes_background);
        this.list.setOnScrollListener(new LastAnimesListImp(new OnScrollListViewMiddle() {
            @Override
            public void onMiddle(int position) {

                Log.d("AB_DEV", "Position :::: " + position);
                setDynamicBackground(position);
            }

            @Override
            public void onScrollMove(int position) {
                setOnScrollSelectedItem(position);
            }
        }));

        listPanel.setView(this.list);
        this.addView(this.list);

        this.screen.update(this.context);
    }

    private void setInitialBackground() {

        this.screenView = new ImageView(this.context);
        this.screenView.setAdjustViewBounds(true);
        this.screenView.setScaleType(ImageView.ScaleType.CENTER);

        this.addView(this.screenView);
        this.screen.setView(this.screenView);
        this.screen.setAlignment(SfPanel.SF_ALIGNMENT_RIGHT);

        if (this.animes.length() >= 1) {
            this.setDynamicBackground(0);
            this.setOnScrollSelectedItem(0);
        }
    }

    private void setOnScrollSelectedItem(int position) {

        if (this.prevView != null) {

            this.prevView.setBackgroundResource(R.color.trans);
            this.prevView.setPadding(0, 0, 0, 0);
        }

        View view = getViewByPosition(position, this.list);
        view.setBackgroundColor(Color.rgb(237, 178, 0));
        view.setPadding(0, 0, 200, 0);
        this.prevView = view;
    }

    private void setDynamicBackground(int position) {

        try {

            JSONObject firstAnime = this.animes.getJSONObject(position);
            String cover = firstAnime.getString("cover");
            if (!cover.equals("")) {
                // Download the image
                this.setImageFromUrl(this.parseUrl(cover), this.screenView);
            } else {
                // Random background color
                this.screenView.setBackgroundColor(this.randomColor());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String parseUrl(String url) {

        String ret = "";

        try {

            URL uri = new URL(url);
            ret = url.toString();

            if (BuildConfig.debugMode) {
                ret = ret.replace("localhost", "192.168.1.69");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private void setImageFromUrl(String url, ImageView view) {

        SfScreen screen = SfScreen.getInstance(this.context);
        Picasso.with(this.context).load(url).resize(screen.getScreenAxis(SfScreen.ScreenWidth), screen.getScreenAxis(SfScreen.ScreenHeight)).into(view);
    }

    public View getViewByPosition(int pos, ListView listView) {

        pos = pos + 2;

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private int randomColor() {

        Integer c1 = 1 + (int)(Math.random() * ((255 - 1) + 1));
        Integer c2 = 3 + (int)(Math.random() * ((254 - 3) + 3));
        Integer c3 = 5 + (int)(Math.random() * ((253 - 5) + 5));
        return Color.rgb(c1, c2, c3);
    }
}