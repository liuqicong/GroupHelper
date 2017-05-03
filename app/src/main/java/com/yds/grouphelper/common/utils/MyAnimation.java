package com.yds.grouphelper.common.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public final class MyAnimation {

	public static void clickAnim(final View view,final MyAnimListner Listener){
        ScaleAnimation scaleAnim;
        if(view.getWidth()>200){
            scaleAnim=new ScaleAnimation(0.9f,1.02f,0.9f,1.1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        }else{
            scaleAnim=new ScaleAnimation(0.9f,1.15f,0.9f,1.15f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        }
		scaleAnim.setDuration(200);
		scaleAnim.setInterpolator(new LinearInterpolator());
		scaleAnim.setFillBefore(true);
		scaleAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(Listener != null) Listener.animEnd(view);
			}
		});
        view.startAnimation(scaleAnim);
	}
	
	
	public static void chooseAnim(final View view,final MyAnimListner Listener){
		ScaleAnimation scaleAnim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		scaleAnim.setDuration(200);
		scaleAnim.setInterpolator(new LinearInterpolator());
		scaleAnim.setFillBefore(true);
		scaleAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(Listener != null) Listener.animEnd(view);
			}
		});
        view.startAnimation(scaleAnim);
	}
	
	
	public static void showImgAnim(View view){
		ScaleAnimation scaleAnim=new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		scaleAnim.setDuration(300);
		//scaleAnim.setInterpolator(new AccelerateInterpolator());
        scaleAnim.setInterpolator(new LinearInterpolator());
		scaleAnim.setFillBefore(true);
        view.startAnimation(scaleAnim);
	}


    public static void clickRotateAnim(final View view,final MyAnimListner Listener){
        RotateAnimation rotateAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnim.setDuration(200);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setFillBefore(true);
        rotateAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if(Listener != null) Listener.animEnd(view);
            }
        });
        view.startAnimation(rotateAnim);
    }
	
	
	public interface MyAnimListner{
		void animEnd(View view);
	} 
	
}
