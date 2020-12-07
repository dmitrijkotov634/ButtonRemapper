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
import android.media.AudioManager;
import android.util.Log;
import java.util.HashMap;

public class ButtonService extends AccessibilityService {
	public static String currentPackageName = "com.dmitrykotov.buttonremap";

    public SharedPreferences keys;
    public AudioManager mAudioManager;
    HashMap<Integer, Long> tempKey = new HashMap<Integer, Long>();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.flags = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;

		info.eventTypes = 0;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		setServiceInfo(info);
        
        keys = getSharedPreferences("keys", Context.MODE_PRIVATE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

	public void onAccessibilityEvent(AccessibilityEvent event) {}

	protected boolean onKeyEvent(KeyEvent event) {
        if (keys.getBoolean("lock", false)) return false;
        if (event.getAction() == event.ACTION_DOWN) {
            tempKey.put(event.getKeyCode(), SystemClock.uptimeMillis());
            if (keys.getBoolean(String.valueOf(event.getKeyCode()) + "i", true)) return false;
            else return true;
        } else {
            int action = keys.getInt(String.valueOf(event.getKeyCode()) + (SystemClock.uptimeMillis() - tempKey.get(event.getKeyCode()) > 1000 ? "1" : "0"), 0);
            tempKey.remove(event.getKeyCode());
            switch (action) {
                case 0:
                    return super.onKeyEvent(event);
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
                case 5:
                    mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                    mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                    break;
                case 6:
                    performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
                    break;
                case 7:
                    performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS);
                    break;
                case 8:
                    performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    break;
                case 9:
                    performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
                    break;
                case 10:
                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                    break;
            }
        }
        return true
            ;}

    @Override
    public void onInterrupt() {

    }
}
