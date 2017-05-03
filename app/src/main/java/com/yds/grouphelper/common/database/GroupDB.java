package com.yds.grouphelper.common.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class GroupDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "group.db";
    private static final int VERSION = 1;
    //收货地址表
    public static final String TABLE_INVITATION="invitation";
    public static final String CREAT_TABLE_INVITATION = "create table invitation("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "content TEXT,"
            + "state INTEGER);";



    public GroupDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //第一次创建的时候调用
        db.execSQL(CREAT_TABLE_INVITATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //升级数据库
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        //创建或打开一个数据库
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        //创建或打开一个可以读写的数据库
        return super.getWritableDatabase();
    }

    //========================================================================================
    /**
     * 判断某张表是否存在
     */
    public boolean tableIsExist(String tabName) {
        if (tabName == null) return false;
        boolean result = false;

        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void creatTable(String sql){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
        db.close();
    }

}
