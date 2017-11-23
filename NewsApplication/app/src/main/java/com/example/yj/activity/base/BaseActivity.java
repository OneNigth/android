package com.example.yj.activity.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.yj.constant.Constant;

/**
 * Created by yj on 2017/9/15.
 * Activity基础类
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * 提供日志TAG
     */
    public String TAG ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        TAG = getComponentName().getShortClassName();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 申请指定权限
     * @param requestcode
     * @param permissions
     */
    public void requestPermission(int requestcode ,String... permissions){
        ActivityCompat.requestPermissions(this,permissions,requestcode);
    }

    /**
     * 判断有无指定权限
     * @param permissions
     * @return
     */
    public boolean hasPermission(String... permissions){
        for (String permission :permissions){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    //用户允许权限后回调此方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant.WRITE_READ_EXTERNAL_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doSDCardPermission();//处理sd卡业务
                }
                break;
        }
    }

    /**
     * 处理sd卡业务------空
     */
    private void doSDCardPermission() {

    }

}
