package com.example.yj.application;

import android.app.Application;
import android.content.Context;

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
    }

    /**
     *
     * @return 当前上下文
     */
    public static MyApplication getInstance() {
        return mApplication;
    }

}
