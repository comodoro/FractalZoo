package com.draabek.fractal.canvas.instance;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.draabek.fractal.canvas.CanvasFractal;

/**
 * Created by ivankruger on 28/10/2017.
 */

public class KochSnowflake extends CanvasFractal {

    Canvas canvas;
    Paint paint;
    Paint midPointPaint;

    @Override
    public void draw(Canvas _canvas) {
        canvas = _canvas;
        int iterations = this.getSettings().get("iterations").intValue();
        float centerX = this.getSettings().get("centerX");
        float centerY = this.getSettings().get("centerY");
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1.0f);

        midPointPaint = new Paint();
        midPointPaint.setColor(Color.RED);
        midPointPaint.setStyle(Paint.Style.FILL);

        int paddingX = canvas.getWidth()/10;
        int paddingY = canvas.getHeight()/3;

        int firstX = paddingX;
        int secondX = canvas.getWidth() - paddingX;
        int firstY = canvas.getHeight() - paddingY;
        int secondY = canvas.getHeight() - paddingY;

        float dx = secondX - firstX;
        float dy = secondY - firstY;

        int length = (int) Math.sqrt(dx*dx+dy*dy);

        float dirX = dx / length;
        float dirY = dy / length;

        float height = (float)(Math.sqrt(2)/2 * length);

        float cx = firstX + dx * 0.5f;
        float cy = firstY + dy * 0.5f;
        float pDirX = -dirY;
        float pDirY = dirX;
        float thirdX = cx - height * pDirX;
        float thirdY = cy - height * pDirY;

        drawNewPoint(firstX, firstY, secondX, secondY, iterations);
        drawNewPoint(secondX, secondY, thirdX, thirdY, iterations);
        drawNewPoint(thirdX, thirdY, firstX, firstY, iterations);


    }

    private void drawNewPoint(float startX, float startY, float endX, float endY, int iterations){

        if(iterations <= 0) {
            canvas.drawLine(startX,startY,endX,endY,paint);
            return;
        }

        if(iterations >= 1){
            float distanceX = (endX - startX)/3;
            float distanceY = (endY - startY)/3;

            float aX = startX+distanceX;
            float aY = startY+distanceY;

            float bX = endX-distanceX;
            float bY = endY-distanceY;

            double sin60 = -0.866025403784438646763723170752936183471402626905190;

            float newPointX = aX + (float)(distanceX * 0.5 + distanceY * sin60);
            float newPointY = aY + (float)(distanceY * 0.5 - distanceX * sin60);

            drawNewPoint(startX,startY,aX,aY,iterations - 1);
            drawNewPoint(aX,aY,newPointX,newPointY,iterations - 1);
            drawNewPoint(newPointX,newPointY,bX,bY,iterations - 1);
            drawNewPoint(bX,bY,endX,endY,iterations - 1);

        }

    }
}
