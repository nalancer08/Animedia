package com.appbuilders.animedia.FragmentViews;

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

import com.appbuilders.animedia.Adapter.AnimeAdapter;
import com.appbuilders.animedia.Controls.SpacesItemDecoration;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 04/11/17
 */

public class AllAnimesView extends SurfaceActivityView {

    private Credentials credentials;
    private SfPanel contentPanel;
        private SfPanel subContentPanel;
            private SfPanel searchButtonPanel;
        private SfPanel masonryPanel;

    private ArrayList<String> selectedAnimes;
    RecyclerView masonry;
    AnimeAdapter adapter;

    public AllAnimesView(Context context) {
        super(context);
    }

    public AllAnimesView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public AllAnimesView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Initializing panels
        this.contentPanel = new SfPanel();

        // Creating masonry layout
        this.createMasonryLayout();

        // Asking for projects
        this.askForLatestAnimes();

    }

    private void createMasonryLayout() {

        this.masonry = new RecyclerView(this.context);
         this.masonry.getRecycledViewPool().setMaxRecycledViews(0,50);
        //this.masonry.getRecycledViewPool().setMaxRecycledViews(View.TYPE_CAROUSEL, 0);
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(20));

        this.contentPanel.setView(this.masonry).setSize(-100, -82);
        this.addView(this.masonry);
    }

    protected void askForLatestAnimes() {

        this.credentials = Credentials.getInstance(this.context);

        if (!credentials.existsPreference("allAnimes")) {
            if (credentials.existsPreviousLogin()) {

                ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes");
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
                        Log.d("DXGOP", "All fragment animes ::: " + res.toString());

                        try {

                            if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                                final JSONArray array = res.getJSONArray("data");
                                fillGrid(array);
                                credentials.savePreference("allAnimes", array.toString());

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
            this.fillGrid(JsonBuilder.stringToJsonArray(credentials.getPreference("allAnimes")));
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
}