package com.example.yj.constant;

import android.Manifest;
import android.os.Environment;

/**
 * Created by yj on 2017/11/11.
 */

public class Constant {
    /**
     * 推送
     */
    public static final String FROM_JPUSH = "fromPush";
    public static final String PUSH_MESSAGE = "pushMessage";
    /**
     * 权限常量相关
     */
    public static final int WRITE_READ_EXTERNAL_CODE = 0x01;
    public static final String[] WRITE_READ_EXTERNAL_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final int HARDWEAR_CAMERA_CODE = 0x02;
    public static final String[] HARDWEAR_CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

    //整个应用文件下载保存路径
    public static String APP_PHOTO_DIR = Environment.
            getExternalStorageDirectory().getAbsolutePath().
            concat("/imooc_business/photo/");
}
