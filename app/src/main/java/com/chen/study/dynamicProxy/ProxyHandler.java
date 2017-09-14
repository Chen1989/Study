package com.chen.study.dynamicProxy;

import android.util.Log;

import com.chen.study.demoHttpAnnotation.HttpAnnotation;
import com.chen.study.util.LogUtil;

import java.lang.annotation.Annotation;
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
        Annotation[] methods = method.getAnnotations();
//        HttpAnnotation httpAnnotation = method.getAnnotation(HttpAnnotation.class);
//        LogUtil.d(httpAnnotation.methodType());
        for (Annotation annotation : methods)
        {
            if (annotation instanceof HttpAnnotation)
            {
                String result = ((HttpAnnotation) annotation).methodType();
                LogUtil.d(result);
            }
        }
        Log.d("DynamicProxy", "before call ProxyHandler invoke method " + method.getName());
        Object result = method.invoke(chenSubject, args);
        Log.d("DynamicProxy", "after call ProxyHandler invoke method " + method);
        return result;
    }
}
