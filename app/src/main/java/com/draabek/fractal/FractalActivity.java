package com.draabek.fractal;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FractalActivity extends Activity {
	private static final String LOG_KEY = FractalActivity.class.getName();
	public static final int CHOOSE_FRACTAL_CODE = 1;
    private FractalView view;
	
	public FractalActivity() {
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InputStream is = this.getResources().openRawResource(R.raw.fractallist);
        Properties fractalList = new Properties();
        try {
        	fractalList.load(is);
        } catch(IOException e) {
        	Log.e(LOG_KEY, "Cannot load fractal list");
        }
        FractalRegistry.getInstance().init(fractalList);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
        view = (FractalView) findViewById(R.id.fractalView);
 	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d(LOG_KEY, "onCreateContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(LOG_KEY, "onCreateOptionsMenu");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(LOG_KEY, "onOptionsItemSelected: " + item.getItemId());
	    switch (item.getItemId()) {
	    case R.id.exit:
	    	Log.d(LOG_KEY, "Exit menu item pressed");
	        System.exit(0);
	        return true;
	    case R.id.save:
	    	Log.d(LOG_KEY, "Save menu item pressed");
	        return attemptSave();
	    case R.id.options:
	    	Log.d(LOG_KEY, "Options menu item pressed");
	    	Intent intent = new Intent(this, FractalListActivity.class);
	    	startActivityForResult(intent, CHOOSE_FRACTAL_CODE);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	public boolean storageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    return true;
		} else {
			return false;
		}
	}
	
	private File getFile() {
		String fileName = view.getFractal().toString() + System.currentTimeMillis() + ".jpg";
		int apiVersion = android.os.Build.VERSION.SDK_INT;
		if (apiVersion >= android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			File file = new File(this.getApplicationContext().getExternalFilesDir(null), fileName);
			return file;
		} else {
			File dir = Environment.getExternalStorageDirectory();
			String path = dir.getAbsolutePath() + "/Android/data/com.drabek.fractal/files/" + fileName;
			File file = new File(path);
			return file;
		}
	}
	
	public boolean attemptSave() {
		Log.d(LOG_KEY, "attemptSave");
		boolean b = false;
		if (storageAvailable()) {
			Log.d(LOG_KEY, "storage available");
			File file = getFile();
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file, false);
			} catch(FileNotFoundException e) {
				Log.e(LOG_KEY, "File not found: " + e);
			}
			b = view.saveBitmap(fos);
			try {
				fos.close();
			} catch(IOException e) {
				Log.w(LOG_KEY, "Cannot close stream: " + e);
			}
		} 
		return b;
	}
	
	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		Log.d(LOG_KEY, "onCreatePanelMenu");
		return super.onCreatePanelMenu(featureId, menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		Log.d(LOG_KEY, "onMenuOpened");
		return super.onMenuOpened(featureId, menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_FRACTAL_CODE) {
			Class<Fractal> clazz = (Class<Fractal>)data.getSerializableExtra(FractalListActivity.EXTRA_KEY);
			Fractal fractal = null;
			try {
				fractal = (Fractal)clazz.newInstance();
				Log.d(LOG_KEY, fractal.getName() + " received");
				view.setFractal(fractal);
				view.invalidate();
			} catch(IllegalAccessException e) {
				Log.e(LOG_KEY, "Exception accessing fractal class: " + e);
			} catch (InstantiationException e) {
				Log.e(LOG_KEY, "Exception instatiating fractal: " + e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
 }