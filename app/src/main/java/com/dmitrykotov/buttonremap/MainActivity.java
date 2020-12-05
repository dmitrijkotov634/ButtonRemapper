package com.dmitrykotov.buttonremap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Adapter;
import java.util.Map;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityEvent;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {
	public SharedPreferences keys;

    public EditText source;
    public Spinner modified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		keys = getSharedPreferences("keys", Context.MODE_PRIVATE);

        source = findViewById(R.id.source);
        modified = findViewById(R.id.modified);

        source.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    modified.setSelection(keys.getInt(s.toString(), 0));
                }
            });

        modified.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> p1, View item, int position, long selectedId) {

                    SharedPreferences.Editor editor = keys.edit();
                    if (modified.getSelectedItemPosition() == 0)
                        editor.remove(source.getText().toString());
                    else
                        editor.putInt(source.getText().toString(), modified.getSelectedItemPosition());
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        if (!checkAccess()) {
            Toast.makeText(getApplicationContext(), "Выберите ButtonRemap", 1000).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }

    public boolean checkAccess() {
        String string = getString(R.string.accessibilityservice_id);
        for (AccessibilityServiceInfo id : ((AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE)).getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)) {
            if (string.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        source.setText(String.valueOf(keyCode));
        return true;
	}
}
