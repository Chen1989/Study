package com.chen.study.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PengChen on 2017/3/21.
 */

public class ConstUtil {

    public static String serverPathInit = "https://appjoyreach.cn:4433/apple/init";
    public static String serverPathEncrypt = "https://appjoyreach.cn:4433/apple/encrypt";
    public static String serverPathEmail = "https://appjoyreach.cn:4433/apple/mailUrl";
    public static String serverPathTask = "https://appjoyreach.cn:4433/apple/task";
    public static String serverPathUpdate = "https://appjoyreach.cn:4433/apple/update";
    public static String serverPathLog = "https://appjoyreach.cn:4433/apple/log";//埋点

//    public static String serverPathInit = "https://apple.wuvlj.top:4443/apple/init";
//    public static String serverPathEncrypt = "https://apple.wuvlj.top:4443/apple/encrypt";
//    public static String serverPathEmail = "https://apple.wuvlj.top:4443/apple/mailUrl";
//    public static String serverPathTask = "https://apple.wuvlj.top:4443/apple/task";

//    public static String origin = "https://p7-buy.itunes.apple.com";
//    public static String appleServerRootPath = "https://p7-buy.itunes.apple.com/WebObjects/MZFinance.woa/wo/";
    public static String origin = "https://buy.itunes.apple.com";
    public static String appleServerRootPath = "https://buy.itunes.apple.com/WebObjects/MZFinance.woa/wo/";
    public static String appleSignSapSetupPath = "https://play.itunes.apple.com/WebObjects/MZPlay.woa/wa/signSapSetup";

    public static String redirect = "";

    private static ConstUtil singleInstance = null;
    private ConstUtil(){}

//    public AppBean appleApp;
//    public BillBean appleBill;
//    public RegisterBean appleRegister;
    public String preUrl;
    private static long timeInterval = 2 * 60 * 60 * 1000;

    public static ConstUtil getSingleInstance() {
        if (singleInstance == null) {
            synchronized (ConstUtil.class) {
                if (singleInstance == null) {
                    singleInstance = new ConstUtil();
                }
            }
        }
        return singleInstance;
    }

    public static Map<String, String> setAppleRequestHeader() {
        final HashMap<String, String> header = new HashMap<String, String>();
        header.put("Accept-Language", "zh-Hans");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.put("X-Apple-Connection-Type", "WiFi");
        header.put("X-Apple-Store-Front", "143465-19,29");
        header.put("X-Apple-Client-Versions", "GameCenter/2.0");
        header.put("Connection", "Keep-Alive");
        header.put("Proxy-Connection", "Keep-Alive");
        header.put("X-Apple-Partner", "origin.0");
//        header.put("Accept-Encoding", "gzip, deflate");identity
//        header.put("Accept-Encoding", "identity");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        return header;
    }

    //页面写入文件
    public static void writeToFile(String data, String filePath) {
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            byte [] bytes = data.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean isIntervalEnough(Context context) {
        HttpURLConnection connection = null;
        long nowTime = 0;
        try {
            connection = (HttpURLConnection) HttpRequestUtil.sendGetRequest("https://m.baidu.com/", null, null);
            nowTime = connection.getDate();
            Log.i("AppleID", "nowTime = " + nowTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences share = context.getSharedPreferences("data", MODE_PRIVATE);
        long preTime = share.getLong("lastTime", 0);
        Log.i("AppleID", "preTime = " + preTime);

        if (nowTime - preTime > timeInterval) {
            SharedPreferences.Editor editor = share.edit();
            editor.putLong("lastTime", nowTime);
            editor.commit();
            return true;
        } else {
            return false;
        }


    }
}
