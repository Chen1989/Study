package com.chen.study;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chen.study.download.DownloadUtils;
import com.chen.study.pluginRes.ResourceBean;
import com.chen.study.pluginRes.ResourceManager;

public class MainActivity extends Activity {
    private Button downLoadBtn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.iv_plugin);
        downLoadBtn = (Button) findViewById(R.id.btn_download);
        downLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUninstalledBundle(v);
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
        ResourceManager.init(getApplicationContext());
//        Looper.prepare();
    }

    /**
     * 加载未安装APK资源
     *
     * @param view
     */
    public void loadUninstalledBundle(View view) {
        ResourceBean loadResource = ResourceManager.unInstalled().loadResource("/sdcard/StartActivity.apk");
        Drawable drawable = ResourceManager.unInstalled().getDrawable(loadResource.packageName, "pic1");
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

}
