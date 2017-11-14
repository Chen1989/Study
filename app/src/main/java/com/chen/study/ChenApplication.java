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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
//        getMethod();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                testHttpAnnotation();
//            }
//        }).start();


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
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d("asyncSubject onSubscribe onSubscribe " );  //不输出（异常才会输出）
            }

            @Override
            public void onNext(String value) {
                LogUtil.d("asyncSubject onNext " + value);  //不输出（异常才会输出）
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d("asyncSubject onError");  //不输出（异常才会输出）
            }

            @Override
            public void onComplete() {
                LogUtil.d("asyncSubject onComplete");  //不输出（异常才会输出）
            }
        };
        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                LogUtil.d("asyncSubject subscribe");  //不输出（异常才会输出）
                e.onNext("ceshi1");
                e.onNext("ceshi2");
                e.onNext("ceshi3");
                e.onComplete();
                e.onNext("ceshi4");
                e.onNext("ceshi5");
            }
        });
        observable.subscribe(observer);
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
