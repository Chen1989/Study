package com.chen.study.download;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.chen.study.util.LogUtil;

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

    }

    public Context getApplicationContext() {
        return context;
    }

    public void onDestroy() {
        service = null;
        doTask = null;
        taskList.clear();
    }

    /**
     *
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
     */
    static class Task {
        private final String path;
        private final String pluginName;
        private final String pluginUrl;
        private final int pluginVersion;
        private final boolean forceUpdate;
        private final long size;

        public Task(String pluginName, String pluginUrl, int pluginVersion
                , boolean forceUpdate
                , long size, String path) {
            this.pluginUrl = pluginUrl;
            this.pluginVersion = pluginVersion;
            this.forceUpdate = forceUpdate;
            this.pluginName = pluginName;
            this.size = size;
            this.path = path;
        }

        @Override
        public String toString() {
            return "pluginName:" + pluginName + "size:" + size + "--pluginVersion " + pluginVersion + "----pluginUrl:" + pluginUrl;
        }
    }

    private static final Queue<Task> taskList = new LinkedList<>();

    static Task doTask;

    //url,pluginName,pluginVersion,foreUpdate
    public static void startActionDownload(Context context,
                                           String pluginName, String pluginUrl, int pluginVersion, boolean foreUpdate) {
        Intent intent = new Intent(context, DownLoadApkService.class);
        intent.putExtra("pluginName", pluginName);
        intent.putExtra("pluginUrl", pluginUrl);
        intent.putExtra("pluginVersion", pluginVersion);
        intent.putExtra("foreUpdate", foreUpdate);
        intent.setAction("com.core.service.startActionDownload");
        getService().onHandleIntent(intent);
    }

    public static void startActionCCD(Context context) {
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
        for (Map<String, String> map : tasks) {
            Task task = new Task(map.get("pluginName")
                    , map.get("pluginUrl")
                    , Integer.valueOf(map.get("pluginVersion"))
                    , Boolean.valueOf(map.get("foreUpdate"))
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
        String pluginName = data.getString("pluginName");
        String pluginUrl = data.getString("pluginUrl");
        int pluginVersion = data.getInt("pluginVersion");
        boolean forceUpdate = data.getBoolean("forceUpdate");

        if (doTask != null && null != doTask.pluginUrl) {
            if (doTask.pluginUrl.equals(pluginUrl)) {
                LogUtil.i(String.format("has downloading with url:%s||pluginName:%s", pluginUrl, pluginName));
                return;
            }
        }

        File file = new File(getApplicationContext().getDir("apps", Context.MODE_PRIVATE), pluginName + ".app");
        if (isDownLoadCompleted(getApplicationContext(), file)) {
            onDownLoadCompleted(getApplicationContext(), pluginName, file.getPath(), pluginUrl);
            return;
        }

        long size = getSize(pluginUrl);
        boolean isWifi;
        isWifi = isWifi();
        if (!isWifi) {
//            String aaa = OtherSdk.get(context).get("is2G", false+ "");
            String aaa = "";
            if (is2G() && !Boolean.parseBoolean(aaa)) {
                LogUtil.i("can't download by 2g(gprs)");
                saveDownloadInfo(getApplicationContext(), pluginName, pluginUrl, size, file.getPath(), false);
                return;
            }
        }

        String ttt = "";
//        String ttt = OtherSdk.get(context).get("isWifi", false + "");
        if (!Boolean.parseBoolean(ttt)) {//not need wifi,means is allow all
            Task task = new Task(pluginName, pluginUrl, pluginVersion, forceUpdate, size, file.getPath());
            taskList.offer(task);
            sortQueue();
            startDownload();
        } else {
            if (isWifi) {
                Task task = new Task(pluginName, pluginUrl, pluginVersion, forceUpdate, size, file.getPath());
                taskList.offer(task);
                sortQueue();
                startDownload();
            } else {
                LogUtil.i( "only need download by wifi");
                saveDownloadInfo(getApplicationContext(), pluginName, pluginUrl, size, file.getPath(), false);
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
            while (i + 1 < tasks.length - 1 && tasks[i].pluginUrl.equalsIgnoreCase(tasks[i + 1].pluginUrl)) {
                i++;
            }
            taskList.offer(tasks[i]);
        }
        if (!tasks[tasks.length - 2].pluginUrl.equalsIgnoreCase(tasks[tasks.length - 1].pluginUrl)) {
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
                onDownLoadBegin(getApplicationContext(), doTask.pluginUrl, doTask.pluginName, doTask.path);
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
            , final String pluginUrl
            , final String pluginName
            , final String path) {
        final DownLoadClientBase.DownloadTask task = new DownLoadClientBase.DownloadTask();
        task.getData().put("pluginName", pluginName);
//        task.getData().put("referrer", referrer);
        task.setUrl(pluginUrl);
        task.setFlag("ApkDownLoad");
//        task.getData().put("name", doTask.name);
        task.getData().put("size", doTask.size);

        task.setPath(path);

        task.setDownLoadTaskListener(new DownLoadClientBase.DownloadListener() {

            @Override
            public void onProgress(long current, long total) {
                super.onProgress(current, total);
                 LogUtil.i(String.format("current:%s--total:%s",current,total));
            }

            @Override
            public void onCompleted(DownLoadClientBase.DownloadTask task) {
                if (isDownLoadCompleted(context, new File(task.getPath()))) {
                    String pluginName = task.param("pluginName", null);
                    LogUtil.i( "on download completed");
                    onDownLoadCompleted(context, pluginName, task.getPath(), task.getUrl());
                } else {
                    boolean result = retryDownload(context, pluginName, pluginUrl, false, task.param("size", 0l));
                    if (result) {
                        resetTask();
                    } else {
                        doTask = null;
                    }
                }
                LogUtil.i( "on download completed");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                String pluginName = task.param("pluginName", null);
                DownLoadApkService.this.onError(e);
                LogUtil.i("on download error");
                DownLoadApkService.this.onCompleted(context, "download error");

                if (doTask == null) {
                    LogUtil.i("doTask==nul");
                    saveDownloadInfo(getApplicationContext(), pluginName, pluginUrl,
                            task.param("size", 0l), path, false);
                    if (isNetworkAvailable(getApplicationContext())) {
                        resetTask();
                    }
                    return;
                }
                boolean result = retryDownload(context, pluginName, pluginUrl, false, task.param("size", 0l));
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
     * @param pluginName
     * @param pluginUrl
     * @param foreUpdate
     * @param size
     * @return true start next download
     */
    public boolean retryDownload(Context context, String pluginName,
                                 String pluginUrl, boolean foreUpdate, long size) {
        if (doTask==null){
            LogUtil.i("not task ,cancel save");
            return false;
        }
        /**
         * 重试 次数大于1取消
         */
        LogUtil.i("reading for can download");

        if (!isNetworkAvailable(getApplicationContext())) {
            saveDownloadInfo(context, pluginName, pluginUrl, size);
            LogUtil.i( "networkNotAvailable cancel retry download");
            doTask = null;
            while (!taskList.isEmpty()) {
                Task t = taskList.poll();
                if (t == null) return false;
                saveDownloadInfo(context, t.pluginName, t.pluginUrl, t.size, t.path, false);
            }
            return false;
        }

        int errorCount = 0;
        List<Map<String, String>> list =
                DBHelper.getInstance(context).get(down_table, "where pkg = ? and url=?", new String[]{pluginName, pluginUrl});
        if (list.size() > 0) {
            errorCount = Integer.valueOf(list.get(0).get("count"));
            if (errorCount > 0 && !foreUpdate) {
                LogUtil.i( "retry than 1");
                LogUtil.i( "save and wait network change");
                saveDownloadInfo(context, pluginName, pluginUrl, size);
                return true;
            } else {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("pluginName", pluginName);
                values.put("count", "1");
                if (DBHelper.getInstance(context).update(down_table, values,
                        String.format("where pkg='%s' and url='%s'", pluginName, pluginUrl))) {
                    LogUtil.i( "retry 1");
                    onDownLoadBegin(context, pluginUrl, pluginName, doTask.path);
                    return false;
                } else {
                    LogUtil.i( "retry download fail");
                    LogUtil.i("save and wait network change");
                    saveDownloadInfo(context, pluginName, pluginUrl, size);
                    return true;
                }
            }
        } else {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("pluginName", pluginName);
            values.put("count", "1");
            values.put("pluginUrl", pluginUrl);
            values.put("size", size);
            values.put("path", doTask.path);
            values.put("forceUpdate", doTask.forceUpdate);

            if (DBHelper.getInstance(context)
                    .add(down_table, values)
                    || foreUpdate) {
                LogUtil.i( "retry 1");
                onDownLoadBegin(context, pluginUrl, pluginName, doTask.path);
                return false;
            } else {
                LogUtil.i( "retry than 1 or update fail");
                LogUtil.i( "save and wait network change");
                saveDownloadInfo(context, pluginName, pluginUrl, size);
                return true;
            }

        }
    }

    private void saveDownloadInfo(Context context
            , String pluginName
            , String pluginUrl
            , long size) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("pkg", pluginName);
        values.put("count", "0");
        values.put("url", pluginUrl);
        values.put("size", size);
        values.put("path", doTask.path);
        values.put("forceUpdate", false);

        DBHelper.getInstance(context)
                .addOrUpdate(down_table, values, "where pluginName=? and pluginUrl=?", pluginName, pluginUrl);
    }

    private void saveDownloadInfo(Context context
            , String pluginName
            , String pluginUrl
            , long size, String path, boolean forceUpdate) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("pluginName", pluginName);
        values.put("count", "0");
        values.put("pluginUrl", pluginUrl);
        values.put("size", size);
        values.put("path", path);
        values.put("forceUpdate", forceUpdate);

        DBHelper.getInstance(context)
                .addOrUpdate(down_table, values, "where pluginName=? and pluginUrl=?", pluginName, pluginUrl);
    }

    public synchronized void onDownLoadCompleted(final Context context, final String pluginName,
                                                 final String path,
                                                 String pluginUrl) {
        LogUtil.i( "onDownLoadCompleted aaaaaaaa");
        DBHelper.getInstance(context).del(down_table, "pluginName=? and pluginUrl=?", new String[]{pluginName, pluginUrl});
        resetTask();

        FilePermission.set(path, FilePermission.Group_RWX | FilePermission.Other_RWX | FilePermission.User_RWX);

    }

    private void resetTask() {
        doTask = null;
        startDownload();
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
}
