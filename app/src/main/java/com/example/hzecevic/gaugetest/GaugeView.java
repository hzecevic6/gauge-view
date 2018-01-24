package com.example.hzecevic.gaugetest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {

    private static final String TAG = GaugeView.class.getSimpleName();

    private Paint arcPaint;

    public GaugeView(Context context) {
        super(context);
        initialize();
    }

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(75f);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
//        Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
//
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int chosenDimension = Math.min(widthSize, heightSize);
//
//        setMeasuredDimension(chosenDimension, chosenDimension);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int arcCenterX = width / 2;
        int arcCenterY = height / 2;

        int dimen = Math.min(width, height);

        @SuppressLint("DrawAllocation") final RectF arcBounds = new RectF(arcCenterX - dimen / 2, arcCenterY - dimen / 8, arcCenterX + dimen / 2, arcCenterY + 7 * dimen / 8);

        // Draw the arc
        arcPaint.setColor(Color.GREEN);
        canvas.drawArc(arcBounds, 180f, 99f, false, arcPaint);
        arcPaint.setColor(Color.YELLOW);
        canvas.drawArc(arcBounds, 280f, 27f, false, arcPaint);
        arcPaint.setColor(Color.rgb(255, 165, 0));
        canvas.drawArc(arcBounds, 308f, 18f, false, arcPaint);
        arcPaint.setColor(Color.RED);
        canvas.drawArc(arcBounds, 327f, 33f, false, arcPaint);

        // Draw the pointers
        final int totalNoOfPointers = 40;
        final int pointerMaxHeight = 25;
        final int pointerMinHeight = 15;

        int startX = 20;
        int startY = arcCenterY;
        arcPaint.setStrokeWidth(5f);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawLine(arcCenterX, arcCenterY +dimen/2 -dimen/8, startX, startY, arcPaint);

//        int pointerHeight;
//        for (int i = 0; i <= totalNoOfPointers; i++) {
//            if (i % 5 == 0) {
//                pointerHeight = pointerMaxHeight;
//            } else {
//                pointerHeight = pointerMinHeight;
//            }
//            canvas.drawLine(startX, startY, startX - pointerHeight, startY, arcPaint);
//            canvas.rotate(90f / totalNoOfPointers, arcCenterX, arcCenterY);
//        }
    }
}
