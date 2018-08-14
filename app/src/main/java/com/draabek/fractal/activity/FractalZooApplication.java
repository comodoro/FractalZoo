package com.draabek.fractal.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class FractalZooApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    protected static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
