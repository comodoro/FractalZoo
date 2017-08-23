package com.draabek.fractal;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.util.Arrays;
import java.util.List;

public class FractalListActivity extends ListActivity {
	public static final String EXTRA_KEY = "Fractal";
	private static final String LOG_KEY = FractalListActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List fractalList = Arrays.asList(FractalRegistry.getInstance().getFractals().values().toArray());
		ArrayAdapter<Fractal> adapter = new ArrayAdapter<Fractal>(this, android.R.layout.simple_list_item_1,
				fractalList);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Fractal fractal = (Fractal)getListView().getItemAtPosition(position);
		Log.d(LOG_KEY, fractal.getName() + " clicked");
		Intent intent = this.getIntent();
		intent.putExtra(EXTRA_KEY, fractal.getClass());
		this.setResult(RESULT_OK, intent);
		SharedPreferences prefs = this.getApplicationContext().getSharedPreferences(
				FractalView.FRACTALS_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(FractalView.PREFS_CURRENT_FRACTAL_KEY, fractal.getName());
		editor.apply();
		FractalRegistry.getInstance().setCurrent(fractal);
		finish();
	}
	
	
}
