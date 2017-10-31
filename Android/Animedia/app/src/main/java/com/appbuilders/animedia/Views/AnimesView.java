package com.appbuilders.animedia.Views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.AlphabetAdapter;
import com.appbuilders.animedia.Adapter.AnimeAdapter;
import com.appbuilders.animedia.Adapter.LastAnimeAdapter;
import com.appbuilders.animedia.Controls.SpacesItemDecoration;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Implement.AlphabetListImp;
import com.appbuilders.animedia.Implement.LastAnimesListImp;
import com.appbuilders.animedia.Libraries.JsonBuilder;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.Listener.OnScrollListViewMiddle;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 30/10/17
 */

public class AnimesView extends SurfaceActivityView {

    Credentials credentials;

    SfPanel tabsMenuPanel;
        SfPanel latestPanel;
        SfPanel AZPanel;
        SfPanel genresPanel;
    SfPanel contentPanel;
    SfPanel slideLettersPanel;

    RecyclerView masonry;

    ListView alphabetList;

    String[] alphabet = this.context.getResources().getStringArray(R.array.alphabet);
    View prevView;
    AnimeAdapter adapter;

    public AnimesView(Context context) {
        super(context);
    }

    public AnimesView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public AnimesView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Setting background
        this.screenCanvas.setBackgroundResource(R.color.animesBackground);

        // Ask for latest animes
        this.askForLatestAnimes();

        // Setting panels
        this.tabsMenuPanel = new SfPanel();
        this.contentPanel = new SfPanel();
        this.slideLettersPanel = new SfPanel();
        this.screen.append(tabsMenuPanel).append(contentPanel).append(slideLettersPanel);

        // Setting views
        this.createTabs();
        this.createMasonryLayout();

        // Screen update
        this.screen.update(this.context);
    }

    private void createTabs() {

        this.latestPanel = new SfPanel();
        Button latestView = new Button(this.context);
        latestView.setText("Latest");
        latestView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        latestView.setBackgroundResource(R.color.yellowItemSelected);
        latestView.setTextColor(Color.WHITE);
        latestPanel.setView(latestView).setSize(-33.33333f, -100);
        this.addView(latestView);

        this.AZPanel = new SfPanel();
        Button AZView = new Button(this.context);
        AZView.setText("A - Z");
        AZView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        AZView.setBackgroundResource(R.color.blackTrans);
        AZView.setTextColor(Color.WHITE);
        AZPanel.setView(AZView).setSize(-33.33333f, -100);
        this.addView(AZView);
        AZView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForAscAnimes();
            }
        });

        this.genresPanel = new SfPanel();
        Button genresView = new Button(this.context);
        genresView.setText("Genres");
        genresView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        genresView.setBackgroundResource(R.color.blackTrans);
        genresView.setTextColor(Color.WHITE);
        genresPanel.setView(genresView).setSize(-33.33333f, -100);
        this.addView(genresView);

        this.tabsMenuPanel.append(latestPanel).append(AZPanel).append(genresPanel);
        this.tabsMenuPanel.setSize(-100, -8);
    }

    private void createMasonryLayout() {

        String animesString = this.activity.getIntent().getStringExtra("animes");
        JSONArray animes = JsonBuilder.stringToJsonArray(animesString);

        this.masonry = new RecyclerView(this.context);
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(16));

        this.contentPanel.setView(this.masonry).setSize(-100, -92);
        this.slideLettersPanel.setSize(0, 0);
        this.addView(this.masonry);
    }

    protected void askForLatestAnimes() {

        this.credentials = Credentials.getInstance(this.context);

        if (credentials.existsPreviousLogin()) {

            ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/latest");
            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
            request.addParameter("token", credentials.getToken());
            request.addField("user_id", credentials.getUserId());
            request.addField("bearer", credentials.getBearer());
            rest.execute(request, new ReSTCallback() {

                @Override
                public void onSuccess(ReSTResponse response) {

                    Log.d("AB_DEV", "RESPUESTA = " + response.body);

                    JSONObject res = JsonFileManager.stringToJSON(response.body);

                    try {

                        if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                            JSONArray animes = res.getJSONArray("data");
                            adapter = new AnimeAdapter(context, animes);
                            masonry.setAdapter(adapter);

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

    protected void askForAscAnimes() {

        //this.masonry.setAdapter(null);
        this.contentPanel.setSize(-90, -92);
        this.slideLettersPanel.setSize(-10, -92);

        this.alphabetList = new ListView(this.context);
        alphabetList.setBackgroundResource(R.color.blackTrans);
        alphabetList.setAdapter(null);
        alphabetList.setAdapter(new AlphabetAdapter(this.context, this.alphabet));
        alphabetList.setOnScrollListener(new AlphabetListImp(new OnScrollListViewMiddle() {
            @Override
            public void onMiddle(int position) {
                //Log.d("AB_DEV", "Position :::: " + position + " ::::::: " + alphabet[position]);
            }

            @Override
            public void onScrollMove(int position) {
                setOnScrollSelectedItem(position);
            }
        }));

        // Asking to the server the animes
        if (credentials != null && credentials.existsPreviousLogin()) {

            ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/orderAsc");
            ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
            request.addParameter("token", credentials.getToken());
            request.addField("user_id", credentials.getUserId());
            request.addField("bearer", credentials.getBearer());
            rest.execute(request, new ReSTCallback() {

                @Override
                public void onSuccess(ReSTResponse response) {

                    Log.d("AB_DEV", "RESPUESTA = " + response.body);

                    JSONObject res = JsonFileManager.stringToJSON(response.body);

                    try {

                        if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                            JSONArray animes = res.getJSONArray("data");
                            adapter.clear();
                            adapter.addNews(animes);

                        } else {
                            //showErrorAlert("Error", "Problemas de conexión");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ReSTResponse response) {
                    Toast.makeText(context, "Try again!!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        this.slideLettersPanel.setView(alphabetList);
        this.addView(alphabetList);

        this.screen.update(this.context);
    }

    protected void fillGrid(final JSONArray animes) {

        this.masonry.setAdapter(null);
        this.masonry.setAdapter(new AnimeAdapter(context, animes));
    }

    private void setOnScrollSelectedItem(int position) {

        if (this.prevView != null) {

            this.prevView.setBackgroundResource(R.color.trans);
            this.prevView.setPadding(0, 0, 0, 0);
        }

        View view = getViewByPosition(position, this.alphabetList);
        view.setBackgroundColor(Color.rgb(237, 178, 0));
        //view.setPadding(0, 0, 0, 0);
        this.prevView = view;
    }

    public View getViewByPosition(int pos, ListView listView) {

        pos = pos + 1;

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