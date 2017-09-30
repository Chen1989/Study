package com.chen.study.demoHttpAnnotation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by PengChen on 2017/9/30.
 */

public class ChenGetRequest extends ChenBaseRequest {
    public ChenGetRequest(ChenHttpContext httpContext) {
        super(httpContext);
    }

    @Override
    public ChenResponse request(ChenMethodContext methodContext) throws IOException {

        return get(buildHttp(methodContext));
    }

    private HttpURLConnection buildHttp(ChenMethodContext methodContext) throws IOException {
        HttpURLConnection connection;
        if (methodContext.getParams() != null && methodContext.getParams().size() > 0) {
            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<String, String>> it = methodContext.getParams().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                sb.append(URLEncoder.encode(e.getKey(), ChenIOConfigs.getInstance().getEncode()));
                sb.append("=");
                sb.append(URLEncoder.encode(e.getValue(), ChenIOConfigs.getInstance().getEncode()));
                if (it.hasNext())
                    sb.append("&");
            }

            if (methodContext.getUrl().contains("?"))
                methodContext.setUrl(methodContext.getUrl() + "&" + sb.toString());
            else
                methodContext.setUrl(methodContext.getUrl() + "?" + sb.toString());
        }

        connection = httpContext.getHttpURLConnection(methodContext.getUrl(), (int) methodContext.getTimeOut(), methodContext.getMethod());
        if (methodContext.getHead() != null) {
            String[] heads = methodContext.getHead();
            for (int i = 0; i < heads.length; i++) {
                if (heads[i] != null) {
                    String[] kv = heads[i].split("[=]");
                    if (kv.length > 1)
                        connection.addRequestProperty(kv[0], kv[1]);
                }
            }
        }

        return connection;
    }

    private ChenResponse get(HttpURLConnection connection) throws IOException {
        connection.connect();
        int code = connection.getResponseCode();
        if (code < 300) {
            return new ChenResponse(code, connection.getInputStream(), connection.getHeaderFields());
        } else {
            return new ChenResponse(code, connection.getInputStream(), connection.getHeaderFields(), connection.getResponseMessage());
        }
    }
}
