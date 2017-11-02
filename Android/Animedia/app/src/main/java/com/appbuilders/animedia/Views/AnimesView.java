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
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.AlphabetAdapter;
import com.appbuilders.animedia.Adapter.AnimeAdapter;
import com.appbuilders.animedia.Controls.SpacesItemDecoration;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Implement.AlphabetListImp;
import com.appbuilders.animedia.Libraries.JsonFileManager;
import com.appbuilders.animedia.Libraries.Rester.ReSTCallback;
import com.appbuilders.animedia.Libraries.Rester.ReSTClient;
import com.appbuilders.animedia.Libraries.Rester.ReSTRequest;
import com.appbuilders.animedia.Libraries.Rester.ReSTResponse;
import com.appbuilders.animedia.Listener.OnScrollListViewMiddle;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        SfPanel searchPanel;
    SfPanel contentPanel;
    SfPanel slideLettersPanel;
    SfPanel adPanel;

    RecyclerView masonry;
    ListView alphabetList;
    AdView mAdView;

    String[] alphabet = this.context.getResources().getStringArray(R.array.alphabet);
    View prevView;
    AnimeAdapter adapter;
    ArrayList<SfPanel> tabsPanelArray;
    int currentTab = 0;

    // Variables to save previous request


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
        this.adPanel = new SfPanel();
        this.screen.append(tabsMenuPanel).append(contentPanel).append(slideLettersPanel).append(this.adPanel);

        // Setting views
        this.createTabs();
        this.createMasonryLayout();
        this.createAd();

        // Screen update
        this.screen.update(this.context);
    }

    private void createTabs() {

        this.tabsPanelArray = new ArrayList<>();

        this.latestPanel = new SfPanel();
        Button latestView = new Button(this.context);
        latestView.setText("Ulltimos");
        latestView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        latestView.setBackgroundResource(R.color.yellowItemSelected);
        latestView.setTextColor(Color.WHITE);
        latestPanel.setView(latestView).setSize(-25, -100);
        this.addView(latestView);
        this.tabsPanelArray.add(this.latestPanel);
        latestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTab != 0) {
                    setCurrentTab(0);
                }
            }
        });

        this.AZPanel = new SfPanel();
        Button AZView = new Button(this.context);
        AZView.setText("A - Z");
        AZView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        AZView.setBackgroundResource(R.color.blackTrans);
        AZView.setTextColor(Color.WHITE);
        AZPanel.setView(AZView).setSize(-25, -100);
        this.addView(AZView);
        this.tabsPanelArray.add(this.AZPanel);
        AZView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTab != 1) {
                    setCurrentTab(1);
                    askForAscAnimes();
                }
            }
        });

        this.genresPanel = new SfPanel();
        Button genresView = new Button(this.context);
        genresView.setText("Generos");
        genresView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        genresView.setBackgroundResource(R.color.blackTrans);
        genresView.setTextColor(Color.WHITE);
        genresPanel.setView(genresView).setSize(-25, -100);
        this.tabsPanelArray.add(this.genresPanel);
        this.addView(genresView);
        genresView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTab != 2) {
                    setCurrentTab(2);
                }
            }
        });


        this.searchPanel = new SfPanel();
        Button searchView = new Button(this.context);
        searchView.setText("Buscar");
        searchView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        searchView.setBackgroundResource(R.color.blackTrans);
        searchView.setTextColor(Color.WHITE);
        this.searchPanel.setView(searchView).setSize(-25, -100);
        this.addView(searchView);
        this.tabsPanelArray.add(this.searchPanel);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTab != 3) {
                    setCurrentTab(3);
                }
            }
        });

        this.tabsMenuPanel.append(latestPanel).append(AZPanel).append(genresPanel).append(searchPanel);
        this.tabsMenuPanel.setSize(-100, -8);
    }

    private void createMasonryLayout() {

        this.masonry = new RecyclerView(this.context);
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(16));

        this.contentPanel.setView(this.masonry).setSize(-100, -82);
        this.slideLettersPanel.setSize(0, 0);
        this.addView(this.masonry);
    }

    private void createAd() {

        this.mAdView = new AdView(this.context);
        this.mAdView.setAdUnitId("ca-app-pub-8714411824921031/8988263733");
        // Add logic for banner sizes
        this.mAdView.setAdSize(AdSize.BANNER);
        this.adPanel.setView(this.mAdView).setSize(-100, -10);
        this.addView(this.mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
        this.contentPanel.setSize(-90, -82);
        this.slideLettersPanel.setSize(-10, -82);

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

    // Methods for tabs
    protected void setCurrentTab(int position) {

        for(int i = 0; i < this.tabsPanelArray.size(); i++) {

            SfPanel tabPanel = this.tabsPanelArray.get(i);
            tabPanel.getView().setBackgroundResource(R.color.blackTrans);
            if (i == position) {
                tabPanel.getView().setBackgroundResource(R.color.yellowItemSelected);
            }
        }
        this.currentTab = position;
    }

}