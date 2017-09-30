package com.chen.study.demoHttpAnnotation;

/**
 * Created by PengChen on 2017/9/29.
 */

public class ChenNetException extends Exception {
    public ChenNetException(String message,int code) {
        super(message+" code:"+code);
    }
}
