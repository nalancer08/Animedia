package com.appbuilders.animedia.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.AnimeAdapter;
import com.appbuilders.animedia.Controls.SpacesItemDecoration;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 05/11/17
 */

public class SearchAnimesView extends SurfaceActivityView {

    private Credentials credentials;
    private SfPanel contentPanel;
        private SfPanel searchPanel;
            private SfPanel searchPanelView;
            private SfPanel searchPanelButton;
        private SfPanel masonryPanel;

    RecyclerView masonry;
    AnimeAdapter adapter;

    public SearchAnimesView(Context context) {
        super(context);
    }

    public SearchAnimesView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public SearchAnimesView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Initializing panels
        this.contentPanel = new SfPanel().setSize(-100, -82);
        this.searchPanel = new SfPanel().setSize(-100, -8).setMargin(threeRuleY(30), 0,0,threeRuleX(0));
        this.masonryPanel = new SfPanel().setSize(-100, -92);

        this.searchPanelView = new SfPanel().setSize(-70, -100).setMargin(0,0,0, threeRuleX(10));
        this.searchPanelButton = new SfPanel().setSize(-28, -100);

        // Appends
        this.subScreen.append(this.contentPanel);
        this.contentPanel.append(this.searchPanel).append(this.masonryPanel);
        this.searchPanel.append(searchPanelView).append(searchPanelButton);

        // Creating search view
        this.createSearchView();

        // Creating masonry view
        this.createMasonryLayout();

        // Update
        this.screen.update(this.context);
    }

    protected void createSearchView() {

        int id = this.context.getResources().getIdentifier("search", "drawable", this.context.getPackageName());
        Bitmap image = BitmapFactory.decodeStream(this.context.getResources().openRawResource(id));

        final EditText searchView = new EditText(this.context);
        searchView.setBackgroundResource(R.drawable.borders_solid_white);
        searchView.setHint("Anime a buscar");
        searchView.setCompoundDrawablePadding(threeRuleX(30));
        searchView.setPadding(threeRuleX(30), 0,0,0);
        searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
        searchView.setTextColor(Color.BLACK);
        this.searchPanelView.setView(searchView);
        this.addView(searchView);


        Button searchButton = new Button(this.context);
        searchButton.setBackgroundResource(R.drawable.borders_solid_yellow);
        searchButton.setTextColor(Color.WHITE);
        searchButton.setText("Buscar");
        this.searchPanelButton.setView(searchButton);
        this.addView(searchButton);

        // Setting callback for search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mSearch = String.valueOf(searchView.getText());
                if (!mSearch.equals("") && mSearch.length() >= 3 ) {
                    searchAnime(mSearch);
                    hideSoftKeyboard(activity);
                } else {
                    Toast.makeText(context, "Debes escribir algo para buscar", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void createMasonryLayout() {

        this.masonry = new RecyclerView(this.context);
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(16));

        this.masonryPanel.setView(this.masonry);
        this.addView(this.masonry);
    }

    protected void searchAnime(String query) {

        this.credentials = Credentials.getInstance(this.context);

        if (!query.equals("")) {

            ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/search");
            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
            request.addParameter("token", credentials.getToken());
            request.addField("user_id", credentials.getUserId());
            request.addField("bearer", credentials.getBearer());
            request.addField("uuid", credentials.getUserUuid());
            request.addField("bit", credentials.getBit());
            request.addField("search", query);
            rest.execute(request, new ReSTCallback() {

                @Override
                public void onSuccess(ReSTResponse response) {

                    JSONObject res = JsonFileManager.stringToJSON(response.body);
                    Log.d("DXGO", "QUERY SEARCH ::: " + res.toString());

                    try {

                        if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                            final JSONArray array = res.getJSONArray("data");
                            if (array.length() > 0) {
                                fillGrid(array);
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

    protected void fillGrid(JSONArray animes) {

        if (this.adapter == null) {
            this.adapter = new AnimeAdapter(context, animes);
            masonry.setAdapter(this.adapter);
        } else {
            this.adapter.clear();
            this.adapter.addNews(animes);
        }
    }

    protected float threeRuleY(int value) {

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

    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}