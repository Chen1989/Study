package com.chen.study.pluginRes;

import android.content.pm.PackageInfo;
import android.content.res.Resources;

/**
 * Created by PengChen on 2017/9/19.
 */

public class PluginApk {
    public PackageInfo packageInfo;
    public Resources resources;
    public ClassLoader classLoader;

    public PluginApk(Resources resources) {
        this.resources = resources;
    }
}
