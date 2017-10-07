package com.draabek.fractal;

import android.view.View;

/**
 * Created by Vojta on 04.08.2017.
 */

public interface FractalViewHandler {
    void saveBitmap();
    void setVisibility(int visibility);
    boolean isRendering();
    View getView();
}
