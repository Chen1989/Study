package com.chen.study.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.chen.study.util.LogUtil;

/**
 * Created by PengChen on 2017/12/22.
 */

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            LogUtil.d("监听到网络变化AAAA");
        }
    }
}
