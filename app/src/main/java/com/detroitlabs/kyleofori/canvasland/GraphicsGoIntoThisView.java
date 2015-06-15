package com.detroitlabs.kyleofori.canvasland;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kyleofori on 6/9/15.
 */
public class GraphicsGoIntoThisView extends View {

    Context context;
    private Bitmap bitmap; //drawing area for display
    private Path path; //used to draw lines onto bitmap
    private List<Path> paths = new ArrayList<>();
    private Paint paint;
    private Map<Path, Integer> colorsMap = new HashMap<>();
    private int defaultColor = Color.GREEN;
    private int selectedColor = defaultColor;
    private float xX, yY;
    private static final float TOLERANCE = 5;


    public GraphicsGoIntoThisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        // we set a new Path
        path = new Path();

        // and we set a new Paint with the desired attributes
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(defaultColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeWidth(8f);

        // Then, we match this path and the color together in a map.
        colorsMap.put(path, defaultColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        The following for loop takes care of all the old (previously-drawn) lines.
        for(Path p: paths) {
//            The following line alone is responsible for keeping the colors of all previously-drawn lines the same.
//            I think in my first attempt at this, I had one path consisting of multiple lines on the canvas.
//            Then, when I started using multiple paths but hadn't added the line below, the issue was the same:
//            individual paths were not matched to the colors they were originally drawn in,
//            so they were getting redrawn every time in the new color.
            paint.setColor(colorsMap.get(p));
            canvas.drawPath(p, paint);
        }
        paint.setColor(selectedColor);
        // draw the path with the paint on the canvas when onDraw
        canvas.drawPath(path, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

//        Depending on how you touch, the following code will be executed.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        path.moveTo(x, y);
        xX = x;
        yY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - xX);
        float dy = Math.abs(y - yY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            path.quadTo(xX, yY, (x + xX) / 2, (y + yY) / 2);
            xX = x;
            yY = y;
        }
    }

    public void clearCanvas() {
//        path.reset() was needed when I only had one path, but now I am working with a list that I can just clear.
        paths.clear();
        invalidate();
    }

    public void changeColor(int color) {
//        path = new Path();
        paint.setColor(color);
        selectedColor = color;
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        path.lineTo(xX, yY);
//        Adds this path to the list of paths.
        paths.add(path);
//        Associates the color used with the path just drawn
        colorsMap.put(path, selectedColor);
//        Makes a whole new path for the next one. This was not in the very first iteration.
        path = new Path();
    }

}