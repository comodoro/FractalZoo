package com.draabek.fractal;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FractalPreferenceActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.options);
	}
}