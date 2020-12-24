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
import android.widget.CheckBox;

public class MainActivity extends Activity {
	public SharedPreferences keys;

    public EditText sourceId;
    public Spinner spinEffect;
    public Spinner spinAction;
    public CheckBox chkReplace;

    public boolean save = false;
    
    ButtonService bService;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        keys = getSharedPreferences("keys", Context.MODE_PRIVATE);
        
        sourceId = findViewById(R.id.sourceId);
        spinEffect = findViewById(R.id.spinEffect);
        spinAction = findViewById(R.id.spinAction);
        chkReplace = findViewById(R.id.chkReplace);

        sourceId.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {};

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {};

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    spinEffect.setSelection(keys.getInt(sourceId.getText().toString() + spinAction.getSelectedItemPosition(), 0));
                    chkReplace.setChecked(keys.getBoolean(sourceId.getText().toString() + "i", false));
                }
            });

        spinAction.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> p1, View item, int position, long selectedId) {
                    spinEffect.setSelection(keys.getInt(sourceId.getText().toString() + spinAction.getSelectedItemPosition(), 0));
                    chkReplace.setChecked(keys.getBoolean(sourceId.getText().toString() + "i", false));
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {};
            });

        spinEffect.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> p1, View item, int position, long selectedId) {
                    updateKey(spinEffect);
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                }
            });
        
        if (!checkAccess()) {
            Toast.makeText(getApplicationContext(), getString(R.string.request), 1000).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }

    public void onStart() {
        super.onStart();
        switchState(false);
    }

    public void onPause() {
        super.onPause();
        switchState(true);
    }
    
    public void switchState(boolean state) {
        bService = ButtonService.getSharedInstance();
        if (bService != null) bService.state = state;
    }
    
    public void updateKey(View view) {
        SharedPreferences.Editor editor = keys.edit();
        if (spinEffect.getSelectedItemPosition() == 0) {
            editor.remove(sourceId.getText().toString() + spinAction.getSelectedItemPosition());
            editor.remove(sourceId.getText().toString() + "i");
        } else {
            editor.putInt(sourceId.getText().toString() + spinAction.getSelectedItemPosition(), spinEffect.getSelectedItemPosition());
            editor.putBoolean(sourceId.getText().toString() + "i", chkReplace.isChecked());
        }
        editor.apply();
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        sourceId.setText(String.valueOf(keyCode));
        return true;
	}
    
    public boolean checkAccess() {
        String string = getString(R.string.accessibility_service_id);
        for (AccessibilityServiceInfo id : ((AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE)).getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)) {
            if (string.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }
}
