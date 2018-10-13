package com.lixin.Util;

import android.util.Log;

/**
 * Created by li on 2017/12/16.
 */

public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    //Log'sswitch
    public static int level = VERBOSE; //

    public static void v(String tag, String log) {
        if (level <= VERBOSE) {
            Log.v(tag, log);
        }
    }

    public static void d(String tag, String log) {
        if (level <= DEBUG) {
            Log.v(tag, log);
        }
    }

    public static void i(String tag, String log) {
        if (level <= INFO) {
            Log.v(tag, log);
        }
    }

    public static void w(String tag, String log) {
        if (level <= WARN) {
            Log.v(tag, log);
        }
    }

    public static void e(String tag, String log) {
        if (level <= ERROR) {
            Log.v(tag, log);
        }
    }

}