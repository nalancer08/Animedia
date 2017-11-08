package com.appbuilders.animedia.Views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.appbuilders.animedia.Adapter.AlphabetAdapter;
import com.appbuilders.animedia.Adapter.AnimeAdapter;
import com.appbuilders.animedia.Controls.SpacesItemDecoration;
import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Implement.AlphabetListImp;
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
 * Revision 1 - 02/11/17
 */

public class AscAnimesView extends SurfaceActivityView {

    Credentials credentials;

    RecyclerView masonry;
    AnimeAdapter adapter;

    SfPanel contentPanel;

    SfPanel slideLettersPanel;
    ListView alphabetList;
    String[] alphabet;
    View prevView;

    public AscAnimesView(Context context) {
        super(context);
    }

    public AscAnimesView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public AscAnimesView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Initializing alphabet array
        this.alphabet = this.context.getResources().getStringArray(R.array.alphabet);

        // Initializing panels
        this.contentPanel = new SfPanel();
        this.slideLettersPanel = new SfPanel();

        // Appends
        this.subScreen.append(this.contentPanel).append(this.slideLettersPanel);

        // Creating masonry layout
        this.createMasonryLayout();

        // Creating alphabet list
        this.createAlphabetList();

        // Asking for projects
        this.askForAscAnimes();

        this.screen.update(this.context);
    }

    private void createMasonryLayout() {

        this.masonry = new RecyclerView(this.context);
        this.masonry.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.masonry.setAdapter(null);
        this.masonry.addItemDecoration(new SpacesItemDecoration(16));

        this.contentPanel.setView(this.masonry).setSize(-90, -82);
        this.addView(this.masonry);
    }

    private void createAlphabetList() {

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
        this.slideLettersPanel.setSize(-10, -100);
        this.slideLettersPanel.setView(alphabetList);
        this.addView(alphabetList);
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

    protected void askForAscAnimes() {

        this.credentials = Credentials.getInstance(this.context);

        if (!credentials.existsPreference("ascAnimes")) {
            if (credentials.existsPreviousLogin()) {

                ReSTClient rest = new ReSTClient(credentials.getUrl() + "/animes/orderAsc");
                ReSTRequest request = new ReSTRequest(ReSTRequest.REST_REQUEST_METHOD_POST, "");
                request.addParameter("token", credentials.getToken());
                request.addField("user_id", credentials.getUserId());
                request.addField("bearer", credentials.getBearer());
                rest.execute(request, new ReSTCallback() {

                    @Override
                    public void onSuccess(ReSTResponse response) {

                        JSONObject res = JsonFileManager.stringToJSON(response.body);
                        Log.d("DXGO", "ASC fragment animes ::: " + res.toString());

                        try {

                            if (res.getString("result").equals("success") && res.getInt("code") == 200) {

                                final JSONArray array = res.getJSONArray("data");
                                fillGrid(array);
                                credentials.savePreference("ascAnimes", array.toString());

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
            this.fillGrid(JsonBuilder.stringToJsonArray(credentials.getPreference("ascAnimes")));
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