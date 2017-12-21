package com.appbuilders.animedia.Core;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbuilders.animedia.R;
import com.like.LikeButton;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 30/10/17
 */

public class AnimeView extends RecyclerView.ViewHolder {


    public ImageView cover;
    public TextView name;

    public LikeButton like;

    public AnimeView(View itemView) {

        super(itemView);
        this.cover = itemView.findViewById(R.id.img);
        this.name = itemView.findViewById(R.id.img_name);
        this.like = itemView.findViewById(R.id.like);
    }

    public ImageView getCover() {
        return cover;
    }

    public TextView getName() {
        return name;
    }

    public LikeButton getLike() {
        return like;
    }
}