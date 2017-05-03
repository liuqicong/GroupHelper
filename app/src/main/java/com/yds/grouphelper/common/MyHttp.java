package com.yds.grouphelper.common;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.yds.grouphelper.common.API.ApiAction;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yds.grouphelper.common.utils.MyHandler;
import com.yds.grouphelper.common.utils.MyHandler.HandlerListener;
import com.yds.grouphelper.common.utils.Utils;

import java.util.TreeMap;

public final class MyHttp implements HandlerListener {
	
	private static final int TIME_OUT_DELAY = 8000;

	private Context mContext;

	private HttpListener listener;
	private ApiAction apiAction;
	private boolean cacheAPI;

    private MyHandler handler;

	public MyHttp(Context context) {
		 this.mContext=context;
        handler=new MyHandler(mContext,this);
	}
	
	public void AsynPost(HttpListener listener, ApiAction apiAction,TreeMap<String, Object> treeMap) {
		AsynInit(listener, apiAction,treeMap);
	}
	
	public void AsynPost(HttpListener listener, ApiAction apiAction){
		AsynInit(listener, apiAction,new TreeMap<String, Object>());
	}
	
	private void AsynInit(HttpListener listener, ApiAction apiAction, TreeMap<String, Object> treeMap){
		 if(mContext instanceof Activity){
			 try{
                 if(((Activity)mContext).isFinishing()) return;
			}catch(Exception e){
                 e.printStackTrace();
             }
		 }

        RequestParams params=getRequestParams(treeMap,apiAction.allName);
		
		Object result;
		this.listener = listener;
		this.apiAction=apiAction;

		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
		client.addHeader("Accept-Encoding", "gzip");
        //client.setConnectTimeout(TIME_OUT_DELAY);//连接超时
		client.setTimeout(TIME_OUT_DELAY);//读取超时
		client.post(API.BASE_HOST+apiAction.allName, params, new AsyncHttpResponseHandler() {

			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if(handler!=null){
                    Message msg=new Message();
                    msg.what=arg0;
                    msg.obj=new String(arg2);
                    handler.sendMessage(msg);
                }
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,Throwable arg3) {
                if(handler!=null){
                    Message msg=new Message();
                    msg.what=1;
                    msg.obj="网络不通畅";
                    handler.sendMessage(msg);
                }
			}
		});
	}

	public void accessFilure(int errorCode,String whyFail){
		if(errorCode==2){
			whyFail="登录失效，请重新登录";

		}else if(errorCode==404){
			whyFail="服务器没有响应";
		}
		if(null!=listener){
            listener.onHttpFail(apiAction.id, errorCode, whyFail);
        }
	}

    private RequestParams getRequestParams(TreeMap<String, Object> treeMap,String methodName){
        RequestParams params=new RequestParams();
        for (String key : treeMap.keySet()) {
            String value=treeMap.get(key).toString();
            params.put(key, value);
        }
        return params;
    }

    @Override
    public void handlerListener(Message msg) {
        try{
            handler.removeCallbacksAndMessages(null);
            if (listener != null) {
                int code=msg.what;
                String result=(String) msg.obj;
                if(code==200){
                    try {
                        JSONObject obj = new JSONObject(result);
                        int errcode = obj.getInt("errcode");
                        if (errcode == 0) {
                            if(API.API_SHOW_INFO.contains(apiAction.id)){
                                Utils.show(mContext, obj.getString("info"));
                            }
                            String data=obj.has("data")?obj.getString("data"):result;
                            listener.onHttpSuccess(apiAction.id, data);
                        } else {
                            accessFilure(errcode, obj.getString("info"));
                        }
                    } catch (JSONException e) {
                        if(TextUtils.isEmpty(result)){
                            result="服务器异常，请联系管理员";
                        }
                        accessFilure(code, result);
                    }
                }else{
                    accessFilure(code, result);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public interface HttpListener {
		void onHttpSuccess(int actionID, String result);
		void onHttpFail(int actionID, int errorCode, String whyFail);
	}
	
}
