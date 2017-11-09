package com.example.core.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.activity.AdBrowserActivity;
import com.example.adutil.LogUtil;
import com.example.adutil.Utils;
import com.example.constant.SDKConstant;
import com.example.core.AdParameters;
import com.example.module.AdValue;
import com.example.report.ReportManager;
import com.example.widget.CustomVideoView;
import com.example.widget.VideoFullDialog;

/**
 * Created by yj on 2017/10/28.
 */

public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {
    private String TAG = "VideoAdSlot";
    private Context mContext;

    /**
     * UI
     */
    private CustomVideoView customVideoView;
    private ViewGroup parentView;

    /**
     * Data
     */
    private AdValue mAdInstance;
    private AdSlotListener slotListener;
    private boolean canPause = false;//标志能否暂停
    private int lastArea = 0; //防止将要滑入滑出时播放器的状态改变

    public VideoAdSlot(AdValue mAdInstance, AdSlotListener listener) {
        this.mAdInstance = mAdInstance;
        this.slotListener = listener;
        parentView = listener.getAdParent();
        mContext = parentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        customVideoView = new CustomVideoView(mContext, parentView);
        if (mAdInstance != null) {
            LogUtil.d(TAG, "" + mAdInstance.resource);
            customVideoView.setDataSoure(mAdInstance.resource);
            customVideoView.setFrameURI(mAdInstance.thumb);
            customVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(customVideoView.getLayoutParams());
        parentView.addView(paddingView);
        parentView.addView(customVideoView);
    }

    @Override
    public void onBufferUpdate(int time) {
        try {
            ReportManager.suReport(mAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);//发送检测
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 小屏到全屏播放功能
     */
    @Override
    public void onClickFullScreenBtn() {
        try {
//            ReportManager.fullScreenReport(mAdInstance.event.full.content, getPosition());//发送检测
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取videoview在当前界面的属性
        Bundle bundle = Utils.getViewProperty(parentView);
        parentView.removeView(customVideoView);//把播放器从父容器中移除
        VideoFullDialog dialog = new VideoFullDialog(mContext, customVideoView, mAdInstance, getPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                //全屏模式点击返回
                backToSmallModel(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();//全屏播放完成回调
            }
        });
        dialog.setViewBundle(bundle); //为Dialog设置播放器数据Bundle对象
        dialog.setSlotListener(slotListener);
        dialog.show();
        customVideoView.isShowFullBtn(false);
    }

    /**
     * 全屏播放完毕
     */
    private void bigPlayComplete() {
        if (customVideoView.getParent() == null) {//父容器接手videoView
            parentView.addView(customVideoView);
        }
        customVideoView.isShowFullBtn(true);//显示全屏视图
        customVideoView.mute(true);//有声
        customVideoView.setListener(this);//重新设置监听为VideoAdSlot
        customVideoView.seekAndPause(0);
        canPause = false;
    }

    /**
     * 返回小屏模式
     */
    private void backToSmallModel(int position) {
        if (customVideoView.getParent() == null) {//父容器接手videoView
            parentView.addView(customVideoView);
        }
        customVideoView.isShowFullBtn(true);//显示全屏按钮
        customVideoView.mute(false);//静音----------------
        customVideoView.setListener(this);//重新设置监听为VideoAdSlot
        customVideoView.seekAndResume(position);//跳转播放
    }

    /**
     * 广告跳转
     */
    @Override
    public void onClickVideo() {
        String desationUrl = mAdInstance.clickUrl;
        if (!TextUtils.isEmpty(desationUrl)) {//不为空或者""
            if (customVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                slotListener.onClickVideo(desationUrl);
                try {
                    ReportManager.pauseVideoReport(mAdInstance.clickMonitor, customVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);//发送检测
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            //走默认样式
            if (customVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                Intent intent = new Intent(mContext, AdBrowserActivity.class);
                intent.putExtra(AdBrowserActivity.KEY_URL, mAdInstance.clickUrl);
                mContext.startActivity(intent);
                try {
                    ReportManager.pauseVideoReport(mAdInstance.clickMonitor, customVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {
//        sendSUSReport(false);
    }

    @Override
    public void onVideoLoadSuccess() {
        if (slotListener != null) {
            slotListener.onAdVideoLoadSuccess();
        }
    }

    @Override
    public void onVideoLoadFailed() {
        if (slotListener != null) {
            slotListener.onAdVideoLoadFailed();
        }
        //加载失败回到初始状态
        canPause = false;
    }

    @Override
    public void onVideoPlayComplete() {
        try {
//            ReportManager.sueReport(mAdInstance.endMonitor, false, getDuration());//发送报告
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (slotListener != null) {
            slotListener.onAdVideoLoadComplete();
        }
        customVideoView.setIsRealPause(true);
    }

    /**
     * 滑入屏幕播放、滑出暂停功能
     */
    public void updateVedioInScrollView() {
        int currentArea = Utils.getVisiblePercent(parentView);//获取组件在视频出现程度----->0-100
        //未出现在屏幕
        if (currentArea <= 0) {
            return;
        }
        //刚要滑入和滑出的异常情况处理
        if (Math.abs(currentArea - lastArea) >= 100) {
            return;
        }
        //出现面积没有超过屏幕百分之五十
        if (currentArea <= SDKConstant.VIDEO_SCREEN_PERCENT) {
            //防止用户频繁滑动多次执行
            if (canPause) {
                pauseVideo(true);
                canPause = false;
            }
            lastArea = 0;
            customVideoView.setIsRealPause(false);
            customVideoView.setIsComplete(false);
            return;
        }
        //已完成播放或点击暂停时
        if (isComplete() || isRealPause()) {
            pauseVideo(false);
            canPause = false;
            return;
        }
        //满足用户设置条件或者用户点击播放则自动播放
        if (Utils.canAutoPlay(mContext, AdParameters.getCurrentSetting())||isPlaying()) {
            lastArea = currentArea;
            resumeVideo();
            canPause = true;
            customVideoView.setIsRealPause(false);
        } else {
            //否则不播放视频
            pauseVideo(false);
            customVideoView.setIsRealPause(true);
        }
    }

    /**
     * 暂停视频----->划出屏幕后进度改为0
     */
    private void pauseVideo(boolean isAuto) {
        if (customVideoView != null) {
            if (isAuto) {
                if (!isRealPause() && isPlaying()) {//发送自动暂停检测
                    try {
                        ReportManager.pauseVideoReport(mAdInstance.event.pause.content, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            customVideoView.seekAndPause(0);
        }
    }

    /**
     * 继续播放
     */
    private void resumeVideo() {
        if (customVideoView != null) {
            customVideoView.resume();
            if (isPlaying()) {
//                sendSUSReport(true);//发送播放检测
            }
        }
    }
//    /**
//     * 发送视频开始播放监测
//     *
//     * @param isAuto
//     */
//    private void sendSUSReport(boolean isAuto) {
//        try {
//            ReportManager.susReport(mAdInstance.startMonitor, isAuto);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //传递消息到appcontext层
    public interface AdSlotListener {

        ViewGroup getAdParent();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();

        void onClickVideo(String url);
    }

    /**
     * 当前播放秒数
     *
     * @return
     */
    private int getPosition() {
        return customVideoView.getCurrentPosition() ;
    }

    /**
     * 视频总时长
     *
     * @return
     */
    private int getDuration() {
        return customVideoView.getDuration() ;
    }

    private boolean isComplete() {
        if (customVideoView != null) {
            return customVideoView.isComplete();
        }
        return false;
    }

    private boolean isPlaying() {
        if (customVideoView != null) {
            return customVideoView.isPlaying();
        }
        return false;
    }

    private boolean isRealPause() {
        if (customVideoView != null) {
            return customVideoView.isRealPause();
        }
        return false;
    }

    /**
     * 帧图加载监听器
     *
     * @param frameLoadListener
     */
    public void setFrameLoadListener(CustomVideoView.ADFrameImageLoadListener frameLoadListener) {
        customVideoView.setFrameImageLoadListener(frameLoadListener);
    }
}
