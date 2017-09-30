package com.chen.study.demoHttpAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by PengChen on 2017/9/29.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Inherited
public @interface ChenQuery {
    String value();
    ChenMethods method() default ChenMethods.GET;
    String[] fields() default {};
    String[] heads() default {};
    long timeOut() default 1000*30;
}
