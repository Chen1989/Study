package com.chen.study;

import android.app.Application;

import com.chen.study.dynamicProxy.IChenSubject;
import com.chen.study.dynamicProxy.ProxyHandler;
import com.chen.study.dynamicProxy.RealChenSubject;

import java.lang.reflect.Proxy;

/**
 * Created by PengChen on 2017/9/4.
 */

public class ChenApplication extends Application {

    @Override
    public void onCreate() {
        RealChenSubject real = new RealChenSubject();
        IChenSubject proxySubject = (IChenSubject) Proxy.newProxyInstance(IChenSubject.class.getClassLoader(),
                new Class[]{IChenSubject.class},
                new ProxyHandler(real));

        proxySubject.doSomething("测试的");

        super.onCreate();
    }
}
