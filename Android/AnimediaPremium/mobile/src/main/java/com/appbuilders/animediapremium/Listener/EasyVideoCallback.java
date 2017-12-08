package com.appbuilders.animediapremium.Listener;

import android.net.Uri;

import com.appbuilders.animediapremium.Controls.EasyClickableVideoPlayer;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 05/12/17
 */

public interface EasyVideoCallback {

    void onStarted(EasyClickableVideoPlayer player);

    void onPaused(EasyClickableVideoPlayer player);

    void onPreparing(EasyClickableVideoPlayer player);

    void onPrepared(EasyClickableVideoPlayer player);

    void onBuffering(int percent);

    void onError(EasyClickableVideoPlayer player, Exception e);

    void onCompletion(EasyClickableVideoPlayer player);

    void onRetry(EasyClickableVideoPlayer player, Uri source);

    void onSubmit(EasyClickableVideoPlayer player, Uri source);
}
