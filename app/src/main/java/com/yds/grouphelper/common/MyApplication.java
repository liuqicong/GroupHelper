package com.yds.grouphelper.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.yds.grouphelper.common.utils.Utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;

@SuppressLint({ "InflateParams", "NewApi" })
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public void onCreate() {
		super.onCreate();

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Utils.e("异常了==>重启"+ex.toString());
				//restartApplication();
			}
		});

	}
	
	//========================================异常处理=================================================
    private List<Activity> mList = new LinkedList<>();
    public synchronized int activitySize(){return mList.size();}

    public synchronized void addActivity(Activity activity) {
        int size=mList.size();
        if(size>=5){
            for(int i=0;i<size;++i){
                Activity act=mList.get(i);
                if(act==null || act.isFinishing()){
                    mList.remove(i);
                    --i;
                    --size;
                }
            }
        }
        mList.add(activity);
    }

    public synchronized void closeAllActivity() {
        try {
            int size=mList.size();
            for (int i=0;i<size; ++i) {
                Activity activity=mList.get(i);
                if (activity != null && !activity.isFinishing()){
                    activity.finish();
                }
            }
            mList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restartApplication() {
        closeAllActivity();
        Intent k = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        k.addCategory("restart");
        startActivity(k);
        System.exit(0);
    }

	//=================================上下文公有方法==============================================
    private boolean isRunning(Context mContext) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : activityManager.getRunningTasks(Integer.MAX_VALUE)) {
            String calssName=runningTaskInfo.topActivity.getClassName();
            if (calssName.startsWith(mContext.getPackageName()))   return true;
        }
        return false;
    }

	
}
