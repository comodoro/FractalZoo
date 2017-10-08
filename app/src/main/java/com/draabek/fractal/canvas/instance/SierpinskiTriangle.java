package com.draabek.fractal.canvas.instance;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.draabek.fractal.canvas.CanvasFractal;

public class SierpinskiTriangle extends CanvasFractal {

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
		paint.setStrokeWidth(0.1f);
		int shortDim = Math.min(canvas.getWidth(), canvas.getHeight());
		int startX = (int)((canvas.getWidth()-shortDim)/2+centerX*canvas.getWidth());
		int endX = canvas.getWidth()-startX;
		int startY = (int)((canvas.getHeight()-shortDim)/2+centerY*canvas.getHeight());
		int endY = canvas.getHeight()-startY;
		fillTriangle(canvas, paint, (startX + endX)/2, startY, startX, endY, endX, endY);
		paint.setColor(Color.BLACK);
		drawHelper(canvas, paint, startX, startY, endX, endY, shortDim, iterations);
	}


	private void fillTriangle(Canvas canvas, Paint paint, int x1, int y1, int x2, int y2, int x3, int y3) {
		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.close();
		canvas.drawPath(path, paint);
	}
	private void drawHelper(Canvas canvas, Paint paint, int startX, int startY, int endX, int endY, int dim, int depth) {
		fillTriangle(canvas, paint,
				startX + dim/4, startY + dim/2,
				endX - dim/4, startY + dim/2,
				startX + dim/2, endY);
		if (depth < 1) return;
		drawHelper(canvas, paint, startX + dim/4, startY, endX - dim/4, startY  + dim/2,
				dim/2, depth-1);
		drawHelper(canvas, paint, startX, startY+dim/2, endX - dim/2, endY,
				dim/2, depth-1);
		drawHelper(canvas, paint, startX + dim/2, startY+dim/2, endX, endY,
				dim/2, depth-1);
	}
}
