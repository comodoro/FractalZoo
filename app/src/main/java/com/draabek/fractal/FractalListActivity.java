package com.draabek.fractal;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FractalListActivity extends ListActivity {

    public static final String INTENT_HIERARCHY_PATH = "INTENT_HIERARCHY_PATH";
	private Deque<String> hierarchyPath;

	private static final String LOG_KEY = FractalListActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String keyPathStr = getIntent().getStringExtra(INTENT_HIERARCHY_PATH);
		String[] path = (keyPathStr == null) ? new String[]{} : keyPathStr.split("\\|");
		hierarchyPath = new ArrayDeque<>(Arrays.asList(path));
		List<Map<String, String>> data = new ArrayList<>();
		for (String item : FractalRegistry.getInstance().getOnLevel(hierarchyPath)) {
			Map<String, String> row = new HashMap<>();
			row.put("name", item);
			Fractal fractal = FractalRegistry.getInstance().getFractals().get(item);
			row.put("thumbnail", (fractal != null) ? fractal.getThumbPath() : null);
			data.add(row);
		}
		SimpleAdapter adapter = new SimpleAdapter(
				this,
				data,
				R.layout.list_view_row,
				new String[]{"name", "thumbnail"},
				new int[]{R.id.list_view_name, R.id.list_view_thumb}
		);
		adapter.setViewBinder((view, data1, textRepresentation) -> {
            if ((view instanceof ImageView) && (data1 != null)) {
                String s = (String) data1;
                if (s.startsWith("file://")) {
                    Uri imageUri = Uri.parse(s);
                    ((ImageView)view).setImageURI(imageUri);
                } else {
                    Bitmap bmp = Utils.getBitmapFromAsset(getAssets(), s);
                    ((ImageView)view).setImageBitmap(bmp);
                }
                Log.v(LOG_KEY, "Thumb uri: " + data1);
            } else if (view instanceof TextView) {
                String localizedName;
                try {
                    localizedName = getResources().getString(getResources()
                            .getIdentifier((String) data1, "string", "com.draabek.fractal"));
                } catch (Resources.NotFoundException e) {
                    localizedName = (String) data1;
                }
                ((TextView)view).setText(localizedName);
            }
            return true;
        });
		setListAdapter(adapter);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    String item = ((Map<String, String>)(getListView().getItemAtPosition(position))).get("name");
		Fractal fractal = FractalRegistry.getInstance().get(item);
		if (fractal == null) {
            if (Utils.DEBUG) {
                Log.d(LOG_KEY, String.format("Menu item %s clicked", item));
            }
            Intent intent = new Intent(this, FractalListActivity.class);
            //String path = String.join(" ", hierarchyPath); //API 26
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 0;i < hierarchyPath.size();i++) {
                pathBuilder.append(hierarchyPath.pop()).append("|");
            }
            pathBuilder.append(item);
            String path = pathBuilder.toString();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor = prefs.edit();
            editor.putString(Utils.PREFS_CURRENT_FRACTAL_PATH, path);
            editor.apply();
            if (!path.equals("")) intent.putExtra(INTENT_HIERARCHY_PATH, pathBuilder.toString());
            startActivity(intent);
        } else {
            if (Utils.DEBUG) {
                Log.d(LOG_KEY, String.format("Fractal %s clicked", fractal.getName()));
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.CURRENT_FRACTAL_KEY, fractal.getName());
            intent.putExtra(INTENT_HIERARCHY_PATH, getIntent().getStringExtra(INTENT_HIERARCHY_PATH));
            //FractalRegistry.getInstance().setCurrent(fractal);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor = prefs.edit();
            editor.putString(Utils.PREFS_CURRENT_FRACTAL_KEY, fractal.getName());
            editor.apply();
            startActivity(intent);
        }
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (hierarchyPath.size() < 1) return super.onKeyDown(keyCode, event);
            Intent intent = new Intent(this, FractalListActivity.class);
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 0;i < hierarchyPath.size()-1;i++) {
                pathBuilder.append(hierarchyPath.pop()).append("|");
            }
            if (pathBuilder.length() > 0) {
                pathBuilder.deleteCharAt(pathBuilder.length()-1);
                intent.putExtra(INTENT_HIERARCHY_PATH, pathBuilder.toString());
            }

            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
