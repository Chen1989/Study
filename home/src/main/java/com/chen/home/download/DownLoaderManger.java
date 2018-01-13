package com.chen.home.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.chen.home.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PengChen on 2017/12/12.
 */

public class DownLoaderManger {
    public String FILE_PATH;//文件下载保存路径
    private DBHelper helper;//数据库帮助类
    private SQLiteDatabase db;
    private Map<String, FileInfo> map = new HashMap<>();//保存正在下载的任务信息
    private static DownLoaderManger manger;
    private DownLoadTask _task;
    private ArrayList<DownLoadTask> downLoadTasks = new ArrayList<DownLoadTask>();
    private DownLoadTask.DownLoadTaskListener taskListener;
    private Context _context;

    private DownLoaderManger(final Context context) {
        _context = context;
        FILE_PATH = context.getFilesDir() + "/plugins";
        this.helper = new DBHelper(context);
        db = helper.getWritableDatabase();
        taskListener = new DownLoadTask.DownLoadTaskListener() {
            @Override
            public void downloadSuccess(DownLoadTask task) {

                LogUtil.i("downLoadTasks size = " + downLoadTasks.size());
//                Admob.finishDownload(context);
//                helper.deleteData(db, task.getFileInfo());//删除之后无法判断是否下载完成
                downLoadTasks.remove(task);
                if (!downLoadTasks.isEmpty()) {
                    _task = downLoadTasks.get(0);
                    _task.start();
                } else {
                    helper.close();
                    db.close();
                    _task = null;
                    manger = null;
                }
            }

            @Override
            public void downloadFailed(DownLoadTask task) {

                LogUtil.i("downLoadTasks size = " + downLoadTasks.size());
                downLoadTasks.remove(task);
//                File file = new File(FILE_PATH, task.getFileInfo().getFileName());
//                file.delete();
                if (!downLoadTasks.isEmpty()) {
                    _task = downLoadTasks.get(0);
                    _task.start();
                } else {
                    helper.close();
                    db.close();
                    _task = null;
                    manger = null;
                }
            }

            @Override
            public void onProgress(DownLoadTask task, long totalSize, long finishSize) {
                LogUtil.i("totalSize = " + totalSize + " finishSize = " + finishSize);
            }
        };
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static synchronized DownLoaderManger getInstance(Context context) {
        if (manger == null) {
            synchronized (DownLoaderManger.class) {
                if (manger == null) {
                    manger = new DownLoaderManger(context);
                }
            }
        }
        return manger;
    }

    //下载完成并且文件存在,或者存在没有下载
    public boolean fileAvailable(String fileName) {
        FileInfo info = helper.queryDataByName(db, fileName);
        if (info != null) {
            File file = new File(FILE_PATH, fileName);
            if (info.getFinished() == info.getLength() && file.exists() && file.length() == info.getLength()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开始下载任务
     */

    public void start(String name, String url, int version) {
        FileInfo fileInfo = new FileInfo(name, url, version);
        //判断是否需要下载更新
        if (isUpdatePlugin(fileInfo)) {
            addTask(fileInfo);
            //开始任务下载
            if (_task == null) {
                _task = new DownLoadTask(_context, map.get(url), helper, taskListener);
                downLoadTasks.add(_task);
                _task.start();
            } else {
                downLoadTasks.add(new DownLoadTask(_context, map.get(url), helper, taskListener));
            }
        }
    }

    public boolean isUpdatePlugin(FileInfo info) {
        File file = new File(FILE_PATH, info.getFileName());
        if (file.exists()) {
            int currentVersionCode = 0;
            FileInfo dbFileInfo = helper.queryDataByName(db, info.getFileName());
            if (dbFileInfo != null) {
                currentVersionCode = dbFileInfo.getVersion();
            } else {
                return true;
            }

            if (info.getVersion() > currentVersionCode) {
                return true;
            } else if (info.getVersion() == currentVersionCode){
                if (dbFileInfo != null && dbFileInfo.getLength() > dbFileInfo.getFinished()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 添加下载任务
     *
     * @param info 下载文件信息
     */
    public void addTask(FileInfo info) {
        //判断数据库是否已经存在这条下载信息
        if (!helper.isExist(db, info)) {
            helper.insertData(db, info);
        } else {
            //从数据库获取最新的下载信息
            FileInfo dbFileInfo = helper.queryDataByName(db, info.getFileName());
            if (info.getVersion() > dbFileInfo.getVersion()) {
                helper.updateData(db, info);
            } else if (info.getVersion() == dbFileInfo.getVersion()){
                //版本号相等，文件大小和已下载大小相等，说明可以断点续传
                File file = new File(FILE_PATH, info.getFileName());
                if (file.length() == dbFileInfo.getFinished()) {
                    info = dbFileInfo;
                }
            }
        }
        map.put(info.getUrl(), info);
    }

    public int getFileVersion(String fileName) {
        FileInfo dbFileInfo = helper.queryDataByName(db, fileName);
        if (dbFileInfo != null) {
            return dbFileInfo.getVersion();
        } else {
            return 0;
        }
    }
}
