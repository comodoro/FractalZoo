package com.draabek.fractal.palette;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vojta on 28.01.2017.
 */
public class BWPaletteTest {
    @Test
    public void getColorInt() throws Exception {
        BWPalette bwp = new BWPalette();
        //return black for < 0.5, white otherwise
        assertEquals(0xff000000, bwp.getColorInt(0.1f));
        assertEquals(0xffffffff, bwp.getColorInt(0.9f));
    }

}