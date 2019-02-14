package com.draabek.fractal.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsReadUtils {
    private AssetsReadUtils() {
    }

    public static String[] readFractalMetadata(Context context) throws IOException {
        String[] fractals = context.getAssets().list("fractals");
        String[] fractalStrings = new String[fractals.length];

        for (int i = 0; i < fractals.length; i++) {
            InputStream is = context.getAssets().open("fractals/" + fractals[i]);
            fractalStrings[i] = readFully(is);
            is.close();
        }

        return fractalStrings;
    }

    private static String readFully(InputStream inputStream) throws IOException {
        String line;

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }
}
