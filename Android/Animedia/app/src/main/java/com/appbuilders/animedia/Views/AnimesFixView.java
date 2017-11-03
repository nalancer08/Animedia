package com.appbuilders.animedia.Views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;

import com.appbuilders.animedia.Core.Credentials;
import com.appbuilders.animedia.Fragment.AscAnimes;
import com.appbuilders.animedia.Fragment.LatestAnimes;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 02/11/17
 */

public class AnimesFixView extends SurfaceActivityView {

    Credentials credentials;

    SfPanel tabsMenuPanel;
        SfPanel latestPanel;
        SfPanel AZPanel;
        SfPanel genresPanel;
        SfPanel searchPanel;
    SfPanel contentPanel;
    SfPanel adPanel;

    ArrayList<SfPanel> tabsPanelArray;
    int currentTab = 0;

    AdView mAdView;

    public AnimesFixView(Context context) {
        super(context);
    }

    public AnimesFixView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public AnimesFixView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Setting background
        this.screenCanvas.setBackgroundResource(R.color.animesBackground);

        // Setting panels
        this.tabsMenuPanel = new SfPanel();
        this.contentPanel = new SfPanel();
        this.adPanel = new SfPanel();
        this.screen.append(tabsMenuPanel).append(contentPanel).append(this.adPanel);

        // Creating content panel size
        this.contentPanel.setSize(-100, -82);

        // Setting views
        this.createTabs();
        this.createAd();

        // Asking for first tab
        this.askForLatestAnimes();

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
                    askForLatestAnimes();
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

        this.contentPanel.setFragment(new LatestAnimes());
        this.addFragment(this.contentPanel);
    }

    protected void askForAscAnimes() {

        this.contentPanel.setFragment(new AscAnimes(this.context));
        this.addFragment(this.contentPanel);
        this.screen.update(this.context);
    }
}
