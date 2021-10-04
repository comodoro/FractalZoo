package com.draabek.fractal.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.draabek.fractal.R;
import com.draabek.fractal.fractal.FractalRegistry;

import java.util.Locale;
import java.util.Map;

public class FractalParametersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fractal_parameters);
        TextView heading = findViewById(R.id.fractal_parameters_text);
        Map<String, Float> currentParameters = FractalRegistry.getInstance().getCurrent().getParameters();
        RelativeLayout relativeLayout = findViewById(R.id.layout_parameters);
        int lastId = heading.getId();
        int width = getWindowManager().getDefaultDisplay().getWidth()/2;
        for (String parameter : currentParameters.keySet()) {
            TextView label = new TextView(this);
            label.setText(parameter);
            int id = (int) (Math.random() * Integer.MAX_VALUE);
            label.setId(id);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            label.setWidth(width);
            label.setEllipsize(TextUtils.TruncateAt.END);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.BELOW, lastId);
            relativeLayout.addView(label,layoutParams);
            EditText editText = new EditText(this);
            editText.setText(String.format(Locale.getDefault(), "%f", currentParameters.get(parameter)));
            editText.setWidth(width);
            editText.setId ((int) (Math.random() * Integer.MAX_VALUE));
            editText.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (!hasFocus) {
                    String newText = ((EditText)v).getText().toString();
                    try {
                        float f = Float.parseFloat(newText);
                        currentParameters.put(parameter, f);
                    } catch (NumberFormatException e) {
                        editText.setText(String.format(Locale.getDefault(), "%f", currentParameters.get(parameter)));
                    }
                }
            });
            layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.RIGHT_OF, id);
            layoutParams.addRule(RelativeLayout.BELOW, lastId);
            relativeLayout.addView(editText,layoutParams);
            lastId = id;
        }
        Button button = new Button(this);
        button.setText(R.string.parameters_ok_button);
        button.setOnClickListener((View v) -> finish());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.BELOW, lastId);
        layoutParams.setMargins(10,10,10,10);
        relativeLayout.addView(button,layoutParams);
    }
}
