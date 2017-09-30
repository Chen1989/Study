package com.chen.study.demoHttpAnnotation;

/**
 * Created by PengChen on 2017/9/30.
 */

@ChenBaseUrl(value = "http://m.baidu.com/{id}")
public interface ChenBookApi {
    @ChenMethod(value = ChenMethods.POST)
    String search(@ChenPath("id") String id,@ChenParams("q")String q);
}
