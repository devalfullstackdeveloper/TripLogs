package com.triplogs.helper;

import android.util.Log;

/**
 * Created by jigar on 27/07/2020.
 */

public class LogClass {

    private static boolean mLoggingEnabled = true;

    public static void enableLogging(boolean enabled) {
        mLoggingEnabled = enabled;
    }

    public static void v(String tag, String msg) {
        if (mLoggingEnabled) {
            Log.v(tag, msg); //make sure to call android.util here because otherwise you will have a recursion
        }
    }

    public static void d(String tag, String msg) {
        if (mLoggingEnabled) {
            Log.d(tag, msg); //make sure to call android.util here because otherwise you will have a recursion
        }
    }

    public static void i(String tag, String msg) {
        if (mLoggingEnabled) {
            Log.i(tag, msg); //make sure to call android.util here because otherwise you will have a recursion
        }
    }

    public static void w(String tag, String msg) {
        if (mLoggingEnabled) {
            Log.w(tag, msg); //make sure to call android.util here because otherwise you will have a recursion
        }
    }

    public static void e(String tag, String msg) {
        if (mLoggingEnabled) {
            Log.e(tag, msg); //make sure to call android.util here because otherwise you will have a recursion
        }
    }

    public static void e(String tag, String msg, Throwable error) {
        if (mLoggingEnabled) {
            Log.e(tag, msg, error); //make sure to call android.util here because otherwise you will have a recursion
        }
    }
}
