package com.example.lordi.restaurantcastlesuppliesmanager.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Objects;

public class ScreenSizeUtility {

    private int height;

    public ScreenSizeUtility(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(windowManager).getDefaultDisplay().getMetrics(metrics);
        setHeight(metrics.heightPixels);
        setWidth(metrics.widthPixels);
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    private void setWidth(int width) {
    }
}
