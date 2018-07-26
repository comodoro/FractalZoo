package com.draabek.fractal;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Vojta on 11.09.2017.
 */

public class Utils {
    public static final String FRACTALS_PREFERENCE	= "FRACTALS_PREFERENCE";
    public static final String PREFS_CURRENT_FRACTAL_KEY = "prefs_current_fractal_key";
    public static final String PREFS_CURRENT_FRACTAL_PATH = "prefs_current_fractal_path";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static Bitmap getBitmapFromAsset(AssetManager mgr, String path) {
        InputStream is = null;
        Bitmap bitmap;
        try {
            is = mgr.open(path);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (final IOException e) {
            bitmap = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }
}
