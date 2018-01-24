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
    private String currentValue = "45.0";

    // We get this from gauge.
    private String[] values = {"55", "70", "80"};
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

        final float y = arcCenterY + 3 * size / 8;
        int i = 0;

        drawExtremeValues(canvas, arcCenterX, y, 172f, size, String.valueOf(minValue));
        drawArcPart(canvas, minValue, Float.parseFloat(values[0]), colors[0]);
        for (; i < values.length - 1; i++) {
            drawText(canvas, values[i], arcCenterX, y, size);
            drawArcPart(canvas, Float.parseFloat(values[i]), Float.parseFloat(values[i + 1]), colors[i + 1]);
        }

        drawText(canvas, values[i], arcCenterX, y, size);
        drawArcPart(canvas, Float.parseFloat(values[i]), maxValue, colors[i + 1]);
        drawExtremeValues(canvas, arcCenterX, y, 368f, size, String.valueOf(maxValue));

        drawNeedle(canvas, arcCenterX, arcCenterY, y, size);
    }

    public void showGauge(String[] values, String[] colors) {
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

    private void drawText(Canvas canvas, String value, double oldX, double oldY, double size) {
        final double distance = size / 2 + 2 * arcStrokeSize / 3;

        final double a = oldX + distance * Math.cos(Math.toRadians(startAngle));
        final double b = oldY + distance * Math.sin(Math.toRadians(startAngle));

        canvas.drawText(value, (float) a, (float) b, thresholdsPaint);
    }

    private void drawNeedle(Canvas canvas, float centerX, float centerY, float needleY, float size) {
        canvas.rotate(180f * Float.parseFloat(currentValue) / (float) (maxValue - minValue), centerX, centerY + 3 * size / 8);
        path.reset();
        path.moveTo(centerX, needleY);
        path.lineTo(centerX, needleY - 20);
        path.lineTo(centerX - size / 2, needleY - 1);
        path.lineTo(centerX - size / 2, needleY + 1);
        path.close();
        canvas.drawPath(path, needlePaint);

        canvas.drawCircle(centerX + 2, needleY - 10, 10f, needlePaint);
        canvas.drawCircle(centerX - size / 2, needleY, 1f, needlePaint);
    }

    private void drawExtremeValues(Canvas canvas, float centerX, float centerY, float angle, float size, String value) {
        final double distance =  size / 2 + arcStrokeSize / 3;

        final double a = centerX + distance * Math.cos(Math.toRadians(angle));
        final double b = centerY + distance * Math.sin(Math.toRadians(angle));

        canvas.drawText(value, (float) a, (float) b, thresholdsPaint);
    }
}
