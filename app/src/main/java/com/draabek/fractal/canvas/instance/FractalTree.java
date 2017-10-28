package com.draabek.fractal.canvas.instance;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.draabek.fractal.canvas.CanvasFractal;

/**
 * Created by Vojta on 12.09.2017.
 * Based on https://rosettacode.org/wiki/Fractal_tree#Java
 */

public class FractalTree extends CanvasFractal {
    @Override
    public void draw(Canvas canvas) {
        int iterations = this.getParameters().get("iterations").intValue();
        float centerX = this.getParameters().get("centerX");
        float centerY = this.getParameters().get("centerY");
        float angleInc = this.getParameters().get("angle");
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);
        int x1 = (int)(canvas.getWidth() / 2 + centerX * canvas.getWidth());
        int y1 = (int)(centerY * canvas.getHeight());
        drawTree(canvas, paint, x1, y1, -90, iterations);
    }

    private void drawTree(Canvas canvas, Paint paint, int x1, int y1, float angle, int depth) {
        if (depth == 0) return;
        int iterations = this.getParameters().get("iterations").intValue();
        int x2 = x1 + (int) (Math.cos(Math.toRadians(angle)) * depth * 50.0/iterations);
        int y2 = y1 + (int) (Math.sin(Math.toRadians(angle)) * depth * 50.0/iterations);
        canvas.drawLine(x1, y1, x2, y2, paint);
        float angleInc = this.getParameters().get("angle");
        drawTree(canvas, paint, x2, y2, angle - angleInc, depth - 1);
        drawTree(canvas, paint, x2, y2, angle + angleInc, depth - 1);
    }

}
