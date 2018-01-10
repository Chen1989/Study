package com.chen.study.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.chen.study.R;
import com.chen.study.util.LogUtil;

import java.lang.reflect.Method;

/**
 * Created by PengChen on 2017/11/29.
 * 单击，双击，多次点击，长按，滑动等操作
 */

public class SecondActivity extends Activity {
    private float _lastX, _lastY;
    private Handler _handler;
    private Runnable _runner;
    private int _count = 0;
    private Handler _longHandler;
    private Runnable _longRunner;
    private float _x, _y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("event.getAction() = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                _longHandler = new Handler();
                _longHandler.postDelayed(_longRunner = new Runnable() {
                    @Override
                    public void run() {

                        longPress();
                        _lastY = -1;
                        _lastX = -1;
                    }
                }, 500);
                _x = _lastX = event.getRawX();
                _y = _lastY = event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                _x = event.getRawX();
                _y = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                _longHandler.removeCallbacks(_longRunner);
                if (_lastX == -1 && _lastY == -1) {
                    return false;
                }
                float aa = ViewConfiguration.get(this).getScaledTouchSlop();
                if (Math.abs(event.getRawX() - _lastX) < aa && Math.abs(event.getRawY() - _lastY) < aa &&
                        event.getEventTime() - event.getDownTime() < 200) {
                    onClickEvent();
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                onSwipe(_x - _lastX, _y - _lastY, event.getEventTime() - event.getDownTime());
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void onClickEvent() {
        if (_runner == null) {
            _handler = new Handler();
            _handler.postDelayed(_runner = new Runnable() {
                @Override
                public void run() {
                    _runner = null;
                    _handler = null;
                    //单击事件
//                    oneClick();
                    if (_count == 0) {
                        oneClick();
                    } else {
                        multipleClick(_count+1);
                    }
                    _count = 0;
                }
            }, 400);
        } else {
//            _handler.removeCallbacks(_runner);
            _count++;
            //双击事件
//            doubleClick();
        }
    }

    private void oneClick() {
        Toast.makeText(this, "one click event action", Toast.LENGTH_SHORT).show();
    }

    private void doubleClick() {
        Toast.makeText(this, "double click event action", Toast.LENGTH_SHORT).show();
    }

    private void multipleClick(int count) {
        Toast.makeText(this, "multiple click event action " + count, Toast.LENGTH_SHORT).show();
    }

    private void longPress() {
        Toast.makeText(this, "long press event action ", Toast.LENGTH_SHORT).show();
    }

    //向下滑动
    private void onSwipe(float x, float y, long time) {
        if (y > 100 && y > Math.abs(x) && time < 200) {
            openNotify();
        }
    }

    public void openNotify() {
        // TODO Auto-generated method stub
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        try {
            Object service = getSystemService("statusbar");
            Class<?> statusbarManager = Class
                    .forName("android.app.StatusBarManager");
            Method expand = null;
            if (service != null) {
                if (currentApiVersion <= 16) {
                    expand = statusbarManager.getMethod("expand");
                } else {
                    expand = statusbarManager
                            .getMethod("expandNotificationsPanel");
                }
                expand.setAccessible(true);
                expand.invoke(service);
            }

        } catch (Exception e) {
        }

    }
}
