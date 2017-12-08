package com.appbuilders.animediapremium.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuilders.animediapremium.Adapter.AnimeAdapter;
import com.appbuilders.animediapremium.Controls.SpacesItemDecoration;
import com.appbuilders.animediapremium.Core.Credentials;
import com.appbuilders.animediapremium.Libraries.JsonBuilder;
import com.appbuilders.animediapremium.Libraries.JsonFileManager;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTCallback;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTClient;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTRequest;
import com.appbuilders.animediapremium.Libraries.Rester.ReSTResponse;
import com.appbuilders.animediapremium.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 04/11/17
 */

public class GenresAnimesView extends SurfaceActivityView {

    private Credentials credentials;
    private SfPanel contentPanel;
        private SfPanel subContentPanel;
            private SfPanel searchButtonPanel;
        private SfPanel masonryPanel;

    private ArrayList<String> selectedAnimes;
    RecyclerView masonry;
    AnimeAdapter adapter;

    public GenresAnimesView(Context context) {
        super(context);
    }

    public GenresAnimesView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public GenresAnimesView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Initializing array
        this.selectedAnimes = new ArrayList<>();

        // Initializing views
        this.contentPanel = new SfPanel().setSize(-100, -92);
        this.subContentPanel = new SfPanel().setSize(-100, -90);
        this.searchButtonPanel = new SfPanel().setSize(-95, -8).setMargin(threeRuleY(30), 0,0,0);
        this.makeItScrollable(this.subContentPanel, "content");

        // Appends
        this.subScreen.append(this.contentPanel);
        this.contentPanel.append(this.subContentPanel).append(searchButtonPanel);

        // Ask for genres
        this.askForGenres();

        // Update
        this.screen.update(this.context);
    }

    protected void askForGenres() {

        this.credentials = Credentials.getInstance(this.context);

        if (!credentials.existsPreference("genresAnimes")) {
            if (credentials.existsPreviousLogin()) {

                ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/genres");
                ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
                request.addParameter("token", credentials.getToken());
                request.addField("user_id", credentials.getUserId());
                request.addField("bearer", credentials.getBearer());
                request.addField("uuid", credentials.getUserUuid());
                request.addField("bit", credentials.getBit());
                rest.execute(request, new ReSTCallback() {

                    @Override
                    public void onSuccess(ReSTResponse response) {

                        JSONObject res = JsonFileManager.stringToJSON(response.body);
                        Log.d("DXGO", "GENRES fragment animes ::: " + res.toString());

                        try {

                            if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                                final JSONArray array = res.getJSONArray("data");
                                loopGenres(array);
                                credentials.savePreference("genresAnimes", array.toString());

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
        } else {
            this.loopGenres(JsonBuilder.stringToJsonArray(credentials.getPreference("genresAnimes")));
        }
    }

    protected void loopGenres(JSONArray array) {

        Log.d("DXGO", "PASADO THIS ::: " + array.toString());

        if (array != null) {

            for (int i = 0; i < array.length(); i++) {

                try {

                    JSONObject temp = array.getJSONObject(i);
                    String id = temp.getString("id");
                    String name = temp.getString("name");

                    // Creating View
                    TextView nameView = new TextView(this.context);
                    nameView.setText(name);
                    nameView.setBackgroundResource(R.drawable.borders);
                    nameView.setTextColor(Color.WHITE);
                    nameView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    nameView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    nameView.setTag(id);
                    nameView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (selectedAnimes.contains(view.getTag())) {
                                view.setBackgroundResource(R.drawable.borders);
                                selectedAnimes.remove(view.getTag());
                            } else {
                                view.setBackgroundResource(R.drawable.borders_solid_yellow);
                                selectedAnimes.add((String) view.getTag());
                            }
                        }
                    });

                    // Creating panel
                    SfPanel panel = new SfPanel();
                    panel.setSize(-45, -7).setMargin(threeRuleY(35), (i % 2 == 0) ? threeRuleX(50) : 0, 0, 0);
                    panel.setView(nameView).setKey("genre_" + id);

                    // Append
                    this.subContentPanel.append(panel);

                    // Adds
                    this.addToScroll("content", nameView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Button search = new Button(this.context);
            search.setBackgroundResource(R.drawable.borders_solid_yellow);
            search.setTextColor(Color.WHITE);
            search.setText("Buscar");
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchAnime();
                }
            });

            //SfPanel searchPanel = new SfPanel().setSize(-95, -8);
            //searchPanel.setView(search).setMargin(threeRuleY(40), 0,0,0);
            //this.subContentPanel.append(searchPanel);
            //this.addToScroll("content", search);
            this.searchButtonPanel.setView(search);
            this.addView(search);
        }
        this.screen.update(this.context);
    }

    protected void searchAnime() {

        if (this.selectedAnimes.size() > 0) {

            Log.d("DXGO", "ANIMES ::: " + this.selectedAnimes.toString());

            ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/search/genres");
            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
            request.addParameter("token", credentials.getToken());
            request.addField("user_id", credentials.getUserId());
            request.addField("bearer", credentials.getBearer());
            request.addField("uuid", credentials.getUserUuid());
            request.addField("bit", credentials.getBit());
            request.addField("genres", this.selectedAnimes.toString());
            rest.execute(request, new ReSTCallback() {

                @Override
                public void onSuccess(ReSTResponse response) {

                    JSONObject res = JsonFileManager.stringToJSON(response.body);
                    Log.d("DXGO", "GENRES SEARCH ::: " + res.toString());

                    try {

                        if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                            final JSONArray array = res.getJSONArray("data");
                            if (array.length() > 0) {
                                showResults(array);
                            } else {
                                Toast.makeText(context, "No hay resultados que mostrar", Toast.LENGTH_SHORT).show();
                            }

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

        } else {
            Toast.makeText(this.context, "Debes seleccionar un genero antes de buscar", Toast.LENGTH_SHORT).show();
        }
    }

    protected void showResults(JSONArray array) {

        // Hiding genres
        this.subContentPanel.setSize(0, 0);
        this.searchButtonPanel.setSize(0,0);
        this.screen.update(this.context);

        // Creating masonry layout
        this.masonry = new RecyclerView(this.context);
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(16));

        this.masonryPanel = new SfPanel().setSize(-100, -82).setView(this.masonry);
        this.addView(this.masonry);

        // Filling the masonry
        this.fillGrid(array);
    }

    protected float threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

    protected float threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }

    protected void fillGrid(JSONArray animes) {

        if (this.adapter == null) {
            this.adapter = new AnimeAdapter(context, animes);
            masonry.setAdapter(this.adapter);
        } else {
            this.adapter.clear();
            this.adapter.addNews(animes);
        }
    }
}