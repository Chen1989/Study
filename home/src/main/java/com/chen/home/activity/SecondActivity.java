package com.chen.home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.chen.home.R;
import com.chen.home.hook.ActivityHook;

/**
 * Created by PengChen on 2018/1/17.
 */

public class SecondActivity extends Activity {
    private Button btnHook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btnHook = (Button)findViewById(R.id.btn_hook);
        ActivityHook.hookAcivity();
        btnHook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                btnHook.setY(200);
            }
        });
    }
}
