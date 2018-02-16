package com.draabek.fractal;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * A login screen that offers login via email/password.
 */
public class SaveBitmapActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String LOG_KEY = SaveBitmapActivity.class.getName();
    private static final int REQUEST_WRITE = 1;
    private static final int REQUEST_WALLPAPER = 2;

    // UI references.
    private RadioGroup radioGroup;
    EditText filenameEdit;
    private File bitmapFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_bitmap);
        radioGroup = (RadioGroup) findViewById(R.id.save_bitmap_radio_group);
        filenameEdit = (EditText) findViewById(R.id.bitmap_filename);
        Button button = (Button) findViewById(R.id.save_bitmap_ok_button);
        bitmapFile = new File(this.getIntent().getStringExtra(getString(R.string.intent_extra_bitmap_file)));
        File suggestedPath = getFile();
        filenameEdit.getText().append(suggestedPath.getAbsolutePath());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.bitmap_filename_radio) {
                    if (handlePermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.save_bitmap_storage_write_rationale), REQUEST_WRITE)) {
                        saveToFile();
                    }
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.bitmap_set_as_background_radio) {
                   // handlePermissions(Manifest.permission.SET_WALLPAPER,
                     //       getString(R.string.save_bitmap_set_wallpaper_rationale), REQUEST_WALLPAPER);
                    setAsWallpaper();
                } else {
                    Log.e(this.getClass().getName(), "Unknown radio button in SaveBitmapActivity");
                }
            }
        });
    }

    private void saveToFile() {
        String filename = filenameEdit.getText().toString();
        String path = filename.substring(0, filename.lastIndexOf("/")-1);
        File dir = new File(path);
        if (!storageAvailable()) {
            Log.e(LOG_KEY, "External storage not available");
            return;
        }
        if ((!dir.exists()) && (!dir.mkdirs())) {
            Log.e(LOG_KEY, "Directory specified does not exist and could not be created");
            return;
        }
        File f = new File(filename);
        saveBitmap(bitmapFile, f);
        Toast.makeText(SaveBitmapActivity.this, getString(R.string.save_bitmap_success_toast)
                        + f.getAbsolutePath(),
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void setAsWallpaper() {
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(SaveBitmapActivity.this);
        try {
            myWallpaperManager.setStream(new FileInputStream(bitmapFile));
        } catch (IOException e) {
            // Just be ugly in the logcat
            e.printStackTrace();
            finish();
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToFile();
            } else {
                Toast.makeText(SaveBitmapActivity.this, getString(R.string.save_bitmap_storage_write_rationale),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_WALLPAPER) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setAsWallpaper();
            } else {
                Toast.makeText(SaveBitmapActivity.this, getString(R.string.save_bitmap_set_wallpaper_rationale),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private boolean handlePermissions(final String permission, final String rationale, final int code) {
        if (ContextCompat.checkSelfPermission(SaveBitmapActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveBitmapActivity.this,permission)) {
                //Just display a Toast
                Toast.makeText(SaveBitmapActivity.this, rationale, Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(() -> ActivityCompat.requestPermissions(SaveBitmapActivity.this,
                        new String[]{permission}, code), Toast.LENGTH_SHORT);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SaveBitmapActivity.this,
                        new String[]{permission}, code);
            }
            return false;
        }
        return true;
    }

    private void saveBitmap(File bitmapTempFile, File path) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(bitmapTempFile).getChannel();
            destChannel = new FileOutputStream(path).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean storageAvailable() {
        String state = Environment.getExternalStorageState();
        // We can read and write the media
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    private File getFile() {
        String fileName = FractalRegistry.getInstance().getCurrent().toString() + System.currentTimeMillis() + ".jpg";
        return new File(getExternalStoragePublicDirectory(DIRECTORY_PICTURES).getAbsolutePath(), fileName);
    }

}

