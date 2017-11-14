package com.chen.study.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookerPackageManager
{
    public interface OnPkgManagerHooker
    {
        void onMethod(String str);
    }
    public static void hook(Context context,OnPkgManagerHooker _hocker)
    {
        try
        {
            // 获取全局的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 获取ActivityThread里面原始的 sPackageManager
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);

            // 准备好代理对象, 用来替换原始的对象
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            sPackageManagerField.set(currentActivityThread, getActivityManagerProxy(sPackageManager,iPackageManagerInterface,_hocker,context));

            // 2. 替换 ApplicationPackageManager 里面的 mPM对象
            PackageManager pm = context.getApplicationContext().getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, getActivityManagerProxy(sPackageManager,iPackageManagerInterface,_hocker,context));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Object getActivityManagerProxy(final Object host, Class<?> ia, final OnPkgManagerHooker _hocker, final Context context)
    {
        return Proxy.newProxyInstance(ia.getClassLoader(), new Class<?>[]{ia}, new InvocationHandler()
        {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                for (Object arg : args)
                {
                    LogUtil.e( "HookerPackageManager all--"+arg.toString()+"-"+method.getName());
                }
//                if ("checkPermission".equals(method.getName())) {
//                    if (args.length == 2) {
//                        Object arg0 = args[0];
//                        Object arg1 = args[1];
//                        if (arg1 instanceof String)
//                        {
//                            String str = (String) args[1];
//                            if ("com.mj525.lcokscreen0000".equals(str)) {
//                                LogUtil.e("HookerPackageManager checkPermission--"+str);
//                                str = "com.mj525.lcokscreen";
//                                return method.invoke(host, arg0, str);
//                            }
//                        }
//                    }
//                }
                if ("getPackageInfo".equalsIgnoreCase(method.getName())) {
                    LogUtil.e( "HookerPackageManager info.signatures[0] AAAAAAAAAAAA");
                    PackageInfo info = (PackageInfo) method.invoke(host, args);
                    if (info.signatures != null) {
                        LogUtil.e( "HookerPackageManager info.signatures[0] before = " + info.signatures[0].toCharsString());
                        String signNeed = "3082022e3082019702044ce38c9f300d06092a864886f70d0101040500305e310b3009060355040613025553310b3009060355040813024341311630140603550407130d53616e204672616e636973636f31123010060355040a13096861786f722e636f6d311630140603550403130d427265747420536c61746b696e301e170d3130313131373038303434375a170d3336313131303038303434375a305e310b3009060355040613025553310b3009060355040813024341311630140603550407130d53616e204672616e636973636f31123010060355040a13096861786f722e636f6d311630140603550403130d427265747420536c61746b696e30819f300d06092a864886f70d010101050003818d00308189028181009c143ee0b34370e3b157cdba156eef67877f7b0280db5df2a1a1f6f5ba39c89e92b4a72cee361087f76edf9496e312b5f154edf0cf189d5e50e8cd25e3c90b4281bcd1d90cbfb6a066280a447d2e3fac31b945a3f179e29af7be8b32292408fa5c6415cf2bac2643df1306f164e49c7f033ce3ba842f939c36670d3f96df1a030203010001300d06092a864886f70d010104050003818100175c23f5ea1b25ddaf315371ed02f4dc6c859fbeca835746ff65228e4dac4a6fc02fac92272941f4735a1da3ad80275abb4412855d60b2f8b231afa73582f389eac5ad7905f1889ec5c6d3f085146ce718f8a1456877a78fbab0e3e092ed62ed787ce219050ae063f0c414c9c40920555d84b5df638ae93a9a5525f393ca43ea";
                        Signature signature = new Signature(signNeed);
                        info.signatures[0] = signature;
                        LogUtil.e( "HookerPackageManager info.signatures[0] after = " + info.signatures[0].toCharsString());
                    }
                    return info;
                }
//                if ("getApplicationInfo".equals(method.getName())) {
//                    LogUtil.e( "HookerPackageManager getApplicationInfo " + args[0]+"-"+args.length);
//                    if (args.length == 3) {
//                        Object arg0 = args[0];
//                        if (arg0 instanceof String)
//                        {
//                            String strPkg = (String) args[0];
//                            String newPackageName = "com.mj525.lcokscreen0000";
//                            String realPackageName = context.getPackageName();
//                            if (newPackageName.equals(strPkg)) {
//                                args[0] = "com.mj525.lcokscreen";
//                                LogUtil.e("HookerPackageManager getApplicationInfo2 " + args[0]+"-"+args.length);
//                                return method.invoke(host,args);
//                            }
//                        }
//                    }
//                }
                LogUtil.e("HookerPackageManager resolveIntent---" + method.getName());
//                if ("resolveIntent".equals(method.getName())) {
//                    LogUtil.e("HookerPackageManager resolveIntent " + args[0]+"-"+args.length);
//                    if (args[0] instanceof Intent) {
//                        Intent intent = (Intent) args[0];
//                        if ("com.mj525.lcokscreen0000".equals(intent.getComponent().getPackageName())) {
//                            LogUtil.e( "-----intent-"+intent.getComponent().getPackageName());
////                            Intent intent1 = new Intent("com.mj525.lcokscreen/com.google.android.gms.ads.AdActivity");
////                            Class<? extends ComponentName> aClass = intent.getComponent().getClass();
////                            intent.setClassName(context, "com.mj525.lcokscreen");
////                            intent.setComponent(new ComponentName("com.mj525.lcokscreen","com.google.android.gms.ads.AdActivity"));
//                            intent.setComponent(new ComponentName("com.mj525.lcokscreen","com.applovin.adview.AppLovinInterstitialActivity"));
//                            LogUtil.e( "HookerPackageManager -----intent1-"+intent.getComponent().getPackageName());
//                            args[0] = intent;
////                            Logger.e(context, "resolveIntent " + args[1]+"-"+args.length);
////                            Logger.e(context, "resolveIntent " + args[2]+"-"+args.length);
////                            Logger.e(context, "resolveIntent " + args[3]+"-"+args.length);
//                            return method.invoke(host, args);
//                        }
//                    }
//
//                }
//                Object invoke = method.invoke(host, args);
                return method.invoke(host, args);
            }
        });
    }
}
