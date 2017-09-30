package com.chen.study.demoHttpAnnotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;


/**
 * Created by PengChen on 2017/9/29.
 */

public class ChenHttpService extends ChenHttpContext implements ChenNetWorkService {
    private List<ChenInterceptor> interceptors = new ArrayList<>();

    public ChenHttpService() {

    }

    public ChenHttpService(HostnameVerifier hostNameVerifier, SSLSocketFactory sockets) {
        super(hostNameVerifier, sockets);
    }

    @Override
    public ChenNetWorkService addInteropter(ChenInterceptor interopter) {
        interceptors.add(interopter);
        return this;
    }

    @Override
    public <T> T execute(ChenMethodContext context) throws IOException, ChenNetException {
        for (int i = 0; i < interceptors.size(); i++) {
            interceptors.get(i).onInvokerBefore(context);
        }

        T t = doExecute(context);

        for (int i = 0; i < interceptors.size(); i++) {
            interceptors.get(i).onInvokerAfter(context);
        }
        return t;
    }

    private <T> T doExecute(ChenMethodContext context) throws IOException, ChenNetException {
        ChenResponse res = null;
        if (context.getMethod().equalsIgnoreCase("get")) {
            res = new ChenGetRequest(this).request(context);
        } else if (context.getMethod().equalsIgnoreCase("post")) {
            res = new ChenPostRequest(this).request(context);
        } else if (context.getMethod().equalsIgnoreCase("put")) {
            res = new ChenPutRequest(this).request(context);
        } else if (context.getMethod().equalsIgnoreCase("delete")) {
            res = new ChenDeleteRequest(this).request(context);
        } else if (context.getMethod().equalsIgnoreCase("head")) {
            res = new ChenHeadRequest(this).request(context);
        }

        if (res != null) {

            context.setResponse(res);

            if (res.getCode() < 300) {
                return context.getConvert().convert(res.getEntity());
            } else {
                throw new ChenNetException(res.getMessage(), res.getCode());
            }
        }
        throw new ChenNetException("unkown error", -1);
    }
}
