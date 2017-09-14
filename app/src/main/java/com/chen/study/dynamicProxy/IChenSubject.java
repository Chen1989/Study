package com.chen.study.dynamicProxy;

import com.chen.study.demoHttpAnnotation.HttpAnnotation;

/**
 * Created by PengChen on 2017/9/7.
 */

public interface IChenSubject {
    @HttpAnnotation(methodType = "POST")
    void doSomething(String something);
}
