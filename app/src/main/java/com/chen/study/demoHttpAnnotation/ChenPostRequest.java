package com.chen.study.demoHttpAnnotation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by PengChen on 2017/9/30.
 */

public class ChenPostRequest extends ChenBaseRequest {
    public ChenPostRequest(ChenHttpContext httpContext) {
        super(httpContext);
    }

    @Override
    public ChenResponse request(ChenMethodContext context) throws IOException {
        return post(buildHttp(context));
    }

    private HttpURLConnection buildHttp(ChenMethodContext methodContext) throws IOException {
        HttpURLConnection connection;
        String params=null;
        if (methodContext.getParams() != null && methodContext.getParams().size() > 0) {
            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<String, String>> it = methodContext.getParams().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                if (it.hasNext())
                    sb.append("&");
            }
            params=sb.toString();
        }

        connection = httpContext.getHttpURLConnection(methodContext.getUrl(), (int) methodContext.getTimeOut(), methodContext.getMethod());
        if (methodContext.getHead() != null) {
            String[] heads = methodContext.getHead();
            if (Arrays.binarySearch(heads,"Content-Type")==-1){
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            for (int i = 0; i < heads.length; i++) {
                if (heads[i] != null) {
                    String[] kv = heads[i].split("[=]");
                    if (kv.length > 1)
                        connection.addRequestProperty(kv[0], kv[1]);
                }
            }
        }

        connection.setDoOutput(true);
        connection.connect();
        if (params!=null) {
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes(ChenIOConfigs.getInstance().getEncode()));
            out.flush();
        }
        return connection;
    }

    private ChenResponse post(HttpURLConnection connection) throws IOException {

        int code = connection.getResponseCode();
        if (code < 300) {
            return new ChenResponse(code, connection.getInputStream(), connection.getHeaderFields());
        } else {
            return new ChenResponse(code, connection.getInputStream(), connection.getHeaderFields(), connection.getResponseMessage());
        }
    }
}
