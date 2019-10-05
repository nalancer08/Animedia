package com.appbuilders.animedia.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.ChapterAdapter;
import com.appbuilders.animedia.Controls.AutoResizeTextView;
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
import com.appbuilders.credentials.Configurations;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 05/11/17
 */

public class SingleAnimeView extends SurfaceActivityView {

    final private static int ANIME_CHPATERS = 0;
    final private static int ANIME_OVAS = 1;
    final private static int ANIME_MOVIES = 2;

    private Credentials credentials;
    private SfPanel contentPanel;
        private SfPanel imageHeaderPanel;
            private SfPanel followAnimePanel;
            private SfPanel lenguagePanel;
        private SfPanel namePanel;
        private SfPanel descriptionPanel;
        private SfPanel loaderPanel;
        private SfPanel detailsPanel;
            private SfPanel genresPanel;
            private SfPanel typesPanel;
            private SfPanel chaptersPanel;

   private MaterialSpinner lenguageView;
   private AutoResizeTextView serieView;
   private AutoResizeTextView ovasView;
   private AutoResizeTextView movieView;

    protected Anime anime;
    protected String animeString;
    protected Typeface borgenBold;

    protected ListView chaptersView;
    protected List<String> audios;

    /** Version 3.5 **/
    protected int animeMediaState = 0;

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
        this.animeString = getIntent().getStringExtra("anime");
        this.anime = new Anime(JsonBuilder.stringToJson(animeString));
        //Log.d("DXGOP", "SINGLE VIEW ::: " + animeString);

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

        // Initialize arrays
        this.audios = new ArrayList<>();

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

    private void createImageHeader() {

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
            //this.addView(followAnimeButton);
        }
    }

    private void createAnimeName() {

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

    private void createAnimeDescription() {

        AutoResizeTextView animeDescription = new AutoResizeTextView(this.context);
        animeDescription.setTextColor(Color.WHITE);
        animeDescription.setText(this.anime.getDescription());
        //animeDescription.setBackgroundResource(R.color.colorAccent);
        animeDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        this.descriptionPanel.setView(animeDescription);
        this.contentPanel.append(this.descriptionPanel);
        this.addView(animeDescription);
    }

    private void getAnimeDetails() {

        /*PlayGifView gifView = new PlayGifView(this.context);
        gifView.setImageResource(R.drawable.circular_loader);
        gifView.setVelocity(2);*/
        AVLoadingIndicatorView gifView = new AVLoadingIndicatorView(this.context);
        gifView.setIndicator("BallSpinFadeLoaderIndicator");
        gifView.show();

        this.loaderPanel.setView(gifView);
        this.contentPanel.append(this.loaderPanel);
        this.addView(gifView);

        this.askForAnimeDetails();
    }

    private void askForAnimeDetails() {

        this.credentials = Credentials.getInstance(this.context);

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/anime");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        request.addField("user_id", credentials.getUserId());
        request.addField("bearer", credentials.getBearer());
        request.addField("uuid", credentials.getUserUuid());
        request.addField("bit", credentials.getBit());
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

    private void showAnimeDetails(JSONObject data) {

        // Remove loader
        this.loaderPanel.setSize(0,0);

        // Adding media list
        this.contentPanel.append(this.detailsPanel);

        // Creating objects
        try {

            JSONArray genres = data.getJSONArray("genres");
            final JSONArray chapters = data.getJSONArray("chapters");
            JSONArray audios = data.getJSONArray("audios");

            /** Version 3.5 **/
            //JSONArray types = data.getJSONArray("medias");

            // Parsing audios
            for (int k = 0; k < audios.length(); k++) {
                JSONObject audio = audios.getJSONObject(k);
                this.audios.add(audio.getString("audio"));
            }
            this.createLanguageSelect();

            // Creating genres panel
            /*this.genresPanel = new SfPanel().setSize(-100, -20);
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
            }*/

            // Creating type panel
            this.typesPanel = new SfPanel().setSize(-100, -20);
            this.detailsPanel.append(this.typesPanel);
            //this.createTypes(types);

            // Creating chapters panel
            if (chapters.length() > 0) {

                this.chaptersPanel = new SfPanel().setSize(-100, -80);
                this.detailsPanel.append(this.chaptersPanel);

                final ArrayList<Chapter> chaptersArray = Chapter.getChaptersFromJson(chapters);
                this.chaptersView = new ListView(this.context);
                this.chaptersView.setAdapter(null);
                this.chaptersView.setAdapter(new ChapterAdapter(this.context, chapters, animeString));
                this.chaptersView.setDivider(new ColorDrawable(0x00FFFFFF));
                this.chaptersView.setDividerHeight(threeRuleY(50));
                this.chaptersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        /*Log.d("DXGOP", "URL a scannear :: " + chaptersArray.get(i).getUrl());
                        com.appbuilders.credentials.Rester.ReSTClient rest = new com.appbuilders.credentials.Rester.ReSTClient(chaptersArray.get(i).getUrl());
                        com.appbuilders.credentials.Rester.ReSTRequest request = new com.appbuilders.credentials.Rester.ReSTRequest(com.appbuilders.credentials.Rester.ReSTRequest.REST_REQUEST_METHOD_POST, "");
                        rest.execute(request, new com.appbuilders.credentials.Rester.ReSTCallback() {
                            @Override
                            public void onSuccess(com.appbuilders.credentials.Rester.ReSTResponse response) {

                                String resp = response.body;
                                Log.d("DXGOP", "URL PASO 1 :: " + resp);
                                List<String> extractedUrls = extractUrls(resp);

                                for (String url : extractedUrls) {
                                   Log.d("DXGOP", "PASO 2 ::: " + url);
                                }

                            }

                            @Override
                            public void onError(com.appbuilders.credentials.Rester.ReSTResponse reSTResponse) {

                            }
                        });*/




                        //Log.d("DXGOP", "BAJANDO CAPITULO ::: " );
                //        Uri downloadUri = Uri.parse("http://s3.animeflv.com/efire.php?v=cGNJV2w0L0xGdWI0TnBOcmZtY0JpdFBKWWV5eHFZRTNCU3VlYytRa3k1cz0=");
                //        Uri destinationUri = Uri.parse(context.getExternalCacheDir().toString() + "/erick1234.mp4");

                        // Setting timeout globally for the download network requests:
                        /*PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                                .build();
                        PRDownloader.initialize(context, config);

                        int downloadId = PRDownloader.download(chaptersArray.get(i).getUrl(), context.getExternalCacheDir().toString(), "eri_1234.mp4")
                                .build()
                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {

                                    }
                                })
                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {

                                    }
                                })
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
                                        Log.d("DXGOP", "progress ::: " + progress);

                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        Log.d("DXGOP", "ACABO");
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        Log.d("DXGOP", "FALLO ::: " + error);
                                    }
                                });*/




                        /*DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                                .setRetryPolicy(new DefaultRetryPolicy())
                                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                                .setStatusListener(new DownloadStatusListenerV1() {
                                    @Override
                                    public void onDownloadComplete(DownloadRequest downloadRequest) {

                                    }

                                    @Override
                                    public void onDownloadFailed(DownloadRequest downloadRequest, int i, String s) {
                                        Log.d("DXGOP", "FALLO ::: " + s);
                                    }

                                    @Override
                                    public void onProgress(DownloadRequest downloadRequest, long l, long l1, int i) {
                                        Log.d("DXGOP", "UNO :: " + l + " DOS ::: " + l1 + " TRES ::: " + i);
                                    }
                                });*/


                        //Intent intent = new Intent(context, ChromeWebPlayer.class);
                        //intent.putExtra("media", chapters.getJSONObject(i).toString());
                        //intent.putExtra("anime", animeString);
                        //activity.startActivity(intent);

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

        // Showing tutorial if it's available
        //this.showTutorial();
    }

    private void createTypes(JSONArray types) {

        SfPanel seriePanel = new SfPanel().setSize(-31.5f, -60).setMargin(0, threeRuleX(20), 0, 0);
        SfPanel ovasPanel = new SfPanel().setSize(-31.5f, -60).setMargin(0, threeRuleX(20), 0, 0);
        SfPanel moviePanel = new SfPanel().setSize(-31.5f, -60);
        this.typesPanel.append(seriePanel).append(ovasPanel).append(moviePanel);

        this.serieView = new AutoResizeTextView(this.context);
        serieView.setText("Serie");
        serieView.setTextColor(Color.WHITE);
        serieView.setBackgroundResource(R.drawable.borders_solid_yellow);
        serieView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        serieView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        serieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (animeMediaState != SingleAnimeView.ANIME_CHPATERS) {
                    showNotAvailable(view, "Cambiando a capitulos");
                    askForNewMedias("chapter", "");
                    animeMediaState = SingleAnimeView.ANIME_CHPATERS;

                    // Setting select color
                    serieView.setBackgroundResource(R.drawable.borders_solid_yellow);
                    ovasView.setBackgroundResource(R.drawable.borders);
                    movieView.setBackgroundResource(R.drawable.borders);
                }
            }
        });

        this.ovasView = new AutoResizeTextView(this.context);
        ovasView.setText("Ovas");
        ovasView.setTextColor(Color.WHITE);
        ovasView.setBackgroundResource(R.drawable.borders);
        ovasView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        ovasView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        this.movieView = new AutoResizeTextView(this.context);
        movieView.setText("Peliculas");
        movieView.setTextColor(Color.WHITE);
        movieView.setBackgroundResource(R.drawable.borders);
        movieView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        movieView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        seriePanel.setView(serieView);
        ovasPanel.setView(ovasView);
        moviePanel.setView(movieView);

        this.addView(serieView);

        /** Version 3.5 **/
        for (int j = 0; j < types.length(); j++) {

            try {

                JSONObject obj = types.getJSONObject(j);
                String type = obj.getString("type");
                switch(type) {

                    case "ova":

                        ovasView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (animeMediaState != SingleAnimeView.ANIME_OVAS) {
                                    showNotAvailable(view, "Cambiando a ovas");
                                    askForNewMedias("ova", "");
                                    animeMediaState = SingleAnimeView.ANIME_OVAS;

                                    // Setting select color
                                    ovasView.setBackgroundResource(R.drawable.borders_solid_yellow);
                                    serieView.setBackgroundResource(R.drawable.borders);
                                    movieView.setBackgroundResource(R.drawable.borders);
                                }

                            }
                        });
                        this.addView(ovasView);

                    break;

                    case "movie":
                        movieView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (animeMediaState != SingleAnimeView.ANIME_MOVIES) {
                                    showNotAvailable(view, "Cambiando a peliculas");
                                    askForNewMedias("movie", "");
                                    animeMediaState = SingleAnimeView.ANIME_MOVIES;

                                    // Setting select color
                                    movieView.setBackgroundResource(R.drawable.borders_solid_yellow);
                                    ovasView.setBackgroundResource(R.drawable.borders);
                                    serieView.setBackgroundResource(R.drawable.borders);
                                }
                            }
                        });
                        this.addView(movieView);

                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createLanguageSelect() {

        // Fixing audios
        if (this.audios.size() < 2) {
            this.audios.add("");
        }

        this.lenguageView = new MaterialSpinner(this.context);
        this.lenguageView.setItems(this.audios);
        this.lenguageView.setBackgroundResource(R.drawable.borders_solid_yellow);
        this.lenguageView.setTextColor(Color.BLACK);
        this.lenguageView.setArrowColor((!this.audios.get(this.audios.size() - 1).equals("")) ? Color.BLACK : Color.argb(1,237, 178, 0));

        this.lenguageView.setClickable((!this.audios.get(this.audios.size() - 1).equals("")) ? true : false);
        this.lenguageView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (!item.equals("")) {
                    askForNewMedias("chapter", item);
                    Snackbar snack = Snackbar.make(view, "Cambiando a idioma: " + item, Snackbar.LENGTH_LONG);
                    snack.getView().setBackgroundResource(R.color.yellowItemSelected);
                    TextView text = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    text.setTextColor(Color.BLACK);
                    snack.show();
                }
            }
        });

        this.lenguagePanel = new SfPanel().setSize(-30, -22).setView(lenguageView);
        this.lenguagePanel.setPosition(SfPanel.SF_POSITION_ABSOLUTE).setOrigin(SfPanel.SF_UNSET, SfPanel.SF_UNSET, threeRuleY(25), threeRuleX(15));
        this.imageHeaderPanel.append(this.lenguagePanel);
        this.addView(lenguageView);
    }

    private void askForNewMedias(final String type, String audio) {

        this.credentials = Credentials.getInstance(this.context);
        this.chaptersView.setAdapter(null);

        ReSTClient rest = null;
        switch(type) {

            case "chapter":
                rest = new ReSTClient(credentials.getUrl() + "/anime/medias");
                break;

            case "ova":
                rest = new ReSTClient(credentials.getUrl() + "/anime/medias/ovas");
                break;

            case "movie":
                rest = new ReSTClient(credentials.getUrl() + "/anime/medias/movies");
                break;
        }

        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        request.addField("user_id", credentials.getUserId());
        request.addField("bearer", credentials.getBearer());
        request.addField("uuid", credentials.getUserUuid());
        request.addField("bit", credentials.getBit());
        request.addField("anime_id", String.valueOf(anime.getId()));
        request.addField("type", type);
        request.addField("audio", audio);
        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                JSONObject res = JsonFileManager.stringToJSON(response.body);
                Log.d("DXGO", "ANIME NEW MEDIAS ::: " + type + " " + res.toString());

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                        final JSONArray data = res.getJSONArray("data");
                        updateAnimeDetails(data);

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

    private void updateAnimeDetails(final JSONArray chapters) {

        // data is equals to chapters or ovas or movies
        final ArrayList<Chapter> chaptersArray = Chapter.getChaptersFromJson(chapters);
        this.chaptersView.setAdapter(null);
        this.chaptersView.setAdapter(new ChapterAdapter(this.context, chapters, animeString));
        this.chaptersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                /*Log.d("DXGOP", "URL a scannear :: " + chaptersArray.get(i).getUrl());
                com.appbuilders.credentials.Rester.ReSTClient rest = new com.appbuilders.credentials.Rester.ReSTClient(chaptersArray.get(i).getUrl());
                com.appbuilders.credentials.Rester.ReSTRequest request = new com.appbuilders.credentials.Rester.ReSTRequest(com.appbuilders.credentials.Rester.ReSTRequest.REST_REQUEST_METHOD_GET, "");
                rest.execute(request, new com.appbuilders.credentials.Rester.ReSTCallback() {
                    @Override
                    public void onSuccess(com.appbuilders.credentials.Rester.ReSTResponse response) {

                        String resp = response.body;
                        Log.d("DXGOP", "URL PASO 1 :: " + resp);
                        List<String> extractedUrls = extractUrls(resp);

                        for (String url : extractedUrls) {

                            Log.d("DXGOP", "PASO 2 ::: " + url);
                            if (url.contains("mediafire")) {

                                com.appbuilders.credentials.Rester.ReSTClient rest = new com.appbuilders.credentials.Rester.ReSTClient(url);
                                com.appbuilders.credentials.Rester.ReSTRequest request = new com.appbuilders.credentials.Rester.ReSTRequest(com.appbuilders.credentials.Rester.ReSTRequest.REST_REQUEST_METHOD_GET, "");
                                rest.execute(request, new com.appbuilders.credentials.Rester.ReSTCallback() {
                                    @Override
                                    public void onSuccess(com.appbuilders.credentials.Rester.ReSTResponse response) {

                                        String resp = response.body;
                                        Log.d("DXGOP", "FIN PASO 1 :: " + resp);

                                        String pag = resp.split("http://download")[1];
                                        pag = pag.split("\"")[0];
                                        String url = pag.split("'")[0];
                                        if (url.equals("")) {
                                            Log.d("DXGOP", "MORIIIIIII");
                                        } else {
                                            //$('#videoLoading').remove();
                                            url = "http://download" + url;
                                        }
                                        Log.d("DXGOP", "FIN PASO 2 :: " + url);
                                    }

                                    @Override
                                    public void onError(com.appbuilders.credentials.Rester.ReSTResponse reSTResponse) {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onError(com.appbuilders.credentials.Rester.ReSTResponse reSTResponse) {

                    }
                });*/


        //        Log.d("DXGOP", "BAJANDO CAPITULO ::: ");
        //        Uri downloadUri = Uri.parse(chaptersArray.get(i).getUrl());
        //        Uri destinationUri = Uri.parse(context.getExternalCacheDir().toString() + "/erick1234.mp4");

                // Setting timeout globally for the download network requests:
                /*PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                        .build();
                PRDownloader.initialize(context, config);

                int downloadId = PRDownloader.download("http://download1953.mediafire.com/c73qr9d1yehg/tjj70ag24sl7n52/2793_5", context.getExternalCacheDir().toString(), "er.mp4")
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {

                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                Log.d("DXGOP", "progress ::: " + progress);

                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                Log.d("DXGOP", "ACABO");
                            }

                            @Override
                            public void onError(Error error) {
                                Log.d("DXGOP", "FALLO ::: " + error);
                            }
                        });*/


                //Log.d("DXGO", "PICADO ::: " + chapters.getJSONObject(i).toString());
                    /*Intent intent = new Intent(context, ChromeWebPlayer.class);
                    intent.putExtra("media", chapters.getJSONObject(i).toString());
                    intent.putExtra("anime", animeString);
                    activity.startActivity(intent);*/

            }
        });
    }

    private int threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    private int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((http?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    private void showNotAvailable(View view, String message) {

        Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snack.getView().setBackgroundResource(R.color.yellowItemSelected);
        TextView text = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(Color.BLACK);
        snack.show();
    }

    public void showTutorial() {

        final Configurations configs = Configurations.getInstance(this.context);

        if (!configs.exists("showed_single_anime_tutorial")) {
            new ShowcaseView.Builder(this.activity)
                    .setTarget(new ViewTarget(this.lenguageView))
                    .setContentTitle("Idiomas")
                    .setContentText("Si hay idiomas disponibles, aquí podrás cambiarlo. \n Normalmente encontraras japones subtitulado y latino.")
                    .setStyle(R.style.SingleAnime)
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                            new ShowcaseView.Builder(activity)
                                    .setTarget(new ViewTarget(serieView))
                                    .setContentTitle("Capítulos  disponibles")
                                    .setContentText("Siempre empezara en esta pestaña, aquí encontraras todos los capítulos disponibles en Animedia\n")
                                    .setStyle(R.style.SingleAnime)
                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                        @Override
                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                                            new ShowcaseView.Builder(activity)
                                                    .setTarget(new ViewTarget(ovasView))
                                                    .setContentTitle("OVAS  disponibles")
                                                    .setContentText("En esta pestaña, encontraras todos las OVAS disponibles en Animedia\n")
                                                    .setStyle(R.style.SingleAnime)
                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                        @Override
                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                                                            new ShowcaseView.Builder(activity)
                                                                    .setTarget(new ViewTarget(movieView))
                                                                    .setContentTitle("Películas  disponibles")
                                                                    .setContentText("En esta pestaña, encontraras todos las películas disponibles en Animedia\n")
                                                                    .setStyle(R.style.SingleAnime)
                                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                                        @Override
                                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                                                                            View view = getViewByPosition(chaptersView.getChildCount() - 1, chaptersView);
                                                                            View playButton = view.findViewById(R.id.playButton);


                                                                            new ShowcaseView.Builder(activity)
                                                                                    .setTarget(new ViewTarget(playButton))
                                                                                    .setContentTitle("Reproducir un capitulo")
                                                                                    .setContentText("Para poder reproducir un capitulo, deberás simplemente presionar en el icono de reproducir.\n")
                                                                                    .setStyle(R.style.SingleAnime)
                                                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                                                        @Override
                                                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                                                            configs.add("showed_single_anime_tutorial", true);
                                                                                        }

                                                                                        @Override
                                                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                                                                        }
                                                                                    })
                                                                                    .build();
                                                                        }

                                                                        @Override
                                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                                        }

                                                                        @Override
                                                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                                        }

                                                                        @Override
                                                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                                                        }
                                                                    })
                                                                    .build();
                                                        }

                                                        @Override
                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                        }

                                                        @Override
                                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                        }

                                                        @Override
                                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                                        }
                                                    })
                                                    .build();
                                        }

                                        @Override
                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                        }

                                        @Override
                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                        }

                                        @Override
                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                        }
                                    })
                                    .build();
                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                        }
                    })
                    .build();
        }
    }

    public View getViewByPosition(int pos, ListView listView) {

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}