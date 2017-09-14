package com.chen.study.demoHttpAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by PengChen on 2017/9/13.
 */

@Target(ElementType.METHOD)//元注解，标记作用范围在方法中
@Retention(RetentionPolicy.RUNTIME)//元注解，标记注解生命周期，有运行时，class
public @interface HttpAnnotation {
    String methodType() default "GET";
}
