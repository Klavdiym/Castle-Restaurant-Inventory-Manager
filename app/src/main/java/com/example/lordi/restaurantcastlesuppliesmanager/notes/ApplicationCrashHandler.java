package com.example.lordi.restaurantcastlesuppliesmanager.notes;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Class to handle application crashes.
 */
public class ApplicationCrashHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public static void installHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof ApplicationCrashHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new ApplicationCrashHandler());
        }
    }

    private ApplicationCrashHandler() {
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        Log.e("CRASH", getStackTrace(e));
        Log.e("CRASH", e.toString());

        defaultHandler.uncaughtException(t, e);
    }

    private String getStackTrace(Throwable e) {
        final Writer sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stacktrace = sw.toString();
        pw.close();
        return stacktrace;
    }
}