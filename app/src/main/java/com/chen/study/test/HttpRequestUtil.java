package com.chen.study.test;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


/**
 * Created by PengChen on 2017/3/16.
 */

public class HttpRequestUtil {

    public static CookieManager manager = new CookieManager();
//    public static SdkCookieManager manager = new SdkCookieManager();
//    static
//    {
//        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        SdkCookieManager.setDefault(manager);
//    }

    static TrustManager[] managerUtils = new TrustManagerUtil[] { new TrustManagerUtil() };

    static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            // System.out.println("Warning: URL Host: " + hostname + " vs. "
            // + session.getPeerHost());
            return true;
        }
    };

    /**
     * 信任所有主机-对于任何证书都不做检查
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android 采用X509的证书信息机制
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, managerUtils, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);//
            // 不进行主机名确认
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHostName(String urlStr) {
        try {
            URL url = new URL(urlStr);
            int port = url.getPort();
            if (port == -1) {
                return url.getProtocol()+"://" + url.getHost();
            } else {
                return url.getProtocol()+"://" + url.getHost() + ":" + port;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送GET请求
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendGetRequest(String url, Map<String, String> params, Map<String, String> headers)
            throws Exception {
        StringBuilder buf = new StringBuilder(url);
        Set<Entry<String, String>> entrys = null;
        // 如果是GET请求，则请求参数在URL中
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            entrys = params.entrySet();
            for (Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(buf.toString());
        HttpURLConnection conn = null;
        if (url1.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            conn = (HttpsURLConnection) url1.openConnection();
            ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
        } else {
            conn = (HttpURLConnection) url1.openConnection();
        }
        conn.setRequestMethod("GET");
        conn.setRequestProperty("content-type","application/json;charset=utf-8");
        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.getResponseCode();
        return conn;
    }


    /**
     * 发送GET请求
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendGetAppleRequest(String url,
                                               Map<String, String> params, Map<String, String> headers)
            throws Exception {
        StringBuilder buf = new StringBuilder(url);
        Set<Entry<String, String>> entrys = null;
        // 如果是GET请求，则请求参数在URL中
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            entrys = params.entrySet();
            for (Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(buf.toString());
        HttpURLConnection conn = null;
        if (url1.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            conn = (HttpsURLConnection) url1.openConnection();
            ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
        } else {
            conn = (HttpURLConnection) url1.openConnection();
        }
        if (null != getStoreCookies(url) && !getStoreCookies(url).isEmpty()) {
            conn.setRequestProperty("Cookie",getStoreCookies(url));
        }

        conn.setRequestMethod("GET");
        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.getResponseCode();
        manager.put(URI.create(url),conn.getHeaderFields());
        return conn;
    }

    public static void redirect(String url, Map<String, String> params, Map<String, String> headers) throws Exception{
        StringBuilder buf = new StringBuilder(url);
        Set<Entry<String, String>> entrys = null;
        // 如果是GET请求，则请求参数在URL中
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            entrys = params.entrySet();
            for (Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(buf.toString());
        HttpURLConnection conn = null;
        if (url1.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            conn = (HttpsURLConnection) url1.openConnection();
            ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
        } else {
            conn = (HttpURLConnection) url1.openConnection();
        }

        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);
        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.getResponseCode();
        if (conn.getResponseCode() == 302) {//重定向一次
            Log.i("AppleID", "getResponseCode()重定向");
            String locationUrl = conn.getHeaderField("Location");
            if (locationUrl == null || locationUrl.isEmpty()) {
                locationUrl = conn.getHeaderField("location");
            }
            if (locationUrl.startsWith("http://")) {
                locationUrl = locationUrl.replace("http://", "");
            }
            if (locationUrl.startsWith("https://")) {
                locationUrl = locationUrl.replace("https://", "");
            }

            ConstUtil.redirect = locationUrl.split("\\.")[0];
        }
        conn.disconnect();
    }


    /**
     * 发送POST请求
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendPostMapRequest(String url,
                                                Map<String, String> params, Map<String, String> headers)
            throws Exception {
        StringBuilder buf = new StringBuilder();
        Set<Entry<String, String>> entrys = null;
        // 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
        if (params != null && !params.isEmpty()) {
            buf.append("{");
            entrys = params.entrySet();
            for (Entry<String, String> entry : entrys) {
                buf.append("\"");
                buf.append(entry.getKey()).append("\":\"")
                        .append(entry.getValue())
                        .append("\",");
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append("}");
        }
        URL url1 = new URL(url);
        HttpURLConnection conn = null;
        if (url1.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            conn = (HttpsURLConnection) url1.openConnection();
            ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
        } else {
            conn = (HttpURLConnection) url1.openConnection();
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-type","application/json;charset=utf-8");

        conn.setDoOutput(true);
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        OutputStream out = conn.getOutputStream();
        out.write(buf.toString().getBytes("UTF-8"));

        out.flush();
        out.close();
        conn.getResponseCode(); // 为了发送成功
        return conn;
    }

    /**
     * 发送POST请求
     * @param url
     * @param postData
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendPostStringRequest(String url,
                                                String postData, Map<String, String> headers)
            throws Exception {
        Set<Entry<String, String>> entrys = null;
        URL url1 = new URL(url);

        HttpURLConnection conn = null;
        if (url1.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            conn = (HttpsURLConnection) url1.openConnection();
            ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
        } else {
            conn = (HttpURLConnection) url1.openConnection();
        }

        if (null != getStoreCookies(url) && !getStoreCookies(url).isEmpty()) {
            conn.setRequestProperty("Cookie",getStoreCookies(url));
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Length", String.valueOf(postData.length()));
        conn.setDoOutput(true);
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        OutputStream out = conn.getOutputStream();
        if (postData != null) {
            out.write(postData.getBytes("UTF-8"));
        }
        out.flush();
        out.close();
        conn.getResponseCode(); // 为了发送成功

        manager.put(URI.create(url),conn.getHeaderFields());

        return conn;
    }

    /**
     * 发送POST请求
     * @param url
     * @param postData
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendPostLogStringRequest(String url,
                                                      String postData, Map<String, String> headers)
            throws Exception {
        Set<Entry<String, String>> entrys = null;
        URL url1 = new URL(url);

        HttpURLConnection conn = null;
        if (url1.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            conn = (HttpsURLConnection) url1.openConnection();
            ((HttpsURLConnection)conn).setHostnameVerifier(DO_NOT_VERIFY);
        } else {
            conn = (HttpURLConnection) url1.openConnection();
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-type","application/json;charset=utf-8");
//        conn.setRequestProperty("Content-Length", String.valueOf(postData.length()));
        conn.setDoOutput(true);
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        OutputStream out = conn.getOutputStream();
        if (postData != null) {
            out.write(postData.getBytes("UTF-8"));
        }
        out.flush();
        out.close();
        conn.getResponseCode(); // 为了发送成功
        return conn;
    }

    /**
     * 将输入流转为字节数组
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] read2Byte(InputStream inStream)throws Exception{
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = inStream.read(buffer)) !=-1 ){
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }
    /**
     * 将输入流转为字符串
     * @param inStream
     * @return
     * @throws Exception
     */
    public static String read2String(InputStream inStream)throws Exception{
        char[] buffer = new char[1024];
        int len = 0;
        StringBuffer sb = new StringBuffer();
        InputStreamReader streamReader = new InputStreamReader(inStream, "UTF-8");
        while( (len = streamReader.read(buffer)) !=-1 ){
            sb.append(new String(buffer, 0, len));
        }
        inStream.close();
        streamReader.close();
        return sb.toString();
    }

    public static String read2String(InputStream inStream, String chartSet)throws Exception{
        char[] buffer = new char[1024];
        int len = 0;
        StringBuffer sb = new StringBuffer();
        InputStreamReader streamReader = new InputStreamReader(inStream, chartSet);
        while( (len = streamReader.read(buffer)) !=-1 ){
            sb.append(new String(buffer, 0, len));
        }
        inStream.close();
        streamReader.close();
        return sb.toString();
    }
    /**
     * 发送xml数据
     * @param path 请求地址
     * @param xml xml数据
     * @param encoding 编码
     * @return
     * @throws Exception
     */
    public static byte[] postXml(String path, String xml, String encoding) throws Exception{
        byte[] data = xml.getBytes(encoding);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset="+ encoding);
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setConnectTimeout(5 * 1000);
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if(conn.getResponseCode()==200){
            return read2Byte(conn.getInputStream());
        }
        return null;
    }

    public static String getStoreCookies(String url){
        String res = "";
        try {
            Map<String, List<String>> cookies = manager.get(URI.create(url), new HashMap<String, List<String>>());
            if (cookies != null && !cookies.isEmpty()) {
                List<String> listCookie = cookies.get("Cookie");
                if (listCookie != null && !listCookie.isEmpty()) {
                    for (String cook : listCookie) {

                        cook = cook.replaceAll("\"", "");
                        cook = cook.replaceAll("$", "");
                        res = res + cook + ";";
                    }
                    if (!res.isEmpty()) {
                        res = res.substring(0, res.length() - 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
