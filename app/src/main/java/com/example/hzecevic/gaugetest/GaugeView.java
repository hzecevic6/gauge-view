package com.example.hzecevic.gaugetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {

    private static final String TAG = GaugeView.class.getSimpleName();

    private Context context;

    private Paint arcPaint;

    private Paint needlePaint;

    private Paint thresholdsPaint;

    private Paint arcGapPaint;

    private Path path;

    private RectF arcBounds;

    private float arcStrokeSize;

    // We get this from feed.
    private double minValue = 0;
    private double maxValue = 100;
    private String currentValue = "20.0";

    // We get this from gauge.
    private String[] values;
    private String[] colors;

    private float startAngle;

    public GaugeView(Context context) {
        super(context);
        this.context = context;

        initialize();
    }

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initialize();
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initialize();
    }

    private void initialize() {
        arcStrokeSize = dpToPx(context, 35f);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(arcStrokeSize);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        needlePaint.setStrokeWidth(dpToPx(context, 1f));
        needlePaint.setStrokeCap(Paint.Cap.ROUND);

        thresholdsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thresholdsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        thresholdsPaint.setStrokeWidth(1f);
        thresholdsPaint.setColor(Color.rgb(158, 158, 158));
        thresholdsPaint.setTextSize(dpToPx(context, 12f));

        arcGapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcGapPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        arcGapPaint.setColor(Color.WHITE);
        arcGapPaint.setStrokeWidth(dpToPx(context, 3));

        path = new Path();
        arcBounds = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (colors.length == 0 || values.length == 0) {
            return;
        }

        startAngle = 180f;

        final float width = canvas.getWidth();
        final float height = canvas.getHeight();

        final float arcCenterX = width / 2;
        final float arcCenterY = height / 2;

        final float size = Math.min(width, height);

        arcBounds.set(arcCenterX - size / 2, arcCenterY - size / 8, arcCenterX + size / 2, arcCenterY + 7 * size / 8);

        final float y = arcCenterY + 3 * size / 8;
        int i = 0;

        drawExtremeValues(canvas, arcCenterX, arcCenterY, size, String.valueOf(minValue), false);
        drawArcPart(canvas, minValue, Float.parseFloat(values[0]), colors[0]);
        for (; i < values.length - 1; i++) {
            drawText(canvas, values[i], arcCenterX, y, size);
            drawArcPart(canvas, Float.parseFloat(values[i]), Float.parseFloat(values[i + 1]), colors[i + 1]);
        }

        drawText(canvas, values[i], arcCenterX, y, size);
        drawArcPart(canvas, Float.parseFloat(values[i]), maxValue, colors[i + 1]);
        drawExtremeValues(canvas, arcCenterX, arcCenterY, size, String.valueOf(maxValue), true);

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
        canvas.drawArc(arcBounds, startAngle, sweepAngle + 0.5f, false, arcPaint);

        startAngle += sweepAngle;
    }

    private void drawText(Canvas canvas, String value, double oldX, double oldY, double size) {
        double offset = 0;
        if (startAngle < 270f) {
            final Rect rect = new Rect();
            thresholdsPaint.getTextBounds(value, 0, value.length(), rect);
            offset = rect.height();
        }

        final double distance = size / 2 + 2 * arcStrokeSize / 3 + offset * 180f / startAngle;

        double a = oldX + distance * Math.cos(Math.toRadians(startAngle));
        double b = oldY + distance * Math.sin(Math.toRadians(startAngle));

        canvas.drawLine((float) oldX, (float) oldY, (float) a, (float) b, arcGapPaint);

        a = oldX + distance * Math.cos(Math.toRadians(startAngle));
        b = oldY + distance * Math.sin(Math.toRadians(startAngle));
        canvas.drawText(value, (float) a, (float) b, thresholdsPaint);
    }

    private void drawNeedle(Canvas canvas, float centerX, float centerY, float needleY, float size) {
        canvas.rotate(180f * Float.parseFloat(currentValue) / (float) (maxValue - minValue), centerX, centerY + 3 * size / 8);
        path.reset();
        path.moveTo(centerX, needleY);
        path.lineTo(centerX, needleY - dpToPx(getContext(), 5));
        path.lineTo(centerX - size / 2, needleY - dpToPx(getContext(), 1));
        path.lineTo(centerX - size / 2, needleY + dpToPx(getContext(), 1));
        path.close();
        canvas.drawPath(path, needlePaint);

        canvas.drawCircle(centerX, needleY - dpToPx(getContext(), 2.5f), dpToPx(getContext(), 2.5f), needlePaint);
        canvas.drawCircle(centerX - size / 2, needleY, dpToPx(getContext(), 1f), needlePaint);
    }

    private void drawExtremeValues(Canvas canvas, float centerX, float centerY, float size, String value, boolean max) {
        final Rect rect = new Rect();
        thresholdsPaint.getTextBounds(value, 0, value.length(), rect);

        float a = rect.width();
        float b = rect.height();

        if (max) {
            canvas.drawText(value, centerX + size / 2 - a / 2, centerY + 3 * size / 8 + 2 * b, thresholdsPaint);
        } else {
            canvas.drawText(value, centerX - size / 2 - a / 2, centerY + 3 * size / 8 + 2 * b, thresholdsPaint);
        }
    }

    public static float dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

}
