package com.chen.study.util;

import android.util.Log;

/**
 * Created by PengChen on 2017/9/12.
 */

public class LogUtil {
    private static boolean showLog = true;
    private static String TAG = "ChenLog";
    public static void setDebug(boolean showLog) {
        if (showLog)
            Log.e(TAG, "open debug:" + showLog);
        LogUtil.showLog = showLog;
    }

    public static int v(String msg) {
        if (!showLog)
            return 0;
        return Log.v(TAG, msg);
    }

    public static int v(String msg, Throwable tr) {
        if (!showLog)
            return 0;
        return Log.v(TAG, msg, tr);
    }

    public static int d(String msg) {
        if (!showLog)
            return 0;
        return Log.d(TAG, msg);
    }

    public static int d(String msg, Throwable tr) {
        if (!showLog)
            return 0;
        return Log.d(TAG, msg, tr);
    }

    public static int i(String msg) {
        if (!showLog)
            return 0;
        return Log.i(TAG, msg);
    }

    public static int i(String msg, Throwable tr) {
        if (!showLog)
            return 0;
        return Log.i(TAG, msg, tr);
    }

    public static int w(String msg) {
        if (!showLog)
            return 0;
        return Log.w(TAG, msg);
    }

    public static int w(String msg, Throwable tr) {
        if (!showLog)
            return 0;
        return Log.w(TAG, msg, tr);
    }

    public static int w(Throwable tr) {
        if (!showLog)
            return 0;
        return Log.w(TAG, tr);
    }

    public static int e(String msg) {
        if (!showLog)
            return 0;
        return Log.e(TAG, msg);
    }

    public static int e(Throwable tr) {
        if (!showLog)
            return 0;
        return Log.e(TAG, tr.getMessage(), tr);
    }

    public static int e(String msg, Throwable tr) {
        if (!showLog)
            return 0;
        return Log.e(TAG, msg, tr);
    }
}
