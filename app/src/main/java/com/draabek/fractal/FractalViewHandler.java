package com.draabek.fractal;

import java.io.OutputStream;

/**
 * Created by Vojta on 04.08.2017.
 */

public interface FractalViewHandler {
    public boolean saveBitmap(OutputStream os);
    void setVisibility(int visibility);
    void invalidate();
}
