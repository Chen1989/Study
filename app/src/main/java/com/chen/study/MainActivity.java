package com.chen.study;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chen.study.download.DownloadUtils;

public class MainActivity extends Activity {
    private Button downLoadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downLoadBtn = (Button) findViewById(R.id.btn_download);
        downLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
//                        downloadUtils.downloadAPK("http://cdn.abcdserver.com:8080/ads/apk/42261267-cb82-42cb-bbc0-057b375125fe.apk", "test.apk");
                    }
                }).start();
            }
        });
        Log.d("MainActivity", "path = " + Environment.getExternalStorageDirectory().getAbsolutePath());
//        Looper.prepare();
    }
}
