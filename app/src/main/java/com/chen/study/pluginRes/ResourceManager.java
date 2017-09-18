package com.chen.study.pluginRes;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.chen.study.util.LogUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * Created by PengChen on 2017/9/18.
 */

public class ResourceManager {

    private static final String TAG = "ResourceManager";

    private ResourceManager() {
    }

    public static void init(Context context) {
        UnInstalled.sManager.init(context);
    }

    public static UnInstalled unInstalled() {
        return UnInstalled.sManager;
    }



    public static class UnInstalled {

        static final UnInstalled sManager = new UnInstalled();

        private Context mContext;
        private Map<String, ResourceBean> mRescources = new HashMap<String, ResourceBean>();
        private String mDexDir;

        private UnInstalled() {

        }

        /**
         * 初始化
         *
         * @param context
         */
        public void init(Context context) {
            mContext = context.getApplicationContext();
            File dexDir = mContext.getDir("dex", Context.MODE_PRIVATE);
            if (!dexDir.exists()) {
                dexDir.mkdir();
            }
            mDexDir = dexDir.getAbsolutePath();
        }

        /**
         * 获取未安装应用资源的ID
         *
         * @param packageName
         * @param fieldName
         * @return
         */
        public int getResourceID(String packageName, String type, String fieldName) {
            int resID = 0;
            ResourceBean recource = getUnInstalledRecource(packageName);
            String rClassName = packageName + ".R$" + type;
            LogUtil.w("resource class:" + rClassName + ",fieldName:" + fieldName);
            try {
                Class cls = recource.classLoader.loadClass(rClassName);
                resID = (Integer) cls.getField(fieldName).get(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resID;
        }

        /**
         * 获取未安装应用Drawable
         *
         * @param packageName
         * @param fieldName
         * @return
         */
        public Drawable getDrawable(String packageName, String fieldName) {
            Drawable drawable = null;
            int resourceID = getResourceID(packageName, "drawable", fieldName);
            ResourceBean recource = getUnInstalledRecource(packageName);
            if (recource != null) {
                drawable = recource.resources.getDrawable(resourceID);
            }
            return drawable;
        }

        /**
         * 加载未安装应用资源包
         *
         * @param resourcePath
         * @return
         */
        public ResourceBean loadResource(String resourcePath) {

            ResourceBean loadResource = null;

            PackageInfo info = queryPackageInfo(resourcePath);    //    获取未安装APK的PackageInfo
            if (info != null) {    //   获取成功
                loadResource = mRescources.get(info.packageName);    // 先从缓存中取, 存在则直接返回, 不重复添加. 否则就搜索添加
                if (loadResource == null) {
                    try {
                        AssetManager assetManager = AssetManager.class.newInstance();    // 创建AssetManager实例
                        Class cls = AssetManager.class;
                        Method method = cls.getMethod("addAssetPath", String.class);
                        method.invoke(assetManager, resourcePath);    // 反射设置资源加载路径
                        Resources resources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(),
                                mContext.getResources().getConfiguration());    // 构造出正确的Resource
                        loadResource = new ResourceBean();
                        loadResource.resources = resources;
                        loadResource.packageName = info.packageName;
                        loadResource.classLoader = new DexClassLoader(resourcePath, mDexDir, null,
                                mContext.getClassLoader());    //   设置正确的类加载器, 因为需要去加载R文件
                        mRescources.put(info.packageName, loadResource);    // 缓存
                        LogUtil.w( "build resource:" + resourcePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            LogUtil.w( "load resource:" + resourcePath);
            return loadResource;

        }

        /**
         * 获取未安装应用PackageInfo
         *
         * @param resourcePath
         * @return
         */
        private PackageInfo queryPackageInfo(String resourcePath) {
            return mContext.getPackageManager().getPackageArchiveInfo(resourcePath, PackageManager.GET_ACTIVITIES);
        }

        /**
         * 获取未安装应用LoadResource
         *
         * @param packageName
         * @return
         */
        public ResourceBean getUnInstalledRecource(String packageName) {
            ResourceBean loadResource = mRescources.get(packageName);
            if (loadResource == null) {
                LogUtil.w("resource " + packageName + " not founded");
            }
            return loadResource;
        }

    }


}
