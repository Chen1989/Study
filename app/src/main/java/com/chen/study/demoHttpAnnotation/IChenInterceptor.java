package com.chen.study.demoHttpAnnotation;

/**
 * Created by PengChen on 2017/9/29.
 */

public interface IChenInterceptor {
    void onInvokerBefore(ChenMethodContext context);
    void onInvokerAfter(ChenMethodContext context);
}
