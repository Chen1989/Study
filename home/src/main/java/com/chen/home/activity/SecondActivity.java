package com.chen.home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.chen.home.R;

/**
 * Created by PengChen on 2018/1/17.
 */

public class SecondActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
