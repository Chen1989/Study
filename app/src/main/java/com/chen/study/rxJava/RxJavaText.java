package com.chen.study.rxJava;

import com.chen.study.util.LogUtil;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.AsyncSubject;

/**
 * Created by PengChen on 2017/10/13.
 */

public class RxJavaText {

    public static void asyncSubject() {
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext("asyncSubject1");
        asyncSubject.onNext("asyncSubject2");
        asyncSubject.onNext("asyncSubject3");
        asyncSubject.onComplete();
        asyncSubject.subscribe(new Observer<String>() {

            @Override
            public void onError(Throwable e) {

                LogUtil.d("asyncSubject onError");  //不输出（异常才会输出）
            }

            @Override
            public void onComplete() {
                LogUtil.d("asyncSubject onCompleted");  //输出 asyncSubject onCompleted
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(String s) {

                LogUtil.d("asyncSubject:"+s);  //输出asyncSubject:asyncSubject3
            }
        });
    }
}
