package com.draabek.fractal.palette;

public class HotPalette extends ColorPalette {

    private int[] intCache;

    public HotPalette(){
        this( 256 );
    }

    public HotPalette(int size){
        intCache = new int[size];
        int idx = (int)(Math.floor(3.0/8 * (double)size));
        for (int i=0; i<size; i++){
            int red = (int)(((i + 1 < idx) ? ((double)(i + 1) / idx) : 1) * 0xff);
            int green = 0;
            if (i > 2 * idx) {
                green = 1;
            } else if (i > idx) {
                green = (int)((double)(i - idx) / idx);
            }
            green = green * 0Xff;
            int blue = (int)(((i + 1 > 2 * idx + 1) ? ((double)(i - idx + 1) / idx) : 0) * 0xff);
            intCache[i] = (blue) | (green << 8) | (red << 16) | 0xff000000;
        }
    }

    @Override
    public int getColorInt(float intensity) {
        return intCache[(int)(intensity*intCache.length)];
    }

    @Override
    public int[] getColorsInt() {
        return intCache;
    }
}
