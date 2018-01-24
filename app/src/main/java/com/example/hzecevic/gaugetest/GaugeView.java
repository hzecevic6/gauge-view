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

    // We get this from feed.
    private double minValue = 0;
    private double maxValue = 100;

    // We get this from gauge.
    private float[] values = {55f, 70f, 80f};
    private String[] colors = {"#009900", "#FFFF00", "#FFA500", "#FF0000"};

    private float startAngle = 180f;

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

        // Draw the arc.
        arcBounds.set(arcCenterX - size / 2, arcCenterY - size / 8, arcCenterX + size / 2, arcCenterY + 7 * size / 8);

        int i = 0;
        drawArcPart(canvas, minValue, values[0], colors[0]);
        for (; i < values.length - 1; i++) {
            drawArcPart(canvas, values[i], values[i + 1], colors[i+1]);
        }
        drawArcPart(canvas, values[i], maxValue, colors[i+1]);

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

    public void showGauge(float[] values, String[] colors) {
        this.values = values;
        this.colors = colors;

        invalidate();
    }

    private void drawArcPart(Canvas canvas, double left, double right, String color) {
        arcPaint.setColor(Color.parseColor(color));
        final float sweepAngle = (float) (right - left) / (float) (maxValue - minValue) * 180f;
        canvas.drawArc(arcBounds, startAngle, sweepAngle - 0.5f, false, arcPaint);

        startAngle += sweepAngle + 0.5f;
    }

    private void drawText(Canvas canvas) {
        
    }
}
