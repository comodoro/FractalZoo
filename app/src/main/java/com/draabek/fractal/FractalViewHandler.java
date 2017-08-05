package com.draabek.fractal;

import com.draabek.fractal.fractal.Fractal;

import java.io.OutputStream;

/**
 * Created by Vojta on 04.08.2017.
 */

public interface FractalViewHandler {
    public Fractal getFractal();
    public void setFractal(Fractal fractal);
    public boolean saveBitmap(OutputStream os);
    public void invalidate();

}
