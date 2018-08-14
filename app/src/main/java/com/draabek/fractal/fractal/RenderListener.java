package com.draabek.fractal.fractal;

/**
 * Created by Vojtech Drabek on 2018-02-14.
 */
public interface RenderListener {
    void onRenderRequested();
    void onRenderComplete(long millis);
}
