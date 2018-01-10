package com.chen.study.test;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
//        try
//        {
//            Object v = ReflectAccess.getValue("android.app.ActivityManagerNative", "gDefault");
//            Class<?> ia = Class.forName("android.app.IActivityManager");
//            if (v.getClass().isAssignableFrom(ia))
//            {
//                ReflectAccess.setValue("android.app.ActivityManagerNative", "gDefault", getActivityManagerProxy(v, ia, _hocker));
//            }
//            else
//            {
//                Object m = ReflectAccess.getValue(v, "mInstance");
//                ReflectAccess.setValue(v, "mInstance", getActivityManagerProxy(m, ia, _hocker));
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    private static Object getActivityManagerProxy(final Object host, Class<?> ia, final OnActivityManagerHooker _hocker)
    {
        return Proxy.newProxyInstance(Hooker.class.getClassLoader(), new Class<?>[]{ia}, new InvocationHandler()
        {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                Log.d("TAG","getActivityManagerProxy methodName = "+method.getName());
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

    public static void compatibleAppCompatActivity() {
        // 兼容AppCompatActivity报错问题
        try {
            Class<?> forName = Class.forName("android.app.ActivityThread");
            Field field = forName.getDeclaredField("sCurrentActivityThread");
            field.setAccessible(true);
            Object activityThread = field.get(null);
            Method getPackageManager = activityThread.getClass().getDeclaredMethod("getPackageManager");
            Object iPackageManager = getPackageManager.invoke(activityThread);

            PackageManagerHandler handler = new PackageManagerHandler(iPackageManager);
            Class<?> iPackageManagerIntercept = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{iPackageManagerIntercept}, handler);

            // 获取 sPackageManager 属性
            Field iPackageManagerField = activityThread.getClass().getDeclaredField("sPackageManager");
            iPackageManagerField.setAccessible(true);
            iPackageManagerField.set(activityThread, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static class PackageManagerHandler implements InvocationHandler{
        private Object mAmsObj;
        public PackageManagerHandler (Object obj) {
            mAmsObj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d("TAG","methodName"+method.getName());
            if(method.getName().equals("startActivity")){
                // 启动Activity的方法,找到原来的Intent
                Intent realIntent = (Intent) args[2];
                // 代理的Intent
                Intent proxyIntent = new Intent();
//                proxyIntent.setComponent(new ComponentName(mContext,mProxyActivity));
                proxyIntent.setClassName(realIntent.getComponent().getPackageName(), "com.chen.study.activity.ThirdActivity");
                // 把原来的Intent绑在代理Intent上面
                proxyIntent.putExtra("oldIntent",realIntent);
                // 让proxyIntent去晒太阳，借尸
                args[2] = proxyIntent;
            }
            return method.invoke(mAmsObj,args);
        }
    }
}
