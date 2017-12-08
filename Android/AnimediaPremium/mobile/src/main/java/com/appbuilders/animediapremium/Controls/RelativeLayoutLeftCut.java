package com.appbuilders.animediapremium.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.appbuilders.animediapremium.R;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 29/10/17
 */

public class RelativeLayoutLeftCut extends RelativeLayout {

    public Canvas canvas;
    public boolean fiilColor = false;

    public RelativeLayoutLeftCut(Context context) {
        super(context);
    }

    public RelativeLayoutLeftCut(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutLeftCut(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RelativeLayoutLeftCut(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setFillColor(boolean fill) {

        this.fiilColor = fill;
        this.invalidate();
        //this.dispatchDraw(this.canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        this.canvas = canvas;

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        Path path = new Path();
        path.moveTo(width / 4 + width / 10 + width / 10, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(width / 4, height);
        path.close();
        canvas.save();

        canvas.clipPath(path, Region.Op.INTERSECT);
        Paint paint = new Paint();
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setColor(ContextCompat.getColor(getContext(), android.R.color.black));
        canvas.drawPath(path, paint);

        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(ContextCompat.getColor(getContext(), R.color.trans));

        canvas.drawPath(path, fill);

        canvas.restore();
        super.dispatchDraw(canvas);
    }
}
