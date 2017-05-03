package com.yds.grouphelper.common;

import java.util.ArrayList;

public final class API {

    public static final String BASE_HOST = "http://finger.shouzhiduobao.com";
    //public static final String BASE_HOST = "http://test.shouzhiduobao.com:80";
    public static final String MODULE_NAME="/app5/";

	public static final ApiAction ACTION_KEEP_ONLINE=new ApiAction("getCellPhoneCheckCode");

    public static ArrayList<Integer> API_CACHE_MAP = new ArrayList<>();
    static {

    }
    
    public static ArrayList<Integer> API_SHOW_INFO = new ArrayList<>();
    static {
    	
    }

    //=============================================================================
    public final static class ApiAction{
    	
    	public int id;
        public String allName;
    	private static int index=0;
    	
    	ApiAction(String name){
    		this.id=(++index);
            this.allName= MODULE_NAME + name;
    	}
    }
    
	
}