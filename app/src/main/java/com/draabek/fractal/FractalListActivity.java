package com.draabek.fractal;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FractalListActivity extends ListActivity {

	private static final String LOG_KEY = FractalListActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Map<String, String>> data = new ArrayList<>();
		for (Fractal fractal : FractalRegistry.getInstance().getFractals().values()) {
			Map<String, String> row = new HashMap<>();
			row.put("name", fractal.getName());
			row.put("thumbnail", fractal.getThumbPath());
			data.add(row);
		}
		SimpleAdapter adapter = new SimpleAdapter(
				this, // Context.
				data,
				R.layout.list_view_row,
				new String[]{"name", "thumbnail"},
				new int[]{R.id.list_view_name, R.id.list_view_thumb}
		);
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if ((view instanceof ImageView) && (data != null)) {
					String s = (String)data;
					if (s.startsWith("file://")) {
						Uri imageUri = Uri.parse(s);
						((ImageView)view).setImageURI(imageUri);
					} else {
						Bitmap bmp = Utils.getBitmapFromAsset(getAssets(), s);
						((ImageView)view).setImageBitmap(bmp);
					}
					Log.v(LOG_KEY, "Thumb uri: " + data);
				} else if (view instanceof TextView) {
					((TextView)view).setText((String)data);
				}
				return true;
			}
		});
		// Bind to our new adapter.
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Fractal fractal = FractalRegistry.getInstance().get(
				    ((Map)(getListView().getItemAtPosition(position))).get("name").toString()
		);
		Log.d(LOG_KEY, fractal.getName() + " clicked");
		Intent intent = this.getIntent();
		intent.putExtra(MainActivity.CURRENT_FRACTAL_KEY, fractal.getName());
		//FractalRegistry.getInstance().setCurrent(fractal);
		this.setResult(RESULT_OK, intent);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString(Utils.PREFS_CURRENT_FRACTAL_KEY, fractal.getName());
		editor.commit();
		finish();
	}
	
	
}
