package com.chen.study.demoHttpAnnotation;

/**
 * Created by PengChen on 2017/9/30.
 */

public interface ChenInterceptor {
    void onInvokerBefore(ChenMethodContext context);
    void onInvokerAfter(ChenMethodContext context);
}
