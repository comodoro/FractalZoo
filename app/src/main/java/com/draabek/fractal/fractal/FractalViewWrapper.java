package com.draabek.fractal.fractal;

import com.draabek.fractal.fractal.RenderListener;

/**
 * Created by Vojta on 04.08.2017.
 */

public interface FractalViewWrapper {
    void saveBitmap();
    void setVisibility(int visibility);
    boolean isRendering();
    void setRenderListener(RenderListener renderListener);
    void clear();
}
