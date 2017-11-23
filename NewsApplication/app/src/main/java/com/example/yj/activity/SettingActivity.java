package com.example.yj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.constant.SDKConstant;
import com.example.core.AdParameters;
import com.example.yj.R;
import com.example.yj.db.SPManager;

/**
 * Created by yj on 2017/11/9.
 */

public class SettingActivity extends Activity implements View.OnClickListener {

    /**
     * UI
     */
    private RelativeLayout mWifiLayout;
    private RelativeLayout mAlwayLayout;
    private RelativeLayout mNeverLayout;
    private CheckBox mWifiBox, mAlwayBox, mNeverBox;
    private ImageView mBackView;

    private static final int PLAY_ALWAY = 0;
    private static final int PLAY_ONLY_WIFI = 1;
    private static final int PLAY_DEFAULT_SETTING = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();//初始化UI
    }

    private void initSetting() {
        int currentSetting = SPManager.getInstance().getInt(SPManager.VIDEO_SETTING, PLAY_ONLY_WIFI);
        switch (currentSetting) {
            case PLAY_ALWAY:
                mAlwayBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);//无背景
                mNeverBox.setBackgroundResource(0);
                break;
            case PLAY_ONLY_WIFI:
                mAlwayBox.setBackgroundResource(0);//无背景
                mWifiBox.setBackgroundResource(R.drawable.setting_selected);
                mNeverBox.setBackgroundResource(0);
                break;
            case PLAY_DEFAULT_SETTING:
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(0);//无背景
                mNeverBox.setBackgroundResource(R.drawable.setting_selected);
                break;
        }

    }

    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_view);
        mWifiLayout = (RelativeLayout) findViewById(R.id.wifi_layout);
        mWifiBox = (CheckBox) findViewById(R.id.wifi_check_box);
        mAlwayLayout = (RelativeLayout) findViewById(R.id.alway_layout);
        mAlwayBox = (CheckBox) findViewById(R.id.alway_check_box);
        mNeverLayout = (RelativeLayout) findViewById(R.id.close_layout);
        mNeverBox = (CheckBox) findViewById(R.id.close_check_box);

        mBackView.setOnClickListener(this);
        mWifiLayout.setOnClickListener(this);
        mAlwayLayout.setOnClickListener(this);
        mNeverLayout.setOnClickListener(this);
        initSetting();//加载上一次保存的设置
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.back_view://返回
                finish();
                break;
            case R.id.alway_layout://数据、WiFi状态播放
                SPManager.getInstance().putInt(SPManager.VIDEO_SETTING, PLAY_ALWAY);//设置存储
                AdParameters.setCurrentSetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI);//本地设置播放条件
                mAlwayBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;
            case R.id.wifi_layout://wifi状态播放
                SPManager.getInstance().putInt(SPManager.VIDEO_SETTING, 2);
                AdParameters.setCurrentSetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_ONLY_WIFI);
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(R.drawable.setting_selected);
                mNeverBox.setBackgroundResource(0);
                break;
            case R.id.close_layout://关闭设置
                SPManager.getInstance().putInt(SPManager.VIDEO_SETTING, 2);
                AdParameters.setCurrentSetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_NEVER);
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(R.drawable.setting_selected);
                break;
        }
    }
}
