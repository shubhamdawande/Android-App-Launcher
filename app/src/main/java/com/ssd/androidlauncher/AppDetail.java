package com.ssd.androidlauncher;

import android.graphics.drawable.Drawable;

class AppDetail {

    CharSequence name; // app: label, contact: name
    CharSequence info; // app: null, contact: number
    Drawable icon;     // app: app icon, contact: fixed
    String type;       // type: application/contact
    CharSequence packageName; // app: package name
    Drawable callIcon; // app: null, contact: call icon
}