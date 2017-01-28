package com.draabek.fractal.palette;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vojta on 28.01.2017.
 */
public class BWPaletteTest {
    @Test
    public void getColor() throws Exception {
        BWPalette bwp = new BWPalette(1);
        //return black for < 0.5, white otherwise
        assertEquals(0xff000000, bwp.getColor(0.1));
        assertEquals(0xffffffff, bwp.getColor(0.9));
    }

}