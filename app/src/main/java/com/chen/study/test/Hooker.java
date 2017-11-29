package com.chen.study.test;

import android.content.Intent;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Hooker
{
    public static class Handle
    {
        public boolean IsCancel = false;
    }

    public interface OnActivityManagerHooker
    {
        void onStartActivity(Intent intent, Handle handle);
    }

    private static List<OnActivityManagerHooker> _list = new ArrayList<OnActivityManagerHooker>();

    public static void addHocker(OnActivityManagerHooker hooker)
    {
        _list.add(hooker);
    }

    public static void clearHock(){
        if(!_list.isEmpty()){
            _list.clear();
        }
    }

    public static void hook()
    {
        hook(new OnActivityManagerHooker()
        {
            @Override
            public void onStartActivity(Intent intent, Handle handle)
            {
                for (OnActivityManagerHooker hooker : _list)
                {
                    hooker.onStartActivity(intent, handle);
                }
            }
        });
    }

    public static void hook(final OnActivityManagerHooker _hocker)
    {
        try
        {
            Object v = ReflectAccess.getValue("android.app.ActivityManagerNative", "gDefault");
            Class<?> ia = Class.forName("android.app.IActivityManager");
            if (v.getClass().isAssignableFrom(ia))
            {
                ReflectAccess.setValue("android.app.ActivityManagerNative", "gDefault", getActivityManagerProxy(v, ia, _hocker));
            }
            else
            {
                Object m = ReflectAccess.getValue(v, "mInstance");
                ReflectAccess.setValue(v, "mInstance", getActivityManagerProxy(m, ia, _hocker));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Object getActivityManagerProxy(final Object host, Class<?> ia, final OnActivityManagerHooker _hocker)
    {
        return Proxy.newProxyInstance(Hooker.class.getClassLoader(), new Class<?>[]{ia}, new InvocationHandler()
        {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                if ("startActivity".equals(method.getName()))
                {
                    for (int i = 0; i < args.length; i++)
                    {
                        Object arg = args[i];
                        if (arg instanceof Intent)
                        {
                            Intent intent = (Intent) arg;

                            //拦截某个没有在manifest文件中声明的activity
                            if (intent.getComponent().getClassName().equalsIgnoreCase("com.chen.study.activity.SecondActivity")) {
                                Intent proxyIntent = new Intent();
                                proxyIntent.setClassName(intent.getComponent().getPackageName(), "com.chen.study.activity.ThirdActivity");
                                proxyIntent.putExtra("oldIntent", intent);
                                args[i] = proxyIntent;
                                return method.invoke(host, args);
                            }
                            if (_hocker != null)
                            {
                                Handle handle = new Handle();
                                _hocker.onStartActivity(intent, handle);
                                if (handle.IsCancel){
                                    if (method.getReturnType().toString().startsWith("int")) {
                                        return 0;
                                    }else {
                                        return null;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }

                return method.invoke(host, args);
            }
        });
    }

    //hook回调，传回真正的intent
    public static void hookSystemHandler() {
        try {

            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            //获取主线程对象
            Object activityThread = currentActivityThreadMethod.invoke(null);
            //获取mH字段
            Field mH = activityThreadClass.getDeclaredField("mH");
            mH.setAccessible(true);
            //获取Handler
            Handler handler = (Handler) mH.get(activityThread);
            //获取原始的mCallBack字段
            Field mCallBack = Handler.class.getDeclaredField("mCallback");
            mCallBack.setAccessible(true);
            //这里设置了我们自己实现了接口的CallBack对象
            mCallBack.set(handler, new ActivityThreadHandlerCallback(handler)) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
