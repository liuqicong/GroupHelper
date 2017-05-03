package com.yds.grouphelper.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.yds.grouphelper.common.API;
import com.yds.grouphelper.common.MyHttp;

import java.util.TreeMap;

/**
 * Created by Administrator on 2016/11/6.
 */

public class HeartbeatService extends Service implements MyHttp.HttpListener{

    private final static long DELAY=10000;

    @Override
    public void onCreate() {
        super.onCreate();
        handler.sendEmptyMessageDelayed(0,DELAY);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            keepOnline();
            handler.sendEmptyMessageDelayed(0,DELAY);
        }
    };

    private void keepOnline() {
        TreeMap<String, Object> params = new TreeMap<>();
        new MyHttp(getApplicationContext()).AsynPost(this, API.ACTION_KEEP_ONLINE, params);
    }


    @Override
    public void onHttpSuccess(int actionID, String result) {

    }

    @Override
    public void onHttpFail(int actionID, int errorCode, String whyFail) {

    }
}
