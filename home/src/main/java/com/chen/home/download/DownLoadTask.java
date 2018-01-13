package com.chen.home.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.chen.home.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by PengChen on 2017/12/12.
 */

public class DownLoadTask extends Thread {
    private FileInfo info;
    private SQLiteDatabase db;
    private DBHelper helper;//数据库帮助类
    private long finished = 0;//当前已下载完成的进度
    private int retryCount = 0;
    private DownLoadTaskListener taskListener;
    private Context mContext;

    public DownLoadTask(Context context, FileInfo info, DBHelper helper, DownLoadTaskListener taskListener) {
        mContext = context;
        this.info = info;
        this.helper = helper;
        this.db = helper.getWritableDatabase();
        this.taskListener = taskListener;
    }

    public FileInfo getFileInfo() {
        return info;
    }

    private void downloadFile() {
        getLength();
        HttpURLConnection connection = null;
        RandomAccessFile rwd = null;
        try {
            URL url = new URL(info.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            //从上次下载完成的地方下载
            long start = info.getFinished();
            //设置下载位置(从服务器上取要下载文件的某一段)
            LogUtil.i("start = " + start);
            connection.setRequestProperty("Range", "bytes=" + start + "-" + info.getLength());//设置下载范围
            //设置文件写入位置
            File file = new File(DownLoaderManger.getInstance(mContext).FILE_PATH, info.getFileName());
            rwd = new RandomAccessFile(file, "rwd");
            //从文件的某一位置开始写入
            rwd.seek(start);
            finished = info.getFinished();
            LogUtil.i("AA connection.getResponseCode() = " + connection.getResponseCode());
            if (connection.getResponseCode() == 206 || connection.getResponseCode() == 200) {//文件部分下载，返回码为206
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    //写入文件
                    rwd.write(buffer, 0, len);
                    finished += len;
                    info.setFinished(finished);
                    helper.updateData(db, info);
                    taskListener.onProgress(this, info.getLength(), finished);
                    //更新界面显示
                }
                //下载完成
                LogUtil.i("finished = " + finished + ", 下载完成");
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (rwd != null) {
                        rwd.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                taskListener.downloadSuccess(this);
            }
        } catch (Exception e) {
            LogUtil.i("下载失败 = " + retryCount);
            e.printStackTrace();
            if (retryCount < 3) {
                LogUtil.i("retryCount = " + retryCount);
                retryCount++;
                downloadFile();
            }else {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (rwd != null) {
                        rwd.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                taskListener.downloadFailed(this);
            }
        }
    }

    @Override
    public void run() {
        downloadFile();
    }

    /**
     * 首先开启一个线程去获取要下载文件的大小（长度）
     */
    private void getLength() {
        HttpURLConnection connection = null;
        try {
            //连接网络
            URL url = new URL(info.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(5000);
            int length = -1;
            LogUtil.i("connection.getResponseCode() = " + connection.getResponseCode());
            if (connection.getResponseCode() == 200) {//网络连接成功
                //获得文件长度
                length = connection.getContentLength();
            }
            if (length <= 0) {
                //连接失败
                return;
            }
            //创建文件保存路径
            File dir = new File(DownLoaderManger.getInstance(mContext).FILE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            LogUtil.i("length = " + length);
            info.setLength(length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface DownLoadTaskListener {
        void downloadSuccess(DownLoadTask task);
        void downloadFailed(DownLoadTask task);
        void onProgress(DownLoadTask task, long totalSize, long finishSize);
    }
}
