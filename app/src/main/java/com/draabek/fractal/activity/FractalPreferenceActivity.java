package com.draabek.fractal.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.draabek.fractal.R;

public class FractalPreferenceActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.options);
	}
}