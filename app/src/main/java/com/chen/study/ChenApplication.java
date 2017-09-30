package com.chen.study;

import android.app.Application;

import com.chen.study.demoHttpAnnotation.ChenBookApi;
import com.chen.study.demoHttpAnnotation.ChenBuilder;
import com.chen.study.demoHttpAnnotation.ChenHttpService;
import com.chen.study.demoHttpAnnotation.ChenInterceptor;
import com.chen.study.demoHttpAnnotation.ChenMethodContext;
import com.chen.study.demoHttpAnnotation.HttpAnnotation;
import com.chen.study.dynamicProxy.IChenSubject;
import com.chen.study.dynamicProxy.ProxyHandler;
import com.chen.study.dynamicProxy.RealChenSubject;
import com.chen.study.net.BaseHttp;
import com.chen.study.util.LogUtil;

import java.lang.reflect.Method;
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
        ClassLoader loader = getClassLoader();
        LogUtil.d("loader = " + loader);
        getMethod();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testHttpAnnotation();
            }
        }).start();


        super.onCreate();
    }

    public void getMethod() {

        Method[] methods = BaseHttp.class.getDeclaredMethods();

        LogUtil.d("methods length = " + methods.length);
        for (Method method : methods) {
            if (method.isAnnotationPresent(HttpAnnotation.class)) {
                LogUtil.d(method.getName());
            }
        }
    }

    private void testHttpAnnotation() {
        ChenBookApi api= ChenBuilder.builder(ChenBookApi.class,new ChenHttpService().addInteropter(new ChenInterceptor() {
            @Override
            public void onInvokerBefore(ChenMethodContext context) {
                System.out.println("onInvokerBefore");
            }

            @Override
            public void onInvokerAfter(ChenMethodContext context) {
                System.out.println("onInvokerAfter");
            }
        }));
        System.out.println(api.search("image","abc"));
    }
}
