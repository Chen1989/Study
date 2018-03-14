package com.chen.home.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.chen.home.R;
import com.chen.home.hook.ActivityHook;
import com.chen.home.util.LogUtil;

import okhttp3.Request;
import com.chen.home.receiver.ChenReceiver;
import com.chen.home.util.LogUtil;

/**
 * Created by PengChen on 2018/1/17.
 */

public class SecondActivity extends Activity {
    private Button btnHook;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btnHook = (Button)findViewById(R.id.btn_hook);
//        ActivityHook.hookAcivity();
//        ActivityHook.hookAcivity();
        mContext = this;
        mContext.getSystemService(Context.ACTIVITY_SERVICE);
        btnHook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                btnHook.setY(200);
                AlertDialog.Builder builder  = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("Test");
                builder.setMessage("hello world");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                btnHook.setY(200);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Intent intent = new Intent(mContext, ThirdActivity.class);
                        ComponentName componentName = new ComponentName(mContext, ChenReceiver.class);
                        ActivityInfo info = null;
                        try {
                            info = mContext.getPackageManager().getReceiverInfo(componentName, PackageManager.MATCH_DEFAULT_ONLY);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        LogUtil.i("info = " + info.name);
                    }
                }).start();
            }
        });
        LogUtil.i("onCreate");

    }

    @Override
    protected void onStart() {
        LogUtil.i("onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        LogUtil.i("onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        LogUtil.i("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i("onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.i("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i("onDestroy");
        super.onDestroy();
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        LogUtil.i("uid = " + uid + ", pid = " + pid);
    }
}
