package com.draabek.fractal.gl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.draabek.fractal.FractalViewWrapper;
import com.draabek.fractal.R;
import com.draabek.fractal.SaveBitmapActivity;

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
                buildDrawingCache(false);
            } else {
                bitmap = bitmapDrawable .getBitmap();
            }
            return bitmap;
    }

    @Override
    public void saveBitmap() {
        try {
            File tmpFile = File.createTempFile("bitmap", "jpg", getContext().getCacheDir());
            Bitmap bitmap = this.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(tmpFile));
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

}
