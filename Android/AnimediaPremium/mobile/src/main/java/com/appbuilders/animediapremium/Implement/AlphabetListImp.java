package com.appbuilders.animediapremium.Implement;

import android.widget.AbsListView;

import com.appbuilders.animediapremium.Listener.OnScrollListViewMiddle;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 26/10/17
 */

public class AlphabetListImp implements AbsListView.OnScrollListener {

    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentScrollState;
    private int fixAdapter = 2;
    private int currentPositionMiddle = 0;
    private OnScrollListViewMiddle listener = null;

    public AlphabetListImp(OnScrollListViewMiddle listener) {

        this.listener = listener;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;

        if (this.listener != null) {
            this.listener.onScrollMove(firstVisibleItem);
        }

    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void isScrollCompleted() {

        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {

            if (this.listener != null) {
                this.listener.onMiddle(this.currentFirstVisibleItem);
            }
        }
    }
}