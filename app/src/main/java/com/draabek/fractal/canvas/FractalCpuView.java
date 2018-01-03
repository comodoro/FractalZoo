package com.draabek.fractal.canvas;

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

import com.draabek.fractal.FractalViewWrapper;
import com.draabek.fractal.R;
import com.draabek.fractal.SaveBitmapActivity;
import com.draabek.fractal.Utils;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FractalCpuView extends SurfaceView implements SurfaceHolder.Callback, FractalViewWrapper
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
	private boolean rendering;
	
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

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	@Override
	protected void onDraw(Canvas canvas) {
		rendering = true;
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
				fractalBitmap = ((BitmapDrawFractal)fractal).redrawBitmap(fractalBitmap, position);
			} else if (fractal instanceof CanvasFractal) {
				Log.v(LOG_KEY, "Draw to canvas");
				((CanvasFractal)fractal).draw(bufferCanvas);
			} else {
				throw new RuntimeException("Wrong fractal type for " + this.getClass().getName());
			}
			canvas.drawBitmap(fractalBitmap, 0, 0, paint);
		}
		Log.d(LOG_KEY, "finished onDraw");
		rendering = false;
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
		prefs.edit().putString(Utils.PREFS_CURRENT_FRACTAL_KEY, FractalRegistry.getInstance().getCurrent().getName()).apply();
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
	
	@SuppressWarnings("SynchronizeOnNonFinalField")
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

	@Override
	public void saveBitmap() {
		try {
			File tmpFile = File.createTempFile("bitmap", "jpg", this.getContext().getCacheDir());
			fractalBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					new FileOutputStream(tmpFile));
			Intent intent = new Intent(this.getContext(), SaveBitmapActivity.class);
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(this.getContext().getString(R.string.intent_extra_bitmap_file), tmpFile.getAbsolutePath());
			this.getContext().startActivity(intent);
		} catch (IOException e) {
			Toast.makeText(this.getContext(), "Could not save current image", Toast.LENGTH_SHORT).show();;
			e.printStackTrace();
		}

	}

	@Override
	public boolean isRendering() {
		return rendering;
	}

	@Override
	public View getView() {
		return this;
	}

	class MotionTracker implements OnTouchListener {
		private float distance;
		private PointF origin;
		private boolean isMoveGesture;
		private boolean isGesture;
		
		void update(MotionEvent evt) {
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
						float ratio = newDistance / distance;
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
			v.performClick();
			return true;
		}
	}
}
