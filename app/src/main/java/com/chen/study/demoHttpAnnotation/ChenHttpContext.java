package com.chen.study.demoHttpAnnotation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by PengChen on 2017/9/29.
 */

public abstract class ChenHttpContext {
    private HostnameVerifier hostNameVerifier;
    private SSLSocketFactory sockets;

    public ChenHttpContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            sockets = context.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        hostNameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    public ChenHttpContext(HostnameVerifier hostNameVerifier, SSLSocketFactory sockets) {
        this.hostNameVerifier = hostNameVerifier;
        this.sockets = sockets;
    }

    public HostnameVerifier getHostNameVerifier() {
        return hostNameVerifier;
    }

    public SSLSocketFactory getSockets() {
        return sockets;
    }

    public HttpURLConnection getHttpURLConnection(String url, int timeOut, String method) throws IOException {
        HttpURLConnection connection;
        if (url.startsWith("https")) {
            HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(url).openConnection();
            urlConnection.setHostnameVerifier(getHostNameVerifier());
            urlConnection.setSSLSocketFactory(getSockets());
            connection = urlConnection;
        } else {
            connection = (HttpURLConnection) new URL(url).openConnection();
        }

        connection.setRequestMethod(method.toUpperCase());

        connection.setConnectTimeout(timeOut);
        connection.setReadTimeout(timeOut);
        connection.setInstanceFollowRedirects(true);
        connection.setDoInput(true);

        return connection;
    }
}
