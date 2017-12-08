package com.appbuilders.animediapremium.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;

import com.appbuilders.animediapremium.R;
import com.appbuilders.surface.SfPanel;
import com.appbuilders.surface.SurfaceActivityView;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 07/12/17
 */

public class PremiumVersionView extends SurfaceActivityView {

    private SfPanel headerPanel;
    private SfPanel bodyPanel;

    public PremiumVersionView(Context context) {
        super(context);
    }

    public PremiumVersionView(Context context, AbsoluteLayout baseLayout) {
        super(context, baseLayout);
    }

    public PremiumVersionView(Context context, boolean fullScreen) {
        super(context, fullScreen);
    }

    @Override
    public void onCreateView() {

        // Setting status bar color
        this.setStatusBarColor();

        // Setting header
        this.setHeader();

        this.screen.update(this.context);
    }

    private void setHeader() {

        View view = new View(this.context);
        view.setBackgroundResource(R.color.premiumHeader);

        this.headerPanel = new SfPanel();
        this.headerPanel.setSize(-100, -40);
        this.headerPanel.setView(view);
        this.screen.append(this.headerPanel);
        this.addView(view);
    }

    protected void setStatusBarColor() {

        Window window = this.activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.rgb(0, 197, 155));
        }
    }

    protected int threeRuleX(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return (width * value) / 1000;
    }

    protected int threeRuleY(int value) {

        Display display = ((Activity)this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heigth = size.y;

        return (heigth * value) / 1794;
    }

}
