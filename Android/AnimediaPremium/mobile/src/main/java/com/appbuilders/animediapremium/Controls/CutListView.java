package com.appbuilders.animediapremium.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import com.appbuilders.animediapremium.R;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 29/10/17
 */

public class CutListView extends ListView {

    public CutListView(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        Path path = new Path();

        /*path.moveTo(width / 15 + width / 10 + width / 10, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(width / 15, height);*/

        path.moveTo(50, height);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(width, height);

        path.close();
        canvas.save();
        canvas.clipPath(path, Region.Op.INTERSECT);
        Paint paint = new Paint();
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        canvas.drawPath(path, paint);

        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(ContextCompat.getColor(getContext(), R.color.blackTrans));
        canvas.drawPath(path, fill);

        canvas.restore();
        super.dispatchDraw(canvas);
    }
}
