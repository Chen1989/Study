package com.chen.home;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ChenSdk", "length = ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlDownload = "";
                urlDownload = "http://192.168.1.105/webPage.js";

                try {
                    URL url = new URL(urlDownload);
                    // 打开连接
                    URLConnection con = url.openConnection();
                    // 输入流
                    int contentLength  = con.getContentLength();
                    InputStream inStream = con.getInputStream();
                    Log.d("ChenSdk", "length = " + contentLength);
                    Log.d("ChenSdk", "content = " + inStream.toString());

                    byte[] buffer = new byte[1024 * 4];
                    int len = 0;
                    StringBuffer sb = new StringBuffer();
                    String dirName  = "D:\\workInstall\\text.js";
                    File f = new File(dirName);
                    OutputStream os = new FileOutputStream(dirName);
                    while( (len = inStream.read(buffer)) !=-1 ){
                        os.write(buffer,0, len);
                    }
                    inStream.close();
                    os.close();
                    Log.d("ChenSdk", "content = " + sb.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
