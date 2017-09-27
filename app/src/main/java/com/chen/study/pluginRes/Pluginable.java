package com.chen.study.pluginRes;

import android.os.Bundle;

/**
 * Created by PengChen on 2017/9/19.
 */

public interface Pluginable {
    void onCreate(Bundle savedInstanceState);

    void onRestart();


    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
