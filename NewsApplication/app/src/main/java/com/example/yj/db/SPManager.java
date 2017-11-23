package com.example.yj.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yj.application.MyApplication;

/**
 * Created by yj on 2017/11/9.
 */

public class SPManager{
    private static SPManager spManager ;

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private static final String VIDEO_SP_NAME = "video.pro";//文件名
    public static final String VIDEO_SETTING = "video_setting";//视频播放设置


    public static SPManager getInstance(){
        if(spManager == null){
            spManager = new SPManager();

        }
        return spManager;
    }

    private SPManager(){
        sp = MyApplication.getInstance().getSharedPreferences(VIDEO_SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, int defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public boolean isKeyExist(String key) {
        return sp.contains(key);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }
}
