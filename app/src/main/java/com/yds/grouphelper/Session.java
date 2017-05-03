package com.yds.grouphelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.yds.grouphelper.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;


@SuppressLint("SdCardPath")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class Session extends Observable{

    private Context mContext;
    private volatile static Session mInstance;
    
    private boolean whetherOpen;
    private int sWidth=-1;
    private int sHeight=-1;

	private int mSteps=0;

    private static List<String> phoneList=new ArrayList();
    private static int repeatNumber=0;

    private Session(Context context) {
        synchronized (this) {
            mContext = context;
        }
    }
    
    public static Session getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Session.class){
                if(mInstance==null){
                    mInstance = new Session(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    //=====================================================================================
    public int getScreenWidth(){
		if (sWidth <= 0) {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager windowManager=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
			windowManager.getDefaultDisplay().getMetrics(dm);
			sWidth = dm.widthPixels;
		}
		return sWidth;
    }
    
    public int getScreenHeight(){
		if (sHeight <= 0) {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager windowManager=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
			windowManager.getDefaultDisplay().getMetrics(dm);
			sHeight = dm.heightPixels;
		}
		return sHeight;
    }
    
	private void notifyData(String key,Object data){
		final HashMap<String, Object> notify=new HashMap<String, Object>(1);
		notify.put(key,data);
		super.setChanged();
	    super.notifyObservers(notify);
	}

    //==================================================================================
  	public void openService(boolean wo){
  		whetherOpen=wo;
  		notifyData(Constants.NOTIFY_ONOFF, 0);
  	}
  	
  	public boolean whetherOpen(){
		return whetherOpen;
  	}
  	
  	public void backMain(boolean isInstall){
  		if(isInstall){
  			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					startApp();
				}
			}, 4000);
  			return;
  		}
  		startApp();
  	}

  	private void startApp(){
  		Intent intent=new Intent(mContext,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constants.INTENT_DATA, "");
  		mContext.startActivity(intent);
  	}
  	
  	public void hasInstallApp(){
  		notifyData(Constants.NOTIFY_INSTALLED,0);
  	}
  	
}
