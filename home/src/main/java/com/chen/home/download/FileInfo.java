package com.chen.home.download;

/**
 * Created by PengChen on 2017/12/12.
 */

public class FileInfo {
    private String fileName;//文件名
    private String url;//下载地址
    private long length;//文件大小
    private long finished;//下载以已完成进度
    private boolean isStop = false;//是否暂停下载
    private boolean isDownLoading = false;//是否正在下载
    private int version = 0;//版本号

    public FileInfo(String fileName, String url, int version) {
        this.fileName = fileName;
        this.url = url;
        this.version = version;
    }

    public FileInfo() {
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public boolean isDownLoading() {
        return isDownLoading;
    }

    public void setDownLoading(boolean downLoading) {
        isDownLoading = downLoading;
    }
}
