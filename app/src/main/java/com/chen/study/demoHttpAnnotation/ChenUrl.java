package com.chen.study.demoHttpAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by PengChen on 2017/9/29.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ChenUrl {
    String value();
    ChenMethods method() default ChenMethods.GET;
    String[] heads() default {};
    long timeOut() default 1000*30;
}
