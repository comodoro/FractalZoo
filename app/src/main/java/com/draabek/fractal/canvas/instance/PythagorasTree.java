package com.draabek.fractal.canvas.instance;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.draabek.fractal.canvas.CanvasFractal;

/**
 * Created by Vojta on 12.09.2017.
 * Based on https://rosettacode.org/wiki/Pythagoras_tree#Java
 */

public class PythagorasTree extends CanvasFractal {
    @Override
    public void draw(Canvas canvas) {
        int iterations = this.getSettings().get("iterations").intValue();
        float centerX = this.getSettings().get("centerX");
        float centerY = this.getSettings().get("centerY");
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1.0f);
        float x1 = canvas.getWidth() * 7 / 16 + centerX * canvas.getWidth();
        float y1 = canvas.getHeight();
        float x2 = canvas.getWidth() * 9 / 16 + centerX * canvas.getWidth();
        float y2 = canvas.getHeight();
        drawTree(canvas, paint, x1, y1, x2, y2, iterations);
    }

    private void fillTriangle(Canvas canvas, Paint paint, float x1, float y1, float x2, float y2, float x3, float y3) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void fillSquare(Canvas canvas, Paint paint, float x1, float y1, float x2, float y2,
                            float x3, float y3, float x4, float y4) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawTree(Canvas canvas, Paint paint, float x1, float y1, float x2, float y2, int depth) {
        if (depth == 0) return;
        float dx = x2 - x1;
        float dy = y1 - y2;
        float x3 = x2 - dy;
        float y3 = y2 - dx;
        float x4 = x1 - dy;
        float y4 = y1 - dx;
        float x5 = x4 + 0.5F * (dx - dy);
        float y5 = y4 - 0.5F * (dx + dy);
        fillSquare(canvas, paint, x1, y1, x2, y2, x3, y3, x4, y4);
        fillTriangle(canvas, paint, x3, y3, x4, y4, x5, y5);
        drawTree(canvas, paint, x4, y4, x5, y5, depth - 1);
        drawTree(canvas, paint, x5, y5, x3, y3, depth - 1);
    }

}
