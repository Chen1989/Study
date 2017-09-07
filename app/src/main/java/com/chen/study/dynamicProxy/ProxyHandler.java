package com.chen.study.dynamicProxy;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by PengChen on 2017/9/7.
 */

public class ProxyHandler implements InvocationHandler {
    private IChenSubject chenSubject;

    public ProxyHandler(IChenSubject iChenSubject) {
        chenSubject = iChenSubject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d("DynamicProxy", "before call ProxyHandler invoke method " + method.getName());
        Object result = method.invoke(chenSubject, args);
        Log.d("DynamicProxy", "after call ProxyHandler invoke method " + method.getName());
        return result;
    }
}
