package com.draabek.fractal.gl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.draabek.fractal.FractalViewWrapper;
import com.draabek.fractal.R;
import com.draabek.fractal.SaveBitmapActivity;
import com.draabek.fractal.Utils;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vojtech Drabek on 2018-01-13
 */

public class RenderImageView extends android.support.v7.widget.AppCompatImageView implements FractalViewWrapper {

    Thread glThread;
    boolean renderingFlag;
    boolean reinitFlag;
    boolean destroyFlag;
    PixelBuffer pixelBuffer;
    SquareRenderer squareRenderer;

    float mPreviousX;
    float mPreviousY;
    float mPreviousX2;
    float mPreviousY2;

    public RenderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        listenToLayout();
    }

    public RenderImageView(Context context) {
        super(context);
        listenToLayout();
    }

    private void listenToLayout() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                init();
                reinitFlag = true;
                requestRender();
            }
        });
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            requestRender();
        }
    }

    public void init() {
        glThread = new Thread(() -> {
            while (!destroyFlag) {
                if (reinitFlag) {
                    pixelBuffer = new PixelBuffer(getWidth(), getHeight());
                    squareRenderer = new SquareRenderer();
                    pixelBuffer.setRenderer(squareRenderer);
                    reinitFlag = false;

                }
                if (renderingFlag) {
                    Bitmap bitmap = pixelBuffer.getBitmap();
                    this.post(() -> {
                        this.setImageBitmap(bitmap);
                        this.renderingFlag = false;
                    });
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        glThread.start();
    }

    //or just save current bitmap redundantly
    public Bitmap getBitmap() {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) getDrawable());
            Bitmap bitmap;
            if (bitmapDrawable == null) {
                buildDrawingCache();
                bitmap = getDrawingCache();
                destroyDrawingCache();
            } else {
                bitmap = bitmapDrawable.getBitmap();
            }
            return bitmap;
    }

    @Override
    public void saveBitmap() {
        try {
            File tmpFile = File.createTempFile("bitmap", ".img", getContext().getCacheDir());
            Bitmap bitmap = this.getBitmap();
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(tmpFile))) throw new IOException("Could not compress bitmap");
            Intent intent = new Intent(this.getContext(), SaveBitmapActivity.class);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(getContext().getString(R.string.intent_extra_bitmap_file), tmpFile.getAbsolutePath());
            getContext().startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this.getContext(), "Could not save current image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRendering() {
        return renderingFlag;
    }

    @Override
    public View getView() {
        return this;
    }

    public void requestRender() {
        renderingFlag = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float TOUCH_SCALE_FACTOR = 1.5f/Math.min(getWidth(), getHeight());

        float x = e.getX();
        float y = e.getY();
        float x2 = 0;
        float y2 = 0;
        if (e.getPointerCount() > 1) {
            x2 = e.getX(1);
            y2 = e.getY(1);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (Utils.DEBUG) {
                    Log.d(this.getClass().getName(), "GL MOVE");
                }
                if (e.getPointerCount() == 1) {
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    Float fractalX = FractalRegistry.getInstance().getCurrent()
                            .getParameters().get("centerX");
                    Float fractalY = FractalRegistry.getInstance().getCurrent()
                            .getParameters().get("centerY");
                    if ((fractalX == null) && (fractalY == null)) {
                        Log.i(this.getClass().getName(), "Fractal has no movable center");
                    } else {
                        if (fractalX != null) {
                            FractalRegistry.getInstance().getCurrent()
                                    .getParameters().put("centerX", fractalX + dx * TOUCH_SCALE_FACTOR);
                            Log.v(this.getClass().getName(), "X shift: " + dx * TOUCH_SCALE_FACTOR);
                        }
                        if (fractalY != null) {
                            //- instead of + because OpenGL has y axis upside down
                            FractalRegistry.getInstance().getCurrent()
                                    .getParameters().put("centerY", fractalY - dy * TOUCH_SCALE_FACTOR);
                            Log.v(this.getClass().getName(), "Y shift: " + dy * TOUCH_SCALE_FACTOR);
                        }
                    }
                } else if ((e.getPointerCount() == 2) && ((mPreviousY2 > 0) || (mPreviousX2 > 0))) {
                    Float scale = FractalRegistry.getInstance().getCurrent()
                            .getParameters().get("scale");
                    if (scale == null) {
                        Log.i(this.getClass().getName(), "Fractal is not scaleable");
                    } else {
                        // Probably abs() is sufficient, but this is better for clarity
                        float oldDist = (float) Math.sqrt((mPreviousX - mPreviousX2) * (mPreviousX - mPreviousX2) +
                                (mPreviousY - mPreviousY2) * (mPreviousY - mPreviousY2));
                        float newDist = (float) Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
                        if (oldDist > 0) {
                            FractalRegistry.getInstance().getCurrent().getParameters().put("scale",
                                    scale * newDist / oldDist);
                            Log.v(this.getClass().getName(), "Scale: " + scale * newDist / oldDist);
                        }
                    }
                }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        mPreviousX2 = x2;
        mPreviousY2 = y2;
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
