package com.appbuilders.animedia.Views;

import android.content.Context;

import com.appbuilders.animedia.R;
import com.appbuilders.surface.SurfaceActivityView;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 29/10/17
 */

public class MenuView extends SurfaceActivityView {

    public MenuView(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {


        this.screenCanvas.setBackgroundResource(R.color.blackTrans);

    }
}
