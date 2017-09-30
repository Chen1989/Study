package com.chen.study.demoHttpAnnotation;

/**
 * Created by PengChen on 2017/9/29.
 */

public enum ChenMethods {
    GET("GET"),POST("POST"),DELETE("DELETE"),PUT("PUT"),HEAD("HEAD");

    private final String value;

    ChenMethods(String value) {
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}
