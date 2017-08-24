package com.draabek.fractal;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.draabek.fractal.fractal.BitmapDrawFractal;
import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;
import com.draabek.fractal.fractal.GLSLFractal;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/*
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

*/

public class FractalActivity extends AppCompatActivity {
    private static final String LOG_KEY = FractalActivity.class.getName();
    public static final int CHOOSE_FRACTAL_CODE = 1;
    private MyGLSurfaceView myGLSurfaceView;
    private FractalCpuView cpuView;
    private FractalViewHandler currentView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    ///private GoogleApiClient client;

    public FractalActivity() {
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Reader jsonReader = new InputStreamReader(this.getResources().openRawResource(R.raw.fractallist));
        JsonParser parser = new JsonParser();
        JsonElement fractalElement = parser.parse(jsonReader);
        JsonArray fractalArray = fractalElement.getAsJsonArray();
        FractalRegistry.getInstance().init(this, fractalArray);
        jsonReader = new InputStreamReader(this.getResources().openRawResource(R.raw.settings));
        JsonObject jsonObject = parser.parse(jsonReader).getAsJsonObject();
        //ugh
        FractalRegistry.getInstance().setCurrent(
                FractalRegistry.getInstance()
                        .get(jsonObject.get("current").getAsString())
        );
        setContentView(R.layout.main);
        myGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.fractalGlView);
        cpuView = (FractalCpuView) findViewById(R.id.fractalCpuView);
        if (this.getSharedPreferences("", MODE_PRIVATE).getBoolean("prefs_use_gpu", true)) {
            currentView = myGLSurfaceView;
        } else {
            currentView = cpuView;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_KEY, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_KEY, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.fractalList:
                Log.d(LOG_KEY, "Fractal list menu item pressed");
                Intent intent = new Intent(this, FractalListActivity.class);
                startActivity(intent);
                return true;
            case R.id.save:
                Log.d(LOG_KEY, "Save menu item pressed");
                return attemptSave();
            case R.id.options:
                Log.d(LOG_KEY, "Options menu item pressed");
                Intent intent2 = new Intent(this, FractalPreferenceActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean storageAvailable() {
        String state = Environment.getExternalStorageState();
        // We can read and write the media
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private File getFile() {
        String fileName = FractalRegistry.getInstance().getCurrent().toString() + System.currentTimeMillis() + ".jpg";
        int apiVersion = Build.VERSION.SDK_INT;
        if (apiVersion >= Build.VERSION_CODES.ECLAIR_MR1) {
            return new File(this.getApplicationContext().getExternalFilesDir(null), fileName);
        } else {
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getAbsolutePath() + "/Android/data/com.drabek.fractal/files/" + fileName;
            return new File(path);
        }
    }

    public boolean attemptSave() {
        Log.d(LOG_KEY, "attemptSave");
        boolean b = false;
        if (storageAvailable()) {
            Log.d(LOG_KEY, "storage available");
            File file = getFile();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file, false);
            } catch (FileNotFoundException e) {
                Log.e(LOG_KEY, "File not found: " + e);
                return false;
            }
            b = currentView.saveBitmap(fos);
            try {
                fos.close();
            } catch (IOException e) {
                Log.w(LOG_KEY, "Cannot close stream: " + e);
            }
        }
        return b;
    }
/*
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
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_FRACTAL_CODE) {
            try {
                Fractal f = FractalRegistry.getInstance().getCurrent();
                Log.d(LOG_KEY, f.getName() + " is current");
                if (f instanceof BitmapDrawFractal) {
                    currentView = cpuView;
                    myGLSurfaceView.setVisibility(View.INVISIBLE);
                } else if (f instanceof GLSLFractal) {
                    currentView = myGLSurfaceView;
                    cpuView.setVisibility(View.INVISIBLE);
                }
                currentView.setVisibility(View.VISIBLE);
                currentView.invalidate();
            } catch (Exception e) {
                Log.e(LOG_KEY, "Exception loading fractal: " + e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
/*    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Fractal Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        ///client.connect();
        ///AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        ///AppIndex.AppIndexApi.end(client, getIndexApiAction());
        ///client.disconnect();
    }
}