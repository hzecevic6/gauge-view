package com.example.hzecevic.gaugetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {

    private static final String TAG = GaugeView.class.getSimpleName();

    private Paint arcPaint;

    private Paint needlePaint;

    private Paint thresholdsPaint;

    private Path path;

    private RectF arcBounds;

    private float arcStrokeSize = 75f;

    private float minValue = 0;
    private float maxValue = 100;

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
        arcPaint.setStrokeWidth(arcStrokeSize);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        needlePaint.setStrokeWidth(4f);
        needlePaint.setStrokeCap(Paint.Cap.ROUND);

        thresholdsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thresholdsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        thresholdsPaint.setStrokeWidth(1f);
        thresholdsPaint.setColor(Color.rgb(158, 158, 158));
        thresholdsPaint.setTextSize(25f);

        path = new Path();
        arcBounds = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float width = canvas.getWidth();
        final float height = canvas.getHeight();

        final float arcCenterX = width / 2;
        final float arcCenterY = height / 2;

        final float size = Math.min(width, height);

        arcBounds.set(arcCenterX - size / 2, arcCenterY - size / 8, arcCenterX + size / 2, arcCenterY + 7 * size / 8);

        // Draw the arc.
        arcPaint.setColor(Color.rgb(50, 205, 50));
        canvas.drawArc(arcBounds, 180f, 99f, false, arcPaint);
        arcPaint.setColor(Color.YELLOW);
        canvas.drawArc(arcBounds, 280f, 27f, false, arcPaint);
        arcPaint.setColor(Color.rgb(255, 165, 0));
        canvas.drawArc(arcBounds, 308f, 18f, false, arcPaint);
        arcPaint.setColor(Color.RED);
        canvas.drawArc(arcBounds, 327f, 33f, false, arcPaint);

        final double distance = size / 2 + 2 * arcStrokeSize / 3;
        final float y = arcCenterY + 3 * size / 8;
        final double a = arcCenterX + distance * Math.cos(Math.toRadians(308));
        final double b = y + distance * Math.sin(Math.toRadians(308));

        canvas.drawText("55", (float) a, (float) b, thresholdsPaint);

        // Draw the needle.
        canvas.rotate(180f / 2, arcCenterX, arcCenterY + 3 * size / 8);
        path.moveTo(arcCenterX, y);
        path.lineTo(arcCenterX, y - 20);
        path.lineTo(arcCenterX - size / 2, y - 1);
        path.lineTo(arcCenterX - size / 2, y + 1);
        path.close();
        canvas.drawPath(path, needlePaint);

        canvas.drawCircle(arcCenterX + 2, y - 10, 10f, needlePaint);
        canvas.drawCircle(arcCenterX - size / 2, y, 1f, needlePaint);
    }

    public void showGauge() {
        
    }
}
