package com.chen.study.httpOk;

import com.chen.study.util.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by PengChen on 2017/11/24.
 */

public class HttpSample {
    private static String urlString = "http://www.jianshu.com/p/1873287eed87";

    //异步
    public static void asynchronousHttpGet() {
        OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(urlString).build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("测试使用的");
//                response.cacheResponse().toString();
//                response.networkResponse().toString();
            }
        });
    }

    //同步
    public static void synchronousHttpGet() {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(urlString).build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("测试使用的");
            }
        });
    }
}
