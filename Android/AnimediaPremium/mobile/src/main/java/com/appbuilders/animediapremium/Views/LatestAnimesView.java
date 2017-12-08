package com.appbuilders.animediapremium.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.AbsoluteLayout;
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
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 02/11/17
 */

public class LatestAnimesView extends SurfaceActivityView {

    Credentials credentials;

    RecyclerView masonry;
    AnimeAdapter adapter;

    SfPanel contentPanel;

    public LatestAnimesView(Context context) {
        super(context);
    }

    public LatestAnimesView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public LatestAnimesView(Context context, boolean fullScreen) {
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
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(16));

        this.contentPanel.setView(this.masonry).setSize(-100, -82);
        this.addView(this.masonry);
    }

    protected void askForLatestAnimes() {

        this.credentials = Credentials.getInstance(this.context);

        if (!credentials.existsPreference("latestAnimes")) {
            if (credentials.existsPreviousLogin()) {

                ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/latest");
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
                        Log.d("DXGO", "Latest fragment animes ::: " + res.toString());

                        try {

                            if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                                final JSONArray array = res.getJSONArray("data");
                                fillGrid(array);
                                credentials.savePreference("latestAnimes", array.toString());

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
            this.fillGrid(JsonBuilder.stringToJsonArray(credentials.getPreference("latestAnimes")));
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