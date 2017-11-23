package com.example.yj.application;

import android.app.Application;

import com.example.yj.share.ShareManager;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by yj on 2017/9/15.
 *    提供当前上下文
 *    初始化工作
 */
public class MyApplication extends Application {

    private static MyApplication mApplication = null ;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initShareSDK();
        initJPush();
    }

    private void initShareSDK() {
        ShareManager.initShareSDK(this);
    }
    //极光推送sdk
    private void initJPush(){
        JPushInterface.setDebugMode(true);//打包时改为false
        JPushInterface.init(this);
    }
    /**
     *
     * @return 当前上下文
     */
    public static MyApplication getInstance() {
        return mApplication;
    }


}
