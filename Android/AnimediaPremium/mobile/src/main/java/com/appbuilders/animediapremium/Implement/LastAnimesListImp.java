package com.appbuilders.animediapremium.Implement;

import android.widget.AbsListView;

import com.appbuilders.animediapremium.Listener.OnScrollListViewMiddle;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class LastAnimesListImp implements AbsListView.OnScrollListener {

    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentScrollState;
    private int fixAdapter = 2;
    private int currentPositionMiddle = 0;
    private OnScrollListViewMiddle listener = null;

    public LastAnimesListImp(OnScrollListViewMiddle listener) {

        this.listener = listener;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;

        int middle = (this.currentVisibleItemCount % 2 == 0) ? (this.currentVisibleItemCount / 2) : (int) Math.floor(this.currentVisibleItemCount / 2);
        int middlePosition = (this.currentFirstVisibleItem + middle) - 2;

        if (this.listener != null) {
            if (middlePosition >= 0 && this.currentPositionMiddle != middlePosition) {
                this.listener.onScrollMove(middlePosition);
            }
        }

    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void isScrollCompleted() {

        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {

            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work! ***/

            int middle = (this.currentVisibleItemCount % 2 == 0) ? (this.currentVisibleItemCount / 2) : (int) Math.floor(this.currentVisibleItemCount / 2);
            int middlePosiition = (this.currentFirstVisibleItem + middle) - 2;
            //int end = (this.currentFirstVisibleItem + this.currentVisibleItemCount) - 1;

            //Log.d("DXGO", "first :::::::::: " + this.currentFirstVisibleItem + " ==== count ==== " + this.currentVisibleItemCount + " :=:=: middle# :=:=: " + middle);

            if (this.listener != null) {
                if (middlePosiition >= 0 && this.currentPositionMiddle != middlePosiition) {
                    this.currentPositionMiddle = middlePosiition;
                    this.listener.onMiddle(middlePosiition);
                } else {

                    if (middlePosiition < 0) {
                        this.currentPositionMiddle = 0;
                        this.listener.onMiddle(0);
                    }
                }
            }
        }
    }
}