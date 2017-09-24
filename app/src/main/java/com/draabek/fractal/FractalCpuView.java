package com.draabek.fractal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.draabek.fractal.fractal.BitmapDrawFractal;
import com.draabek.fractal.fractal.CanvasFractal;
import com.draabek.fractal.fractal.CpuFractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FractalCpuView extends SurfaceView implements SurfaceHolder.Callback, FractalViewHandler
//,GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener 
{

	private static final String LOG_KEY = FractalCpuView.class.getName();
	private Bitmap fractalBitmap;
	private CpuFractal fractal;
	private RectF position;
	private RectF oldPosition;
	private Paint paint;
	private Canvas bufferCanvas = null;
	private SurfaceHolder holder;
	private SharedPreferences prefs;
	
	public FractalCpuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FractalCpuView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		this.setOnTouchListener(new MotionTracker());
		paint = new Paint();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(LOG_KEY,"onDraw");
		SurfaceHolder sh = getHolder();
		synchronized (sh) {
			if ((fractalBitmap == null) || (fractalBitmap.getHeight() != canvas.getHeight()) ||
					(fractalBitmap.getWidth() != canvas.getWidth()) || (bufferCanvas == null)) {
				Log.v(LOG_KEY, "Reallocate buffer");
				fractalBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
						Bitmap.Config.ARGB_8888);
				bufferCanvas = new Canvas(fractalBitmap);
			}
			if (fractal instanceof BitmapDrawFractal) {
				Log.v(LOG_KEY, "Start drawing to buffer");
				fractalBitmap = ((BitmapDrawFractal)fractal).redrawBitmap(fractalBitmap, position, true);
			} else if (fractal instanceof CanvasFractal) {
				Log.v(LOG_KEY, "Draw to canvas");
				((CanvasFractal)fractal).draw(bufferCanvas);
			} else {
				throw new RuntimeException("Wrong fractal type for " + this.getClass().getName());
			}
			canvas.drawBitmap(fractalBitmap, 0, 0, paint);
		}
		Log.d(LOG_KEY, "finished onDraw");
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.holder = holder;
		Log.d(LOG_KEY,"surface changed");
		fractal = (CpuFractal) FractalRegistry.getInstance().getCurrent();
		invalidate();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		Log.d(LOG_KEY,"surface created");
		fractalBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		position = new RectF(1, -2, -1, 1);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(LOG_KEY, "surface destroyed");
		bufferCanvas = null;
		fractalBitmap = null;
		//consider apply instead of commit
		prefs.edit().putString(Utils.PREFS_CURRENT_FRACTAL_KEY, FractalRegistry.getInstance().getCurrent().getName()).commit();
	}
	
	public void startTranslate() {
		oldPosition = new RectF();
		oldPosition.set(position);
	}

	public void translate(float xshift, float yshift) {
		gestureRedraw(xshift, yshift, 1);
		Log.d(LOG_KEY, "Translate: " + xshift + " horizontally, " + yshift + " vertically");
	}

	public void startScale() {
		oldPosition = new RectF();
		oldPosition.set(position);
	}
	
	public void endGesture() {
		Log.d(LOG_KEY, "Gesture ended, redrawing fractal");
		oldPosition = null;
		invalidate();
	}
	
	public void scale(float scale) {
		gestureRedraw(0, 0, scale);
		Log.d(LOG_KEY, "Scale: " + scale);
	}
	
	public void gestureRedraw(float dx, float dy, float scale) {
		Log.d(LOG_KEY, "Redrawing gesture");
		Canvas c = null;
		try {
			synchronized(holder) {
				c = holder.lockCanvas();
				if (scale != 1) c.scale(scale, scale);
				if ((dx != 0) || (dy != 0)) c.translate(dx, dy);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
				invalidate();
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
			} else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
					&& !isGesture) {
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
				} else {
					Log.d(LOG_KEY, "Continuing pinch gesture");
					int n = evt.getPointerCount();
					if (n < 2) return;
					float x2 = evt.getX(1);
					float y2 = evt.getY(1);
					float newDistance = (float)Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
					if (distance != 0) {
						float ratio = (float)(newDistance / distance);
						scale(ratio);
					} else {
						distance = newDistance;
					}
				}
			} else if (action == MotionEvent.ACTION_UP) {
				Log.d(LOG_KEY, "Everything up, resetting gestures");
				isGesture = false;
				distance = 0;
				origin = null;
				endGesture();
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			update(event);
			return true;
		}
		
	}

	@Override
	public void saveBitmap() {
		try {
			File tmpFile = File.createTempFile("bitmap", "jpg", getContext().getCacheDir());
			fractalBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					new FileOutputStream(tmpFile));
			Intent intent = new Intent(this.getContext(), SaveBitmapActivity.class);
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(getContext().getString(R.string.intent_extra_bitmap_file), tmpFile.getAbsolutePath());
			getContext().startActivity(intent);
		} catch (IOException e) {
			Toast.makeText(this.getContext(), "Could not save current image", Toast.LENGTH_SHORT).show();;
			e.printStackTrace();
		}

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
