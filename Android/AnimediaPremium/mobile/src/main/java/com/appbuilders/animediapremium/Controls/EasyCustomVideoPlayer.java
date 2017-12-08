package com.appbuilders.animediapremium.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.appbuilders.animediapremium.Listener.EasyCustomVideoPlayerClickListener;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 05/12/17
 */

public class EasyCustomVideoPlayer extends EasyClickableVideoPlayer {

    private EasyCustomVideoPlayerClickListener listener;

    public EasyCustomVideoPlayer(Context context) {
        super(context);
    }

    public EasyCustomVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyCustomVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EasyCustomVideoPlayerClickListener getOnClickListener() {
        return listener;
    }

    public void setOnClickListener(EasyCustomVideoPlayerClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {

        super.onClick(view);
        if (listener != null) {
            listener.onClick();
        }
    }
}
