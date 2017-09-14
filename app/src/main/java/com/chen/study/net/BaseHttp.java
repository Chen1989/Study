package com.chen.study.net;

import com.chen.study.demoHttpAnnotation.HttpAnnotation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by PengChen on 2017/9/13.
 */

public class BaseHttp {
    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    };

    public static HttpURLConnection buildConnection(URL url) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        if (url.getProtocol().equals("http")) {
            return (HttpURLConnection) url.openConnection();
        } else {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{myX509TrustManager}, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
            return httpsURLConnection;
        }
    }

    @HttpAnnotation(methodType = "POST")
    public void requestUrl() {

    }
}
