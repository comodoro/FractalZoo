package com.draabek.fractal;

import android.app.WallpaperManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * A login screen that offers login via email/password.
 */
public class SaveBitmapActivity extends AppCompatActivity {

    private static final String LOG_KEY = SaveBitmapActivity.class.getName();
    // UI references.
    private RadioGroup radioGroup;
    EditText filenameEdit;
    private Button button;
    private File bitmapFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_bitmap);
        radioGroup = (RadioGroup) findViewById(R.id.save_bitmap_radio_group);
        filenameEdit = (EditText) findViewById(R.id.bitmap_filename);
        button = (Button) findViewById(R.id.save_bitmap_ok_button);
        bitmapFile = new File(this.getIntent().getStringExtra(getString(R.string.intent_extra_bitmap_file)));
        File suggestedPath = getFile();
        filenameEdit.getText().append(suggestedPath.getAbsolutePath());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.bitmap_filename_radio) {
                    String filename = filenameEdit.getText().toString();
                    File f = new File(filename);
                    saveBitmap(bitmapFile, f);
                    finish();
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.bitmap_set_as_background_radio) {
                    WallpaperManager myWallpaperManager = WallpaperManager.getInstance(SaveBitmapActivity.this);
                    try {
                        myWallpaperManager.setStream(new FileInputStream(bitmapFile));
                    } catch (IOException e) {
                        // Tjust be ugly in the logcat
                        e.printStackTrace();
                    }
                } else {
                    Log.e(this.getClass().getName(), "Unknown radio button in SaveBitmapActivity");
                }
            }
        });
    }

    private boolean saveBitmap(File bitmapTempFile, File path) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(bitmapTempFile).getChannel();
            destChannel = new FileOutputStream(path).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                sourceChannel.close();
                destChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
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

}

