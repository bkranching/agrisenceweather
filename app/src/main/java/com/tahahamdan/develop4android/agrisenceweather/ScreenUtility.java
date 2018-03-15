package com.tahahamdan.develop4android.agrisenceweather;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;


public class ScreenUtility {

    private float dpWidth;
    private float dpHeight;
    public ScreenUtility(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = activity.getResources().getDisplayMetrics().density;
        dpWidth = outMetrics.widthPixels / density;
        dpHeight = outMetrics.heightPixels / density;
    }

    public float getWidth() {
        return dpWidth;
    }

    public float getShortestWidth() {
        return Math.min(dpWidth,dpHeight);
    }
}