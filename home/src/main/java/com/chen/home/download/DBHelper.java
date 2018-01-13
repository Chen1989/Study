package com.chen.home.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by PengChen on 2017/12/12.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";
    public static String TABLE_NAME = "plugin_file";//表名
    private static final int VERSION = 1;
    private static final String SQL_CREATE = "create table if not exists " +TABLE_NAME + " (fileName varchar,url varchar,length integer,finished integer,version integer)";
    private static final String SQL_DROP = "drop table if exists " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }

    /**
     * 插入一条下载信息
     */
    public void insertData(SQLiteDatabase db, FileInfo info) {
        ContentValues values = new ContentValues();
        values.put("fileName", info.getFileName());
        values.put("url", info.getUrl());
        values.put("length", info.getLength());
        values.put("finished", info.getFinished());
        values.put("version", info.getVersion());
        db.insert(TABLE_NAME, null, values);
    }

    /**
     * 是否已经插入这条数据
     */
    public boolean isExist(SQLiteDatabase db, FileInfo info) {
        Cursor cursor = db.query(TABLE_NAME, null, "url = ?", new String[]{info.getUrl()}, null, null, null, null);
        boolean exist = cursor.moveToNext();
        cursor.close();
        return exist;
    }

    /**
     * 查询已经存在的一条信息
     */
    public FileInfo queryData(SQLiteDatabase db, String url) {
        Cursor cursor = db.query(TABLE_NAME, null, "url = ?", new String[]{url}, null, null, null, null);
        FileInfo info = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                long length = cursor.getLong(cursor.getColumnIndex("length"));
                long finished = cursor.getLong(cursor.getColumnIndex("finished"));
                int version = cursor.getInt(cursor.getColumnIndex("version"));
                info = new FileInfo();
                info.setStop(false);
                info.setFileName(fileName);
                info.setUrl(url);
                info.setLength(length);
                info.setFinished(finished);
                info.setVersion(version);
            }
            cursor.close();
        }
        return info;
    }

    /**
     * 查询已经存在的一条信息
     */
    public FileInfo queryDataByName(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_NAME, null, "fileName = ?", new String[]{name}, null, null, null, null);
        FileInfo info = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long length = cursor.getLong(cursor.getColumnIndex("length"));
                long finished = cursor.getLong(cursor.getColumnIndex("finished"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                int version = cursor.getInt(cursor.getColumnIndex("version"));
                info = new FileInfo();
                info.setStop(false);
                info.setFileName(name);
                info.setUrl(url);
                info.setLength(length);
                info.setFinished(finished);
                info.setVersion(version);
            }
            cursor.close();
        }
        return info;
    }

    public void updateData(SQLiteDatabase db, FileInfo info) {
        ContentValues values = new ContentValues();
        values.put("fileName", info.getFileName());
        values.put("url", info.getUrl());
        values.put("length", info.getLength());
        values.put("finished", info.getFinished());
        values.put("version", info.getVersion());
        //修改条件
        String whereClause = "url = ?";
        //修改添加参数
        String[] whereArgs={info.getUrl()};
        db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public void deleteData(SQLiteDatabase db, FileInfo info) {
        String whereClause = "url = ?";
        String[] whereArgs = {info.getUrl()};
        db.delete(TABLE_NAME, whereClause, whereArgs);
//        String SQLString = "delete from " + TABLE_NAME + " where url = " + info.getUrl();
//        db.execSQL(SQLString);
    }

    /**
     * 恢复一条下载信息
     */
    public void resetData(SQLiteDatabase db, String url) {
        ContentValues values = new ContentValues();
        values.put("finished", 0);
        values.put("length", 0);
        db.update(TABLE_NAME, values, "url = ?", new String[]{url});
    }
}
