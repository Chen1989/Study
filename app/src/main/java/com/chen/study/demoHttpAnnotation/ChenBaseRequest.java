package com.chen.study.demoHttpAnnotation;

/**
 * Created by PengChen on 2017/9/30.
 */

public abstract class ChenBaseRequest implements ChenRequest {
    protected ChenHttpContext httpContext;

    public ChenBaseRequest(ChenHttpContext httpContext) {
        this.httpContext = httpContext;
    }
}
