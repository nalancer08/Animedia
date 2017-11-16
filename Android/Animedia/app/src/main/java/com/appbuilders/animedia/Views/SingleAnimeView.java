package com.appbuilders.animedia.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.ChapterAdapter;
import com.appbuilders.animedia.Controller.ChromeWebPlayer;
import com.appbuilders.animedia.Controls.AutoResizeTextView;
import com.appbuilders.animedia.Controls.PlayGifView;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.TransitionGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 05/11/17
 */

public class SingleAnimeView extends SurfaceActivityView {

    private Credentials credentials;
    private SfPanel contentPanel;
        private SfPanel imageHeaderPanel;
            private SfPanel followAnimePanel;
        private SfPanel namePanel;
        private SfPanel descriptionPanel;
        private SfPanel loaderPanel;
        private SfPanel detailsPanel;
            private SfPanel genresPanel;
            private SfPanel chaptersPanel;

    protected Anime anime;
    protected Typeface borgenBold;

    public SingleAnimeView(Context context) {
        super(context);
    }

    public SingleAnimeView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public SingleAnimeView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Creating anime object
        String animeString = getIntent().getStringExtra("anime");
        this.anime = new Anime(JsonBuilder.stringToJson(animeString));
        Log.d("DXGO", "SINGLE VIEW ::: " + animeString);

        // Creating typeface
        this.borgenBold =  Typeface.createFromAsset( this.context.getAssets(), "BorgenBold.ttf");

        // Setting background
        this.screenCanvas.setBackgroundResource(R.color.gray);

        // Initializing panels
        this.contentPanel = new SfPanel().setSize(-100,-100);
            this.imageHeaderPanel = new SfPanel().setSize(-100, -30);
            this.namePanel = new SfPanel().setSize(-97, -10);
            this.descriptionPanel = new SfPanel().setSize(-97, -14);
            this.loaderPanel = new SfPanel().setSize(-40,-20).setMargin(threeRuleY(250), 0,0,0);
            this.detailsPanel = new SfPanel().setSize(-100,-46);

        // Appends
        this.screen.append(this.contentPanel);

        // Create image header
        this.createImageHeader();

        // Create name
        this.createAnimeName();

        // Create anime description
        this.createAnimeDescription();

        // Asking for anime details
        this.getAnimeDetails();

        // Update
        this.screen.update(this.context);
    }

    protected void createImageHeader() {

        if (anime != null) {

            /*final ImageView imageHeaderView = new ImageView(this.context);
            imageHeaderView.setAdjustViewBounds(true);
            imageHeaderView.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

            KenBurnsView imageHeaderView = new KenBurnsView(this.context);
            imageHeaderView.setAdjustViewBounds(true);
            imageHeaderView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /*Picasso.with(this.context).load(anime.getCover()).placeholder(R.drawable.placeholder).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageHeaderView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            });*/
            //Picasso.with(this.context).load(anime.getCover()).placeholder(R.drawable.placeholder).into(imageHeaderView);
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            imageLoader.displayImage(anime.getCover(), imageHeaderView);

            this.imageHeaderPanel.setView(imageHeaderView);
            this.contentPanel.append(this.imageHeaderPanel);
            this.addView(imageHeaderView);

            Button followAnimeButton = new Button(this.context);
            followAnimeButton.setBackgroundResource(R.drawable.borders_solid_black_trans);
            followAnimeButton.setTextColor(Color.WHITE);
            followAnimeButton.setText("Seguir");

            this.followAnimePanel = new SfPanel().setSize(-25, -20).setView(followAnimeButton);
            this.followAnimePanel.setPosition(SfPanel.SF_POSITION_ABSOLUTE).setOrigin(SfPanel.SF_UNSET, threeRuleX(25), threeRuleY(25), SfPanel.SF_UNSET);
            this.imageHeaderPanel.append(this.followAnimePanel);
            this.addView(followAnimeButton);
        }
    }

    protected void createAnimeName() {

        TextView animeName = new TextView(this.context);
        animeName.setTextColor(Color.WHITE);
        animeName.setText(this.anime.getName());
        animeName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        animeName.setTypeface(this.borgenBold);
        animeName.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        animeName.setGravity(Gravity.CENTER_VERTICAL);

        this.namePanel.setView(animeName).setMargin(threeRuleY(-20), 0, 0,0);
        this.contentPanel.append(this.namePanel);
        this.addView(animeName);
    }

    protected void createAnimeDescription() {

        AutoResizeTextView animeDescription = new AutoResizeTextView(this.context);
        animeDescription.setTextColor(Color.WHITE);
        animeDescription.setText(this.anime.getDescription());
        //animeDescription.setBackgroundResource(R.color.colorAccent);
        animeDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        this.descriptionPanel.setView(animeDescription);
        this.contentPanel.append(this.descriptionPanel);
        this.addView(animeDescription);
    }

    protected void getAnimeDetails() {

        PlayGifView gifView = new PlayGifView(this.context);
        gifView.setImageResource(R.drawable.circular_loader);
        gifView.setVelocity(2);

        this.loaderPanel.setView(gifView);
        this.contentPanel.append(this.loaderPanel);
        this.addView(gifView);

        this.askForAnimeDetails();
    }

    protected void askForAnimeDetails() {

        this.credentials = Credentials.getInstance(this.context);

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/anime");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        request.addField("user_id", credentials.getUserId());
        request.addField("bearer", credentials.getBearer());
        request.addField("anime_id", String.valueOf(anime.getId()));
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                JSONObject res = JsonFileManager.stringToJSON(response.body);
                Log.d("DXGO", "ANIME DETAILS ::: " + res.toString());

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        final JSONObject data = res.getJSONObject("data");
                        showAnimeDetails(data);

                    } else {
                        //showErrorAlert("Error", "Problemas de conexión");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ReSTResponse response) {

                String errorMessage;
                if (response.statusCode == 404) {
                    errorMessage = "HUMAN used SEARCH\nBut, it failed!";
                } else {
                    errorMessage = "Error " + Integer.toString(response.statusCode);
                }
                Toast.makeText(context, "Try again!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void showAnimeDetails(JSONObject data) {

        // Remove loader
        this.loaderPanel.setSize(0,0);

        // Adding media list
        this.contentPanel.append(this.detailsPanel);

        // Creating objects
        try {

            JSONArray genres = data.getJSONArray("genres");
            final JSONArray chapters = data.getJSONArray("chapters");

            // Creating genres panel
            this.genresPanel = new SfPanel().setSize(-100, -20);
            this.detailsPanel.append(genresPanel);
            for (int i = 0; i < genres.length(); i++) {

                JSONObject genre = genres.getJSONObject(i);

                AutoResizeTextView genreView = new AutoResizeTextView(this.context);
                genreView.setText(genre.getString("name"));
                genreView.setTextColor(Color.WHITE);
                genreView.setBackgroundResource(R.drawable.borders_solid_yellow);
                genreView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                genreView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                SfPanel genrePanel = new SfPanel().setSize(-(100/genres.length()),-50).setView(genreView);
                genrePanel.setMargin(threeRuleY(30), 0,0,0);
                this.genresPanel.append(genrePanel);
                this.addView(genreView);
            }

            // Creating chapters panel
            if (chapters.length() > 0) {

                this.chaptersPanel = new SfPanel().setSize(-100, -80);
                this.detailsPanel.append(this.chaptersPanel);

                ArrayList<Chapter> chaptersArray = Chapter.getChaptersFromJson(chapters);
                ListView chaptersView = new ListView(this.context);
                chaptersView.setAdapter(null);
                chaptersView.setAdapter(new ChapterAdapter(this.context, chaptersArray));
                chaptersView.setDivider(new ColorDrawable(0x00FFFFFF));
                chaptersView.setDividerHeight(threeRuleY(50));
                chaptersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {

                            //Log.d("DXGO", "PICADO ::: " + chapters.getJSONObject(i).toString());
                            Intent intent = new Intent(context, ChromeWebPlayer.class);
                            intent.putExtra("media", chapters.getJSONObject(i).toString());
                            activity.startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                this.chaptersPanel.setView(chaptersView);
                this.addView(chaptersView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Update
        this.screen.update(this.context);
    }

    protected int threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    protected int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }
}