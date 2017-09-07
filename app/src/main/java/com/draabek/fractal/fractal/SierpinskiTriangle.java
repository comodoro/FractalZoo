package com.draabek.fractal.fractal;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class SierpinskiTriangle extends CanvasFractal {


	public SierpinskiTriangle() {

	}

	@Override
	public void draw(Canvas canvas) {
		int iterations = this.getSettings().get("iterations").intValue();
		float centerX = this.getSettings().get("centerX");
		float centerY = this.getSettings().get("centerY");
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(2.0f);
		canvas.drawLine(canvas.getWidth()/2, 0, 0, canvas.getHeight(), paint);
		canvas.drawLine(canvas.getWidth()/2, 0, canvas.getWidth(), canvas.getHeight(), paint);
		canvas.drawLine(canvas.getWidth(), canvas.getHeight(), 0, canvas.getHeight(), paint);
	}

	private void drawHelper(Canvas canvas, int depth) {
		/*float centerX = this.getSettings().get("centerX");
		float centerY = this.getSettings().get("centerY");
		float scale  = this.getSettings().get("scale");
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		float adjcenterX = (centerX * width * scale) + width/2;
		float adjcenterY = (centerY * height * scale) + height/2;
		Paint paint = new Paint();
		canvas.drawLine(adjcenterX, adjcenterY - height/2*scale, );*/
	}
}
