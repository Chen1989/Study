package com.chen.study.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.WindowManager;

/**
 * Created by PengChen on 2017/11/14.
 */

public class Utils {
    //网络是否可用
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    /*
 *
 * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
 */
    public static void setBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    public static void updateVoice(Context context, int progress, boolean isMute) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (isMute) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            seekbarVoice.setProgress(0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
//            seekbarVoice.setProgress(progress);
//            currentVoice = progress;
        }
    }

    public static void test() {
        AsyncTask<String, Integer, Long> asyncTask = new AsyncTask<String, Integer, Long>() {
            @Override
            protected Long doInBackground(String... params) {
                return null;
            }
        };
        asyncTask.execute();
    }

}
