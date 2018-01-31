package com.appbuilders.animedia.Core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 30/01/18
 */

public class Util {

    public static Bitmap getImage(Context context, String name) {

        //this.screenCanvas.setBackground(new BitmapDrawable(image));
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Bitmap image = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
        return image;
    }
}
