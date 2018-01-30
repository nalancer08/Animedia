package com.appbuilders.animedia.Controls;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Random;

/**
 * Created by Suleiman on 26-07-2015.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;
    private int min = 1;
    private int max = 3;

    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace * ( new Random().nextInt(max - min + 1) + min );

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1)
            outRect.top = mSpace;
    }
}