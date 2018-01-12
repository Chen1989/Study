package com.chen.study.download;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.chen.study.util.LogUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author longyu
 */
public class DownLoadApkService {

    private String down_table = "down_table";

    private Context context;

    private static DownLoadApkService service;
    private DownLoadApkService() {

    }

    public static DownLoadApkService getService() {
        if (null == service)
            service = new DownLoadApkService();
        return service;
    }

    public void onCreate(final Context context) {
        this.context = context;
        _installMap = new LaunchMap(getApplicationContext());

    }

    public Context getApplicationContext() {
        return context;
    }

    public void onDestroy() {
        service = null;
        doTask = null;
        taskList.clear();
    }

    static class Task {
        private final String path;
        private final String pkg;
        private final String url;
        private final String referrer;
        private final boolean forceUpdate;
        private final String name;
        private final long size;

        public Task(String pkg, String url
                , String referrer
                , boolean forceUpdate
                , String name
                , long size, String path) {
            this.pkg = pkg;
            this.url = url;
            this.referrer = referrer;
            this.forceUpdate = forceUpdate;
            this.name = name;
            this.size = size;
            this.path = path;
        }

        @Override
        public String toString() {
            return "pkg:" + pkg + "size:" + size + "----url:" + url;
        }
    }

    private static final Queue<Task> taskList = new LinkedList<>();

    static Task doTask;

    public static void startActionDownload(Context context
            , String pkg
            , String url
            , String referrer
            , boolean foreUpdate
            , String name) {
        Intent intent = new Intent(context, DownLoadApkService.class);
        intent.putExtra("pkg", pkg);
        intent.putExtra("url", url);
        intent.putExtra("referrer", referrer);
        intent.putExtra("foreUpdate", foreUpdate);
        intent.putExtra("name", name);
        intent.setAction("com.core.service.startActionDownload");
        getService().onHandleIntent(intent);
    }

    public static void startActionCCD(Context context
    ) {
        Intent intent = new Intent(context, DownLoadApkService.class);
        intent.setAction("com.core.service.startActionConnectedChangeDownload");
        context.startService(intent);
        getService().onHandleIntent(intent);
    }

    protected void onHandleIntent(final Intent intent) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (intent != null) {
                    if (intent.getAction().equals("com.core.service.startActionConnectedChangeDownload")) {
                        handleActionConnectedChange();
                    } else if (intent.getAction().equals("com.core.service.startActionDownload")) {
                        handleActionDownload(intent.getExtras());
                    }
                }
            }
        }.start();

    }

    private void handleActionConnectedChange() {
        boolean isWifi;
        isWifi = isWifi();

        List<Map<String, String>> tasks = DBHelper.getInstance(getApplicationContext()).get(down_table, "where count=? ORDER BY size desc limit 10", new String[]{"0"});
        for (Map<String, String> map : tasks
                ) {
            Task task = new Task(map.get("pkg")
                    , map.get("url")
                    , map.get("referrer")
                    , Boolean.valueOf(map.get("foreUpdate"))
                    , map.get("name")
                    , Long.valueOf(map.get("size")), map.get("path"));
            taskList.offer(task);
        }

        LogUtil.i("net connected,download task：" + tasks.size());

        if (tasks.size() > 0) {
            sortQueue();
            startDownload();
        }
    }

    private void handleActionDownload(Bundle data) {
        String pkg = data.getString("pkg");
        String url = data.getString("url");
        String referrer = data.getString("referrer");
        boolean forceUpdate = data.getBoolean("forceUpdate");
        String name = data.getString("name");

        if (doTask != null && null != doTask.url && null != doTask.referrer) {
            if (doTask.url.equals(url) && doTask.referrer.equals(referrer)) {
                LogUtil.i(String.format("has downloading with url:%s||referrer:%s", url, referrer));
                return;
            }
        }

        File file = new File(getApplicationContext().getDir("apps", Context.MODE_PRIVATE), pkg + ".app");
        if (isDownLoadCompleted(getApplicationContext(), file)) {
            onDownLoadCompleted(getApplicationContext(), pkg, referrer, file.getPath(), name, url);
            return;
        }

        long size = getSize(url);
        boolean isWifi;
        isWifi = isWifi();
        if (!isWifi) {
//            String aaa = OtherSdk.get(context).get("is2G", false+ "");
            String aaa = "";
            if (is2G() && !Boolean.parseBoolean(aaa)) {
                LogUtil.i("can't download by 2g(gprs)");
                saveDownloadInfo(getApplicationContext(), pkg, url, referrer, name, size, file.getPath(), false);
                return;
            }
        }

        String ttt = "";
//        String ttt = OtherSdk.get(context).get("isWifi", false + "");
        if (!Boolean.parseBoolean(ttt)) {//not need wifi,means is allow all
            Task task = new Task(pkg, url, referrer, forceUpdate, name, size, file.getPath());
            taskList.offer(task);
            sortQueue();
            startDownload();
        } else {
            if (isWifi) {
                Task task = new Task(pkg, url, referrer, forceUpdate, name, size, file.getPath());
                taskList.offer(task);
                sortQueue();
                startDownload();
            } else {
                LogUtil.i( "only need download by wifi");
                saveDownloadInfo(getApplicationContext(), pkg, url, referrer, name, size, file.getPath(), false);
            }
        }
    }

    private void sortQueue() {
        Task[] tasks = taskList.toArray(new Task[]{});
        Arrays.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task, Task t1) {
                if (task.size > t1.size)
                    return 1;
                if (task.size == t1.size)
                    return 0;
                return -1;
            }
        });
        taskList.clear();
        if (tasks.length == 1) {
            taskList.offer(tasks[0]);
            return;
        }
        for (int i = 0; i < tasks.length - 1; i++) {
            while (i + 1 < tasks.length - 1 && tasks[i].url.equalsIgnoreCase(tasks[i + 1].url)) {
                i++;
            }
            taskList.offer(tasks[i]);
        }
        if (!tasks[tasks.length - 2].url.equalsIgnoreCase(tasks[tasks.length - 1].url)) {
            taskList.offer(tasks[tasks.length - 1]);
        }
    }

    private boolean is2G() {
        return !isFastMobileNetwork();
    }

    /**
     * @return true not is 2g net
     */
    private boolean isFastMobileNetwork() {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    private long getSize(String url) {

        try {
            return DownLoader.getContentLength(url, new HashMap<String, String>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return info.getType() == ConnectivityManager.TYPE_WIFI;
                }
            }
        }
        return false;
    }

    private void startDownload() {
        if (doTask == null) {
            doTask = taskList.poll();
            if (doTask != null) {
                onDownLoadBegin(getApplicationContext(), doTask.url, doTask.pkg, doTask.referrer, doTask.path);
                LogUtil.i( "start download:" + doTask);
            }
        } else {
            LogUtil.i("downloading……");
            LogUtil.i("downloading:" + doTask.toString());
        }
    }

    private boolean isDownLoadCompleted(Context context, File file) {
        File newFile = new File(file.getPath() + ".apk");
        file.renameTo(newFile);

        try {
            PackageInfo info = context.getPackageManager().getPackageArchiveInfo(newFile.getPath(), 0);

            if (info != null) {
                newFile.renameTo(file);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        file.delete();

        return false;
    }

    public void onDownLoadBegin(final Context context
            , final String url
            , final String pkg
            , final String referrer
            , final String path) {
        final DownLoadClientBase.DownloadTask task = new DownLoadClientBase.DownloadTask();
        task.getData().put("pkg", pkg);
        task.getData().put("referrer", referrer);
        task.setUrl(url);
        task.setFlag("ApkDownLoad");
        task.getData().put("name", doTask.name);
        task.getData().put("size", doTask.size);

        task.setPath(path);

        task.setDownLoadTaskListener(new DownLoadClientBase.DownloadListener() {

            @Override
            public void onProgress(long current, long total) {
                super.onProgress(current, total);
                // Logger.i(String.format("current:%s--total:%s",current,total));
            }

            @Override
            public void onCompleted(DownLoadClientBase.DownloadTask task) {
                if (isDownLoadCompleted(context, new File(task.getPath()))) {
                    String pkg = task.param("pkg", null);
                    String referrer = task.param("referrer", null);
                    LogUtil.i( "on download completed");
                    onDownLoadCompleted(context, pkg, referrer, task.getPath(), task.param("name", "unknown"), task.getUrl());
                } else {
                    boolean result = retryDownload(context, pkg, url, referrer, false, task.param("size", 0l));
                    if (result) {
                        resetTask();
                    } else {
                        doTask = null;
                    }
                }

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                String pkg = task.param("pkg", null);
                DownLoadApkService.this.onError(e);
                LogUtil.i("on download error");
                DownLoadApkService.this.onCompleted(context, "download error");

                if (doTask == null) {
                    LogUtil.i("doTask==nul");
                    saveDownloadInfo(getApplicationContext(), pkg, url, referrer
                            , task.param("name", "unknown"),
                            task.param("size", 0l), path, false);
                    if (isNetworkAvailable(getApplicationContext())) {
                        resetTask();
                    }
                    return;
                }
                boolean result = retryDownload(context, pkg, url, referrer, false, task.param("size", 0l));
                if (result) {
                    resetTask();
                } else {
                    doTask = null;
                }
            }
        });

        getDownLoader().download(task);
    }

    private DownLoadClientBase getDownLoader() {
        return DownLoadClientBase.createStatic();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param context
     * @param pkg
     * @param url
     * @param referrer
     * @param foreUpdate
     * @param size
     * @return true start next download
     */
    public boolean retryDownload(Context context, String pkg,
                                 String url, String referrer
            , boolean foreUpdate, long size) {
        if (doTask==null){
            LogUtil.i("not task ,cancel save");
            return false;
        }
        /**
         * 重试 次数大于1取消
         */
        LogUtil.i("reading for can download");

        if (!isNetworkAvailable(getApplicationContext())) {
            saveDownloadInfo(context, pkg, url, referrer, size);
            LogUtil.i( "networkNotAvailable cancel retry download");
            doTask = null;
            while (!taskList.isEmpty()) {
                Task t = taskList.poll();
                if (t == null) return false;
                saveDownloadInfo(context, t.pkg, t.url, t.referrer, t.name, t.size, t.path, false);
            }
            return false;
        }

        int errorCount = 0;
        List<Map<String, String>> list = DBHelper.getInstance(context).get(down_table, "where pkg = ? and url=?", new String[]{pkg, url});
        if (list.size() > 0) {
            errorCount = Integer.valueOf(list.get(0).get("count"));
            if (errorCount > 0 && !foreUpdate) {
                LogUtil.i( "retry than 1");
                LogUtil.i( "save and wait network change");
                saveDownloadInfo(context, pkg, url, referrer, size);
                return true;
            } else {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("pkg", pkg);
                values.put("count", "1");
                if (DBHelper.getInstance(context).update(down_table, values, String.format("where pkg='%s' and url='%s'", pkg, url))) {
                    LogUtil.i( "retry 1");
                    onDownLoadBegin(context, url, pkg, referrer, doTask.path);
                    return false;
                } else {
                    LogUtil.i( "retry download fail");
                    LogUtil.i("save and wait network change");
                    saveDownloadInfo(context, pkg, url, referrer, size);
                    return true;
                }
            }
        } else {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("pkg", pkg);
            values.put("count", "1");
            values.put("referrer", referrer);
            values.put("url", url);
            values.put("name", doTask.name);
            values.put("size", size);
            values.put("path", doTask.path);
            values.put("forceUpdate", doTask.forceUpdate);

            if (DBHelper.getInstance(context)
                    .add(down_table, values)
                    || foreUpdate) {
                LogUtil.i( "retry 1");
                onDownLoadBegin(context, url, pkg, referrer, doTask.path);
                return false;
            } else {
                LogUtil.i( "retry than 1 or update fail");
                LogUtil.i( "save and wait network change");
                saveDownloadInfo(context, pkg, url, referrer, size);
                return true;
            }

        }
    }

    private void saveDownloadInfo(Context context
            , String pkg
            , String url
            , String referrer
            , long size) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("pkg", pkg);
        values.put("count", "0");
        values.put("referrer", referrer);
        values.put("url", url);
        values.put("name", doTask.name);
        values.put("size", size);
        values.put("path", doTask.path);
        values.put("forceUpdate", false);

        DBHelper.getInstance(context)
                .addOrUpdate(down_table, values, "where pkg=? and url=?", pkg, url);
    }

    private void saveDownloadInfo(Context context
            , String pkg
            , String url
            , String referrer
            , String name
            , long size, String path, boolean forceUpdate) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("pkg", pkg);
        values.put("count", "0");
        values.put("referrer", referrer);
        values.put("url", url);
        values.put("name", name);
        values.put("size", size);
        values.put("path", path);
        values.put("forceUpdate", true);

        DBHelper.getInstance(context)
                .addOrUpdate(down_table, values, "where pkg=? and url=?", pkg, url);
    }

    public synchronized void onDownLoadCompleted(final Context context, final String pkg,
                                                 final String referrer,
                                                 final String path,
                                                 final String adName,
                                                 String url) {

        String platformType = "";
        if (!adName.isEmpty()){
//            platformType = CommonUtil.getIntFromPlatform(context,adName);

        }
//        final String adType = SharePreUtil.getString("mSelectedType");
//        final String adPlacementId = OtherSdk.get(context).get(adName + adType, "");

        LaunchInfo info = new LaunchInfo();
        info.PackageName = pkg;
        info.Referrer = referrer;
        info.adName = adName;
        _installMap.save(info);
        DBHelper.getInstance(context).del(down_table, "pkg=? and url=?", new String[]{pkg, url});
        resetTask();

        FilePermission.set(path, FilePermission.Group_RWX | FilePermission.Other_RWX | FilePermission.User_RWX);

        LogUtil.i( "begin install " + pkg);
        LogUtil.i("当前下载完成广告平台：" + adName);
        LogUtil.i( "当前path：" + path);
    }

    public static void saveGPLaunchInfo(String packageName, String adName) {
        LaunchInfo info = new LaunchInfo();
        info.PackageName = packageName;
        info.adName = adName;
        _installMap.save(info);
    }

    private void resetTask() {
        doTask = null;
        startDownload();
    }

    private static LaunchMap _installMap;

    public class LaunchMap {
        private SharedPreferences _ps;

        public LaunchMap(Context context) {
            _ps = context.getSharedPreferences("LaunchInfo", Context.MODE_PRIVATE);
        }

        public void save(LaunchInfo info) {
            try {
                JSONObject json = new JSONObject();
                json.put("pkg", info.PackageName);
                json.put("ref", info.Referrer);
                json.put("adName", info.adName);
                _ps.edit().putString(info.PackageName, json.toString()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public LaunchInfo getAndDelete(String pkg) {
            LaunchInfo info = get(pkg);
            _ps.edit().remove(pkg).apply();

            return info;
        }

        public LaunchInfo get(String pkg) {
            try {
                String v = _ps.getString(pkg, null);
                if (v != null) {
                    JSONObject json = new JSONObject(v);

                    LaunchInfo info = new LaunchInfo();
                    info.PackageName = json.optString("pkg", null);
                    info.Referrer = json.optString("ref", null);
                    info.adName = json.optString("adName", null);
                    if (info.PackageName == null)
                        return null;
                    else
                        return info;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void sendReferrer(final Context context, final String pkg, final String referrer) {
        Intent inte = new Intent("com.android.vending.INSTALL_REFERRER");
        inte.setPackage(pkg);
        inte.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        inte.putExtra("referrer", referrer);
        context.sendBroadcast(inte);
    }


    public void onCompleted(Context context, String type) {
        LogUtil.i( "flow completed because " + type);
    }

    public void onError(Throwable e) {
        e.printStackTrace();
    }

    public static class LaunchInfo {
        public String PackageName;
        public String Referrer;
        public String adName;
    }
}
