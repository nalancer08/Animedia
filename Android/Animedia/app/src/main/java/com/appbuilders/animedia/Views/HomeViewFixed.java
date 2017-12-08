package com.appbuilders.animedia.Views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.LastAnimeAdapter;
import com.appbuilders.animedia.BuildConfig;
import com.appbuilders.animedia.Controller.ChromeWebPlayer;
import com.appbuilders.animedia.Controller.HomeController;
import com.appbuilders.animedia.Controller.PlayerController;
import com.appbuilders.animedia.Controls.CutListView;
import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.Core.Chapter;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Implement.LastAnimesListImp;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.Listener.OnScrollListViewMiddle;
import com.appbuilders.animedia.R;
import com.appbuilders.credentials.Configurations;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;
import com.brouding.simpledialog.SimpleDialog;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

public class HomeViewFixed extends SurfaceActivityView {

    protected JSONArray animes;

    private SfPanel correction;
    private SfPanel listPanel;

    private SfPanel recomendationPanel;
    private FloatingActionButton recomendationView;

    protected ImageView screenView;
    protected ListView list;
    protected View prevView = null;

    private boolean firstTime = false;

    public HomeViewFixed(Context context) {
        super(context);
    }

    public HomeViewFixed(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public HomeViewFixed(Context context, boolean fullScreen) {
        super(context, fullScreen);
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

        this.correction = new SfPanel().setSize(-100, -60);
        this.listPanel = new SfPanel().setSize(-70, -40);
        this.subScreen.append(correction).append(listPanel);

        //list.setBackgroundResource(R.color.blackTrans);
        //this.list.setBackgroundResource(R.drawable.last_animes_background);
        this.list.setOnScrollListener(new LastAnimesListImp(new OnScrollListViewMiddle() {
            @Override
            public void onMiddle(int position) {
                setDynamicBackground(position);

            }

            @Override
            public void onScrollMove(int position) {

                setOnScrollSelectedItem(position);
                //setDynamicBackground(position);
            }
        }));

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gotoAnimePlayer(i-2);
            }
        });

        listPanel.setView(this.list);
        this.addView(this.list);

        /** Version 3.0 **/
        this.setRecomendationButton();
        /*****************/

        this.screen.update(this.context);
        this.showTutorial();
    }

    public void setRecomendationButton() {

        this.recomendationView = new FloatingActionButton(this.context);
        this.recomendationView.setImageResource(R.drawable.fab_add);
        this.recomendationView.setColorNormalResId(R.color.yellowItemSelected);

        this.recomendationPanel = new SfPanel().setSize(-10, -18).
                setPosition(SfPanel.SF_POSITION_ABSOLUTE).
                setOrigin(SfPanel.SF_UNSET, SfPanel.SF_UNSET, threeRuleY(-100), threeRuleX(30)).
                setView(this.recomendationView);
        this.subScreen.append(this.recomendationPanel);
        this.addView(this.recomendationView);

        // Settign callback
        this.recomendationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Credentials credentials = Credentials.getInstance(context);

                if (credentials.existsPreviousLogin()) {

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_recomend_anime);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    // Settign callbacks
                    final EditText animeName = dialog.findViewById(R.id.recomend_name);

                    final Button recomendClose = dialog.findViewById(R.id.recomend_close);
                    recomendClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.hide();
                        }
                    });

                    final Button recomendSend = dialog.findViewById(R.id.recomend_send);
                    recomendSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String animeString = String.valueOf(animeName.getText());

                            if (!animeString.equals("")) {

                                recomendSend.setEnabled(false);
                                recomendClose.setEnabled(false);
                                sendRecomend(dialog, animeString);

                            } else {
                                Toast.makeText(context, "Debes ingresar un nombre anime, para recomendarlo", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    new SimpleDialog.Builder(context)
                        .setTitle("Debes iniciar sesiòn")
                        .setContent("Para poder recomendar un anime, debes iniciar sesiòn", 3)
                        .setBtnConfirmText("Iniciar Sesiòn")
                        .setBtnCancelText("Cancelar")
                        .onConfirm(new SimpleDialog.BtnCallback() {
                            @Override
                            public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {

                                HomeController instance = (HomeController) context;
                                instance.getMenu().openMenu();
                            }
                        })
                        .show();
                }
            }
        });
    }

    public void showTutorial() {

        final Configurations configs = Configurations.getInstance(this.context);

        if (!configs.exists("showed_tutorial_" + BuildConfig.VERSION_NAME)) {
            new ShowcaseView.Builder(this.activity)
                    .setTarget(new ViewTarget(this.list))
                    .setContentTitle("Últimos animes")
                    .setContentText("En esta lista encontraras los últimos capítulos de los animes que se encuentran en transmisión actualemnte.")
                    .setStyle(R.style.CustomShowcaseTheme3)
                    //.hideOnTouchOutside()
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                            final HomeController instance = (HomeController) context;
                            instance.getMenu().openMenu();

                            new ShowcaseView.Builder(activity)
                                    .setTarget(new ViewTarget(instance.getMenu()))
                                    .setContentTitle("Menú")
                                    .setContentText("Para activar este menú, debes deslizar la pantalla de izquierda a derecha.")
                                    .setStyle(R.style.CustomShowcaseTheme4)
                                    .withMaterialShowcase()
                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                        @Override
                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {

                                            new ShowcaseView.Builder(activity)
                                                    .setTarget(new ViewTarget(instance.getLoginButton()))
                                                    .setContentTitle("Inicio de sesión")
                                                    .setContentText("Para poder disfrutar de todo el contenido disponible en Animedia, debes iniciar sesión presionando este botón.")
                                                    .setStyle(R.style.CustomShowcaseTheme2)
                                                    .withMaterialShowcase()
                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                        @Override
                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                            configs.add("showed_tutorial_" + BuildConfig.VERSION_NAME, true);
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

    private void setInitialBackground() {

        this.screenView = new ImageView(this.context);
        this.screenView.setAdjustViewBounds(true);
        //this.screenView.setScaleType(ImageView.ScaleType.CENTER);
        this.screenView.setScaleType(ImageView.ScaleType.FIT_XY);

        this.addView(this.screenView);
        this.screen.setView(this.screenView);
        //this.screen.setAlignment(SfPanel.SF_ALIGNMENT_RIGHT);
        this.subScreen.setAlignment(SfPanel.SF_ALIGNMENT_RIGHT);

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
        view.setPadding(0, 0, threeRuleX(150), 0);
        this.prevView = view;
    }

    private void setDynamicBackground(int position) {

        try {

            JSONObject firstAnime = this.animes.getJSONObject(position);
            String cover = firstAnime.getString("cover");
            if (!cover.equals("")) {
                // Download the image
                this.setImageFromUrl(cover, this.screenView);
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

    private void setImageFromUrl(String url, final ImageView image) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        //ImageSize targetSize = new ImageSize(1000, 1600); // result Bitmap will be fit to this size
        imageLoader.loadImage(url, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

                int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                Bitmap imageB = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                image.setImageBitmap(imageB);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                image.setImageBitmap(loadedImage);

                //animeImage.setImageBitmap(loadedImage);
                /*if (SfScreen.getInstance(context).getScreenAxis(SfScreen.ScreenHeight) >= 2220) {
                    animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 1000, 1500, false));
                } else {
                    animeImage.setImageBitmap(Bitmap.createScaledBitmap(loadedImage, 512, 780, false));
                }*/
            }
        });

        /*Picasso.with(this.context).load(url).placeholder(R.drawable.placeholder).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                view.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                int id = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
                view.setImageBitmap(image);
            }
        });*/
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

    protected void gotoAnimePlayer(int position) {

        Intent intent;

        try {

            JSONObject anime = this.animes.getJSONObject(position);
            JSONObject media = anime.getJSONObject("media");
            Chapter chapter = new Chapter(media);

            if (chapter.getUrl().contains("animeflv")) {
                intent = new Intent(context, PlayerController.class);
            } else {
                intent = new Intent(context, ChromeWebPlayer.class);
            }

            intent.putExtra("media", media.toString());
            intent.putExtra("anime", anime.toString());
            activity.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int randomColor() {

        Integer c1 = 1 + (int)(Math.random() * ((255 - 1) + 1));
        Integer c2 = 3 + (int)(Math.random() * ((254 - 3) + 3));
        Integer c3 = 5 + (int)(Math.random() * ((253 - 5) + 5));
        return Color.rgb(c1, c2, c3);
    }

    protected int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }

    protected int threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    /************************************************************************************************
     *                                         Version 3.0                                          *
     ************************************************************************************************/

    protected void sendRecomend(final Dialog dialog, String name) {

        final Credentials credentials = Credentials.getInstance(this.context);

        ReSTClient rest = new ReSTClient(credentials.getUrl() + "/anime/recomend/new");
        ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
        request.addParameter("token", credentials.getToken());
        request.addField("user_id", credentials.getUserId());
        request.addField("bearer", credentials.getBearer());
        request.addField("uuid", credentials.getUserUuid());
        request.addField("bit", credentials.getBit());

        request.addField("name", name);

        rest.execute(request, new ReSTCallback() {

            @Override
            public void onSuccess(ReSTResponse response) {

                JSONObject res = JsonFileManager.stringToJSON(response.body);
                Log.d("DXGOP", "RECOMEND ::: " + res.toString());

                try {

                    if (res.getString("result").equals("success") && res.getInt("code") == 200) {
                        dialog.hide();
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
}