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

    private static final float ANGLE_CONST = 180f;

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
    private String currentValue = "23.0";

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

        startAngle = ANGLE_CONST;

        final float width = canvas.getWidth();
        final float height = canvas.getHeight();
        final float size = Math.min(width, height);

        final float centerX = width / 2;
        final float centerY = height / 2;

        // Move arc's bounding box so that only top half of arc and some empty space reserved for min/max values is on the canvas.
        arcBounds.set(centerX - size / 2, centerY - size / 8, centerX + size / 2, centerY + 7 * size / 8);

        // Position of center of the arc.
        final float y = centerY + 3 * size / 8;
        int i = 0;

        drawExtremeValues(canvas, centerX, centerY, size, String.valueOf(minValue), false);
        drawArcPart(canvas, minValue, Float.parseFloat(values[0]), colors[0]);

        for (; i < values.length - 1; i++) {
            drawText(canvas, values[i], centerX, y, size);
            drawArcPart(canvas, Float.parseFloat(values[i]), Float.parseFloat(values[i + 1]), colors[i + 1]);
        }

        drawText(canvas, values[i], centerX, y, size);
        drawArcPart(canvas, Float.parseFloat(values[i]), maxValue, colors[i + 1]);
        drawExtremeValues(canvas, centerX, centerY, size, String.valueOf(maxValue), true);

        drawNeedle(canvas, centerX, y, size);
    }

    public void showGauge(String[] values, String[] colors) {
        this.values = values;
        this.colors = colors;

        invalidate();
    }

    /**
     * Draws one part of the arc from the left value to the right value.
     *
     * @param canvas canvas
     * @param left   left, lesser value
     * @param right  right, greater value
     * @param color  color of this part of the arc
     */
    private void drawArcPart(Canvas canvas, double left, double right, String color) {
        arcPaint.setColor(Color.parseColor(color));

        final float sweepAngle = (float) (right - left) / (float) (maxValue - minValue) * ANGLE_CONST;
        // Add 0.5f to the sweep angle so that arcs connect.
        canvas.drawArc(arcBounds, startAngle, sweepAngle + 0.5f, false, arcPaint);

        startAngle += sweepAngle;
    }

    /**
     * Draw value of one part of the arc.
     *
     * @param canvas canvas
     * @param value  value of part of the arc
     * @param oldX   x axis of center of the whole arc
     * @param oldY   y axis of center of the whole arc
     * @param size   value used for distances
     */
    private void drawText(Canvas canvas, String value, double oldX, double oldY, double size) {
        double offset = 0;

        if (startAngle < 270f) {
            final Rect rect = new Rect();
            thresholdsPaint.getTextBounds(value, 0, value.length(), rect);
            offset = rect.height();
        }

        // Distance from center of the arc to the text.
        // We take size / 2 since that is the diameter of the arc.
        // Add 2 * arcStrokeSize / 3 so that text is positioned above the arc but not too far from it.
        // Offset * ANGLE_CONST / startAngle - lesser angles are impacted more, because text needs to be moved more from the arc.
        final double distance = size / 2 + 2 * arcStrokeSize / 3 + offset * ANGLE_CONST / startAngle;

        final double a = oldX + distance * Math.cos(Math.toRadians(startAngle));
        final double b = oldY + distance * Math.sin(Math.toRadians(startAngle));

        // Draw gap between two arcs, this gap is pointing to the text.
        canvas.drawLine((float) oldX, (float) oldY, (float) a, (float) b, arcGapPaint);

        canvas.drawText(value, (float) a, (float) b, thresholdsPaint);
    }

    /**
     * Draws the gauge needle.
     *
     * @param canvas  canvas
     * @param centerX x axis of center of the arc
     * @param needleY y axis of center of the arc
     * @param size    value used for distances
     */
    private void drawNeedle(Canvas canvas, float centerX, float needleY, float size) {
        // Rotate canvas to the position where needle should be placed.
        canvas.rotate(ANGLE_CONST * Float.parseFloat(currentValue) / (float) (maxValue - minValue), centerX, needleY);

        // Draw triangle.
        path.reset();
        path.moveTo(centerX, needleY);
        path.lineTo(centerX, needleY - dpToPx(context, 5));
        path.lineTo(centerX - size / 2, needleY - dpToPx(context, 1));
        path.lineTo(centerX - size / 2, needleY + dpToPx(context, 1));
        path.close();
        canvas.drawPath(path, needlePaint);

        // Draw large circle.
        canvas.drawCircle(centerX, needleY - dpToPx(context, 2.5f), dpToPx(context, 2.5f), needlePaint);

        // Draw small circle.
        canvas.drawCircle(centerX - size / 2, needleY, dpToPx(context, 1f), needlePaint);
    }

    /**
     * Draw text displaying max/min value of Feed.
     *
     * @param canvas  canvas
     * @param centerX x axis of center of canvas
     * @param centerY y axis of center of canvas
     * @param size    value used for distances
     * @param value   min/max value of Feed
     * @param max     true if value to be drawn is maximum Feed value
     */
    private void drawExtremeValues(Canvas canvas, float centerX, float centerY, float size, String value, boolean max) {
        // Get size of box containing value text.
        final Rect rect = new Rect();
        thresholdsPaint.getTextBounds(value, 0, value.length(), rect);
        final float a = rect.width();
        final float b = rect.height();

        // CenterY + 3 * size / 8 moves position to the bottom of the arc and 2 * b moves it additionally so that there is a little empty space between the arc and the text.
        final float yOffset = centerY + 3 * size / 8 + 2 * b;

        if (max) {
            // When value is max, we write it underneath the arc on the right.
            // CenterX + size / 2 moves position to the right underneath the arc.
            // But since text starts at that position we add - a / 2 so we move it a little bit to the left, so the whole text fits underneath the arc.
            canvas.drawText(value, centerX + size / 2 - a / 2, yOffset, thresholdsPaint);
        } else {
            // When value is min we write in underneath the arc on the left.
            // CenterX - size / 2 moves position to the left underneath the arc.
            // But since text starts at that position we add - a / 2 so we move it a little more to the left, so the whole text fits underneath the arc.
            canvas.drawText(value, centerX - size / 2 - a / 2, yOffset, thresholdsPaint);
        }
    }

    /**
     * Scales dp to px depending on screen resolution.
     *
     * @param context context
     * @param dp      dp
     * @return dp scaled to px
     */
    public static float dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

}
