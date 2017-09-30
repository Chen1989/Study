package com.chen.study.demoHttpAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PengChen on 2017/9/29.
 */

public class ChenMethodContext {
    private Type returnType;
    private Map<String, String> params;
    private String method;
    private String url;
    private String[] head;
    private IChenConvert convert;
    private long timeOut;
    private ChenResponse response;

    public ChenMethodContext(Type returnType, Map<String, String> params, String method, String url, String[] head, IChenConvert convert, long timeOut) {
        this.returnType = returnType;
        this.params = params;
        this.method = method;
        this.url = url;
        this.head = head;
        this.convert = convert;
        this.timeOut = timeOut;
    }

    public static ChenMethodContext parse(Method method, IChenConvert convert, Object[] args) throws ChenNetException {
        ChenQuery query = method.getAnnotation(ChenQuery.class);

        if (query != null) {
            return getQuery(method, convert, args, query);
        }

        ChenUrl urlAnn = method.getAnnotation(ChenUrl.class);
        if (urlAnn != null) {
            return getUrl(method, convert, args, urlAnn);
        }

        ChenBaseUrl baseUrl = method.getDeclaringClass().getAnnotation(ChenBaseUrl.class);

        if (baseUrl != null) {
            return getBaseUrl(method, convert, args, baseUrl);
        }
        throw new ChenNetException("url is null", -1);
    }

    private static ChenMethodContext getQuery(Method method, IChenConvert convert, Object[] args, ChenQuery query) throws ChenNetException {
        String[] keys = query.fields();
        Map<String, String> paramsMap = new HashMap<>();
        for (int j = 0; j < keys.length; j++) {
            String value = String.valueOf(args[j]);
            paramsMap.put(keys[j], value);
        }
        String methods = query.method().getValue();
        String[] heads = query.heads();
        String queryUrl = query.value();
        String url = parseParams(method, args, queryUrl, paramsMap);
        return new ChenMethodContext(method.getReturnType(), paramsMap, methods, url, heads, convert, query.timeOut());
    }

    private static ChenMethodContext getUrl(Method method, IChenConvert convert, Object[] args, ChenUrl urlAnn) throws ChenNetException {
        String url = null;
        long timeOut = 0;

        String methods = urlAnn.method().getValue();
        String[] heads = urlAnn.heads();
        String queryUrl = urlAnn.value();
        timeOut = urlAnn.timeOut();
        Map<String, String> paramsMap = new HashMap<>();
        url = parseParams(method, args, queryUrl, paramsMap);

        return new ChenMethodContext(method.getReturnType(), paramsMap, methods,
                url, heads, convert, timeOut);
    }

    private static ChenMethodContext getBaseUrl(Method method, IChenConvert convert, Object[] args, ChenBaseUrl baseUrl) throws ChenNetException {
        String url = null;
        url = baseUrl.value();
        long timeOut = 0;
        String methods = baseUrl.method().getValue();
        String[] heads = baseUrl.heads();
        timeOut = baseUrl.timeOut();
        Map<String, String> paramsMap = new HashMap<>();
        ChenMethod methodP = method.getAnnotation(ChenMethod.class);
        if (methodP != null) {
            if (methodP.value().getValue().length() > 0)
                methods = methodP.value().getValue();
            if (methodP.heads().length > 0)
                heads = methodP.heads();
            timeOut = methodP.timeOut();

        }
        url = parseParams(method, args, url, paramsMap);
        return new ChenMethodContext(method.getReturnType(), paramsMap, methods,
                url, heads, convert, timeOut);
    }

    private static String parseParams(Method method, Object[] args, String queryUrl, Map<String, String> paramsMap) throws ChenNetException {
        String url = null;
        if (method.getParameterAnnotations() != null) {
            if (queryUrl != null && queryUrl.length() > 0) {
                if (queryUrl.startsWith("http://") || queryUrl.startsWith("https://")) {
                    url = queryUrl;
                } else {
                    ChenBaseUrl baseUrl = method.getDeclaringClass().getAnnotation(ChenBaseUrl.class);
                    if (baseUrl != null) {
                        url = baseUrl.value() + queryUrl;
                    } else {
                        throw new ChenNetException("url is null", -1);
                    }
                }
            } else {
                ChenBaseUrl baseUrl = method.getDeclaringClass().getAnnotation(ChenBaseUrl.class);
                if (baseUrl != null) {
                    url = baseUrl.value();
                } else {
                    throw new ChenNetException("url is null", -1);
                }
            }

            Annotation[][] anns = method.getParameterAnnotations();
            for (int j = 0; j < anns.length; j++) {
                Annotation[] ann2 = anns[j];
                if (ann2.length > 1) {
                    for (int i = 0; i < ann2.length; i++) {
                        Annotation ann = ann2[i];
                        if (ann instanceof ChenPath) {
                            ChenPath path = (ChenPath) ann;
                            String key = path.value();
                            Object value = args[j];
                            key = String.format("{%s}", key);
                            assert url != null;
                            url = url.replace(key, (String) value);
                        } else if (ann instanceof ChenParams) {
                            ChenParams params = (ChenParams) ann;
                            String key = params.value();
                            Object value = args[j];
                            paramsMap.put(key, (String) value);
                        }
                    }
                } else {
                    Annotation ann = anns[j][0];
                    if (ann instanceof ChenPath) {
                        ChenPath path = (ChenPath) ann;
                        String key = path.value();
                        Object value = args[j];
                        key = String.format("{%s}", key);
                        assert url != null;
                        url = url.replace(key, (String) value);
                    } else if (ann instanceof ChenParams) {
                        ChenParams params = (ChenParams) ann;
                        String key = params.value();
                        Object value = args[j];
                        paramsMap.put(key, (String) value);
                    }
                }

            }

        }
        return url;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String[] getHead() {
        return head;
    }

    public IChenConvert getConvert() {
        return convert;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHead(String[] head) {
        this.head = head;
    }

    public void setConvert(IChenConvert convert) {
        this.convert = convert;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public ChenResponse getResponse() {
        return response;
    }

    public void setResponse(ChenResponse response) {
        this.response = response;
    }
}
