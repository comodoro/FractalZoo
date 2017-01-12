package com.draabek.fractal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.FileOutputStream;

public class FractalView extends SurfaceView implements SurfaceHolder.Callback
//,GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener 
{

	private static final String LOG_KEY = FractalView.class.getName();
	public static final String FRACTALS_PREFERENCE	= "FRACTALS_PREFERENCE";
	public static final String PREFS_CURRENT_FRACTAL_KEY = "prefs_current_fractal";
	private Bitmap fractalBitmap;
	private Fractal fractal;
	private RectF position;
	private RectF oldPosition;
	private boolean portrait = false;
	private Paint paint;
	private SurfaceHolder holder;
	private SharedPreferences prefs;
	
	public FractalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FractalView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		this.setOnTouchListener(new MotionTracker());
		/*gd = new GestureDetector(context, this);
		gd.setOnDoubleTapListener(this);*/
		paint = new Paint();
		prefs = context.getSharedPreferences(FRACTALS_PREFERENCE, Context.MODE_PRIVATE);
		String name = prefs.getString(PREFS_CURRENT_FRACTAL_KEY, "Mandelbrot");//getResources().getString(R.string.mandelbrot));
		fractal = FractalRegistry.getInstance().get(name);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(LOG_KEY,"onDraw");
		SurfaceHolder sh = getHolder();
		synchronized (sh) {
			if (fractalBitmap == null)
				fractalBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
						Bitmap.Config.ARGB_8888);
			canvas.drawBitmap(fractalBitmap, 0, 0, paint);
		}
		Log.d(LOG_KEY, "finished onDraw");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.holder = holder;
		Log.d(LOG_KEY,"surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		Log.d(LOG_KEY,"surface created");
		fractalBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		position = new RectF(1, -2, -1, 1);
		fractalBitmap = fractal.redrawBitmap(fractalBitmap, position, true);
		invalidate();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(LOG_KEY, "surface destroyed");
	}
	
	public void startTranslate() {
		oldPosition = new RectF();
		oldPosition.set(position);
	}
	
	public void endTranslate() {
		Log.d(LOG_KEY, "Translation ended, redrawing fractal");
		fractalBitmap = fractal.redrawBitmap(fractalBitmap, position, true);
		invalidate();
	}
	
	public void translate(float xshift, float yshift) {
		//Need to convert pixel coordinates into unitless complex fractal coordinates
		float xstep = (position.right - position.left) / getWidth();
		float ystep = (position.bottom - position.top) / getHeight();
		//the coordinates actually represent the set not the screen, hence the minus
		xshift = -xshift * xstep;
		yshift = -yshift * ystep;
		position.left = oldPosition.left + xshift;
		position.right = oldPosition.right + xshift;
		position.top = oldPosition.top + yshift;
		position.bottom = oldPosition.bottom + yshift;
		gestureRedraw();
		Log.d(LOG_KEY, "Translate: " + xshift + " horizontally, " + yshift + " vertically");
	}

	public void startScale() {
		oldPosition = new RectF();
		oldPosition.set(position);
	}
	
	public void endScale() {
		Log.d(LOG_KEY, "Scale gesture ended, redrawing fractal");
		fractalBitmap = fractal.redrawBitmap(fractalBitmap, position, true);
		invalidate();
	}
	
	public void scale(float scale) {
		float width = oldPosition.right - oldPosition.left;
		float height = oldPosition.bottom - oldPosition.top;
		float xcenter = oldPosition.left + width/2;
		float ycenter = oldPosition.top + height/2;
		//the function actually gets 1/scale
		float newWidth = width / scale;
		float newHeight = height / scale;
		position.left = xcenter - newWidth/2;
		position.right = xcenter + newWidth/2;
		position.top = ycenter - newHeight/2;
		position.bottom = ycenter + newHeight/2;
		gestureRedraw();
		Log.d(LOG_KEY, "Scale: " + scale);
	}
	
	public void gestureRedraw() {
		Log.d(LOG_KEY, "Redrawing gesture");
		Canvas c = null;
		try {
			synchronized(holder) {
				c = holder.lockCanvas();
				float xratio = (oldPosition.right - oldPosition.left) / getWidth();
				float yratio = (oldPosition.bottom - oldPosition.top) / getHeight();
				Rect src = new Rect(0, 0, getWidth(), getHeight());
				RectF dst = new RectF(position.left/xratio, position.top/yratio, position.right/xratio, position.bottom/yratio);
				c.drawBitmap(fractalBitmap, src, dst, paint);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	
	public void tap(float x, float y) {
		Log.d(LOG_KEY, "Single tap at point [" + x + ", " + y + "]");
	}
	
	class MotionTracker implements OnTouchListener {
		private float distance;
		private PointF origin;
		private boolean isMoveGesture;
		private boolean isGesture;
		
		public void update(MotionEvent evt) {
			Log.d(LOG_KEY, "Update on screen touch");
			float x = evt.getX();
			float y = evt.getY();
			int action = evt.getAction() & MotionEvent.ACTION_MASK;
			if (action == MotionEvent.ACTION_DOWN) {
				Log.d(LOG_KEY, "Touch down");
				isGesture = false;
			} else if (((action == MotionEvent.ACTION_UP)
					|| ((action == MotionEvent.ACTION_POINTER_UP)))
							&& (isGesture == false)) {
				//single tap ocurred
				if ((action == MotionEvent.ACTION_POINTER_UP)) {
					tap(x, y);
				} 
			} else if (action == MotionEvent.ACTION_POINTER_DOWN) {
					Log.d(LOG_KEY, "Second pointer landed, starting pinch gesture");
					isGesture = true;
					isMoveGesture = false;
					startScale();
			} else if (action == MotionEvent.ACTION_MOVE) {
				if (!isGesture) {
					isGesture = true;
					isMoveGesture = true;
					Log.d(LOG_KEY, "Starting move gesture");
					origin = new PointF(x, y);
					startTranslate();
				} else if (isMoveGesture) {
					Log.d(LOG_KEY, "Continuing move gesture");
					if (origin == null) {
						Log.d(LOG_KEY, "Should not happen: move gesture already started and origin is null");
					} else {
						translate((x - origin.x), (y - origin.y));
					}
				} else if (!isMoveGesture) {
					Log.d(LOG_KEY, "Continuing pinch gesture");
					int n = evt.getPointerCount();
					if (n < 2) return;
					float x2 = evt.getX(1);
					float y2 = evt.getY(1);
					float newDistance = (float)Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
					if (distance != 0) {
						float ratio = (float)(newDistance / distance);
						//if (ratio > 1) {
							scale(ratio);
						//}
					} else {
						distance = newDistance;
					}
				}
			} else if (action == MotionEvent.ACTION_UP) {
				Log.d(LOG_KEY, "Everything up, resetting gestures");
				isGesture = false;
				distance = 0;
				origin = null;
				endTranslate();
				endScale();
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			update(event);
			return true;
		}
		
	}
	
	
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		logger.info("MotionEvent");
		if (gd.onTouchEvent(event)) {
			logger.info("GD handled the event");
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			logger.info("MotionEvent down");
			return false;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			logger.info("MotionEvent move");
			float x = event.getX() - lastX;
			float y = event.getY() - lastY;
			logger.info("Motion coordinates: " + x + ", " + y);
			float origX = position.left;
			float origY = position.top;
			float w = position.width();
			float h = position.height();
			position = new RectF(x + origX, y + origY, w, h);
			fractalBitmap = fractal.redrawBitmap(fractalBitmap, position, true);
			lastX = x;
			lastY = y;
			invalidate();
			return true;
		} else {
			return false;
		}
	}*/

	public boolean saveBitmap(FileOutputStream fos) {
		if (fractalBitmap != null) {
			return fractalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} else {
			Log.e(LOG_KEY, "Attempting to save null bitmap");
			return false;
		}
	}

	public Fractal getFractal() {
		return fractal;
	}
	
	public void setFractal(Fractal fractal) {
		this.fractal = fractal;
	}

	/*
	@Override
	public boolean onDoubleTap(MotionEvent event) {
		logger.info("onDoubleTap");
		lastX = event.getX();
		lastY = event.getY();
		float w = position.width();
		float h = position.height();
		float x = position.left;
		float y = position.top;
		logger.info("Rectangle: " + x + ", " + y + ", " + w + ", " + h);
		position = new RectF(x + lastX - w, y + lastY - h, 2 * w, 2 * h);
		fractalBitmap = fractal.redrawBitmap(fractalBitmap, position, true);
		invalidate();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		logger.info("onDoubleTapEvent");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		logger.info("onSingleTapConfirmed");
		return false;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		logger.info("onDown");
		lastX = event.getX();
		lastY = event.getY();
		float w = position.width();
		float h = position.height();
		float x = position.left;
		float y = position.top;
		float normX = x + lastX / getWidth() * w - w/4;
		float normY = y + lastY / getHeight() * h - h/4;
		logger.info("Rectangle: " + normX + ", " + normY + ", " + w/2 + ", " + h/2);
		position = new RectF(normX, normY,
				w/2, h/2);
		//fractalBitmap = fractal.redrawBitmap(fractalBitmap, position, true);
		//invalidate();
		new RedrawFractalTask().execute((Void[])null);
		return false;	
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		logger.info("onFling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		logger.info("onLongPress");

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		logger.info("onScroll");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		logger.info("onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		logger.info("onSingleTapUp");
		return false;
	}
	
	private class RedrawFractalTask extends AsyncTask<Void, Void, Bitmap> {
	     protected Bitmap doInBackground(Void... params) {
	         return fractal.redrawBitmap(fractalBitmap, position, portrait);
	     }

	     protected void onPostExecute(Bitmap result) {
	         invalidate();
	     }
	 }
	*/
}
