package com.dmitrykotov.buttonremap;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.content.SharedPreferences;
import android.os.*;
import android.view.accessibility.*;
import android.content.*;
import android.content.pm.*;
import android.view.*;
import android.widget.Toast;

public class ButtonService extends AccessibilityService {
	public static String currentPackageName = "com.dmitrykotov.buttonremap";

    public SharedPreferences keys;

    @Override
    protected void onServiceConnected() {
        keys = getSharedPreferences("keys", Context.MODE_PRIVATE);

        super.onServiceConnected();
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.flags = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;

		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		setServiceInfo(info);
    }

	public void onAccessibilityEvent(AccessibilityEvent event) {
    }

	protected boolean onKeyEvent(KeyEvent event) {
        int modified = keys.getInt(String.valueOf(event.getKeyCode()), 0);

		if (modified == 0) {
            return super.onKeyEvent(event);
        } else {
            if (event.getAction() == event.ACTION_DOWN) {
                switch (modified) {
                    case 1:
                        performGlobalAction(GLOBAL_ACTION_BACK);
                        break;
                    case 2:
                        performGlobalAction(GLOBAL_ACTION_RECENTS);
                        break;

                }
            }
            return true;
        }
	}

    @Override
    public void onInterrupt() {

    }
}
