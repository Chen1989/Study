package com.chen.study.pluginRes;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

/**
 * Created by PengChen on 2017/9/19.
 */
public class ProxyActivity extends Activity {
    LifeCircleController mPluginController = new LifeCircleController(this);    //  用于管理代理Activity生命周倩和资源的类


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginController.onCreate(getIntent().getExtras());
    }

    @Override
    public Resources getResources() {
        Resources resources = mPluginController.getResources();
        return null != resources ? resources : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        AssetManager assets = mPluginController.getAssets();
        return null != assets ? mPluginController.getAssets() : super.getAssets();
    }

    @Override
    public ClassLoader getClassLoader() {
        ClassLoader loader = mPluginController.getClassLoader();
        return null != loader ? loader : super.getClassLoader();
    }
}
