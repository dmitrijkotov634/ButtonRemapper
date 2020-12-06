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
import android.media.AudioManager;

public class ButtonService extends AccessibilityService {
	public static String currentPackageName = "com.dmitrykotov.buttonremap";

    public SharedPreferences keys;
    public AudioManager mAudioManager;
    
    @Override
    protected void onServiceConnected() {
        keys = getSharedPreferences("keys", Context.MODE_PRIVATE);

        super.onServiceConnected();
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.flags = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;

		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		setServiceInfo(info);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

	public void onAccessibilityEvent(AccessibilityEvent event) {
    }

	protected boolean onKeyEvent(KeyEvent event) {
        int modified = keys.getInt(String.valueOf(event.getKeyCode()), 0);

		if (modified == 0) {
            return super.onKeyEvent(event);
        } else {
            if (keys.getBoolean("locked", false)) return false;
            if (event.getAction() == event.ACTION_DOWN) {
                switch (modified) {
                    case 1:
                        performGlobalAction(GLOBAL_ACTION_BACK);
                        break;
                    case 2:
                        performGlobalAction(GLOBAL_ACTION_RECENTS);
                        break;
                    case 3: 
                        mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
                        mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
                        break;
                    case 4:
                        mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
                        mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
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
