package com.yds.grouphelper.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by LiuQiCong on 2016/5/9.
 */
public final class MyHandler extends Handler {

    private WeakReference<Context> mActivity;
    private HandlerListener listener;

    public MyHandler(Context context,HandlerListener listener) {
        mActivity = new WeakReference<>(context);
        this.listener=listener;
    }

    @Override
    public void handleMessage(Message msg) {
        if(mActivity!=null && listener!=null){
            listener.handlerListener(msg);
        }
    }

    public interface HandlerListener{
        void handlerListener(Message msg);
    }

}
