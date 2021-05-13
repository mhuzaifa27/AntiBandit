package com.example.finalapp;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/* Trick for Mi and Huaway devices
   */

public class FakeAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() {

    }
}
