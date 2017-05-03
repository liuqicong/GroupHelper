package com.yds.grouphelper.common;

import android.text.TextUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * API响应缓存管理器
 * 使用SoftReference管理缓存
 *
 */
public final class ResponseCacheManager {

    private  volatile static ResponseCacheManager mInstance;
    private HashMap<String, Object> mResponsePool;
    private SoftReference<HashMap<String, Object>> mResponseCache;

    private ResponseCacheManager() {
        mResponsePool = new HashMap<>();
        mResponseCache = new SoftReference<>(mResponsePool);
    }

    public static ResponseCacheManager getInstance() {
        if (mInstance == null) {
            synchronized (ResponseCacheManager.class){
                if(mInstance==null){
                    mInstance = new ResponseCacheManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 从缓存中获取API访问结果
     */
    public Object getResponse(String key) {
        if (TextUtils.isEmpty(key) || mResponseCache == null) {
            return null;
        }
        return mResponseCache.get().get(key);
    }

    /**
     * 缓存API访问结果
     */
    public void putResponse(String key, Object value) {
        if(mResponseCache != null) {
            mResponseCache.get().put(key, value);
        }
    }

    /**
     * 清除所有API缓存
     */
    public void clear() {
        if (mResponseCache != null) {
            mResponseCache.clear();
            mResponseCache = null;
        }
        if (mResponsePool != null) {
            mResponsePool.clear();
            mResponsePool = null;
        }
        mInstance = null;
    }

}