package com.draabek.fractal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.draabek.fractal.fractal.FractalViewWrapper;
import com.draabek.fractal.R;
import com.draabek.fractal.fractal.RenderListener;
import com.draabek.fractal.util.AssetsReadUtils;
import com.draabek.fractal.util.Utils;
import com.draabek.fractal.canvas.FractalCpuView;
import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;
import com.draabek.fractal.gl.RenderImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_KEY = MainActivity.class.getName();
    private static final String DEFAULT_FRACTAL_NAME = "Mandelbrot";
    public static final String CURRENT_FRACTAL_KEY = "current_fractal";
    public static final int CHOOSE_FRACTAL_CODE = 1;

    Map<Class<? extends FractalViewWrapper>, FractalViewWrapper> availableViews;
    private FractalViewWrapper currentView;
    private SharedPreferences prefs;

    @BindView(R.id.indeterminateBar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FractalRegistry.getInstance().init(AssetsReadUtils.readFractalMetadata(this));
        } catch (IOException e) {
            Log.e(LOG_KEY,"Exception loading fractal metadata");
            throw new RuntimeException(e);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        FractalRegistry.getInstance().setCurrent(getLastFractal());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initAvailableViews();
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        unveilCorrectView(FractalRegistry.getInstance().getCurrent().getName());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (Utils.DEBUG) {
                Log.d(LOG_KEY, "ACTION_UP");
            }
            if (getSupportActionBar() == null) return false;
            if (getSupportActionBar().isShowing()) getSupportActionBar().hide();
            else getSupportActionBar().show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Utils.DEBUG) {
            Log.d(LOG_KEY, "onCreateOptionsMenu");
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Utils.DEBUG) {
            Log.d(LOG_KEY, "onOptionsItemSelected: " + item.getItemId());
        }
        switch (item.getItemId()) {
            case R.id.fractalList:
                if (Utils.DEBUG) {
                    Log.d(LOG_KEY, "Fractal list menu item pressed");
                }
                Intent intent = new Intent(this, FractalListActivity.class);
                intent.putExtra(FractalListActivity.INTENT_HIERARCHY_PATH,
                        getIntent().getStringExtra(FractalListActivity.INTENT_HIERARCHY_PATH));
                startActivityForResult(intent, CHOOSE_FRACTAL_CODE);
                return true;
            case R.id.save:
                if (Utils.DEBUG) {
                    Log.d(LOG_KEY, "Save menu item pressed");
                }
                return attemptSave();
            case R.id.parameters:
                if (Utils.DEBUG) {
                    Log.d(LOG_KEY, "Parameters menu item pressed");
                }
                Intent intent2 = new Intent(this, FractalParametersActivity.class);
                startActivity(intent2);
                return true;
            case R.id.options:
                if (Utils.DEBUG) {
                    Log.d(LOG_KEY, "Options menu item pressed");
                }
                Intent intent3 = new Intent(this, FractalPreferenceActivity.class);
                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean attemptSave() {
        currentView.saveBitmap();
        return true;
    }

    /**
     * Make visible correct view according to the Fractal.getViewWrapper method
     * @param newFractal Name of the current fractal
     */
    private void unveilCorrectView(String newFractal) {
        Fractal f = FractalRegistry.getInstance().get(newFractal);
        if (f == null) {
            Log.e(this.getClass().getName(), String.format("Fractal %s not found", newFractal));
            f = FractalRegistry.getInstance().get(DEFAULT_FRACTAL_NAME);
        }
        assert f != null;

        if (currentView != null) currentView.setVisibility(View.GONE);
        FractalViewWrapper available = availableViews.get(f.getViewWrapper());
        if (available == null) {
            throw new RuntimeException("No appropriate view available");
        }

        currentView = available;
        FractalRegistry.getInstance().setCurrent(f);
        if (Utils.DEBUG) {
            Log.d(LOG_KEY, f.getName() + " is current");
        }
        currentView.setVisibility(View.VISIBLE);
        currentView.clear();
        currentView.setRenderListener(new RenderListener() {
            @Override
            public void onRenderRequested() {
                Log.i(this.getClass().getName(), String.format("Rendering requested on %s",
                        FractalRegistry.getInstance().getCurrent().getName()));
                progressBar.post(() -> progressBar.setVisibility(View.VISIBLE));

            }

            @Override
            public void onRenderComplete(long millis) {
                Log.i(this.getClass().getName(), String.format("Rendering complete in %d ms", millis));
                progressBar.post(() -> {
                    if (!currentView.isRendering())
                        progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_FRACTAL_CODE) {
            try {
                String pickedFractal = data.getStringExtra(CURRENT_FRACTAL_KEY);
                unveilCorrectView(pickedFractal);
            } catch (Exception e) {
                Log.e(LOG_KEY, "Exception on fractal switch");
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Fractal getLastFractal() {
        Fractal lastFractal = FractalRegistry
            .getInstance()
            .get(prefs.getString(Utils.PREFS_CURRENT_FRACTAL_KEY, DEFAULT_FRACTAL_NAME));

        if (lastFractal == null) {
            lastFractal = FractalRegistry.getInstance().get(DEFAULT_FRACTAL_NAME);
        }

        return lastFractal;
    }

    //Put views into map where key is the view class, this is then requested from the fractal
    private void initAvailableViews() {
        RenderImageView renderImageView = findViewById(R.id.fractalGlView);
        FractalCpuView cpuView = findViewById(R.id.fractalCpuView);

        availableViews = new HashMap<>();
        availableViews.put(renderImageView.getClass(), renderImageView);
        availableViews.put(cpuView.getClass(), cpuView);
    }
}