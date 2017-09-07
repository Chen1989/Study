package com.chen.study.dynamicProxy;

import android.util.Log;

/**
 * Created by PengChen on 2017/9/7.
 */

public class RealChenSubject implements IChenSubject {
    @Override
    public void doSomething(String something) {
        Log.d("DynamicProxy", "call doSomething method " + something);
    }
}
