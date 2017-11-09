package com.example.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.activity.AdBrowserActivity;
import com.example.adutil.LogUtil;
import com.example.adutil.Utils;
import com.example.constant.SDKConstant;
import com.example.core.video.VideoAdSlot.AdSlotListener;
import com.example.firstsdk.R;
import com.example.module.AdValue;
import com.example.report.ReportManager;

/**
 * Created by yj on 2017/11/1.
 */

public class VideoFullDialog extends Dialog implements CustomVideoView.ADVideoPlayerListener {
    private static final String TAG = VideoFullDialog.class.getSimpleName();

    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private Context mContext;
    private RelativeLayout mRootView;
    private RelativeLayout mParentView;
    private ImageView mBackButton;
    /**
     * Data
     */
    private FullToSmallListener listener;
    private AdValue mInstance;
    private int mPosition;
    private boolean isFirst = true;
    private int deltaY; //动画要执行的平移值
    private Bundle mStartBundle;//用于Dialog入场动画
    private Bundle mEndBundle; //用于Dialog出场动画
    private AdSlotListener mSlotListener;

    public VideoFullDialog(@NonNull Context context, CustomVideoView customVideoView, AdValue instance, int position) {
        super(context, R.style.dialog_full_screen);//全屏样式
        mContext = context;
        mVideoView = customVideoView;
        mInstance = instance;
        mPosition = position;
    }

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initView();
    }

    private void initView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);
        mBackButton = (ImageView) findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBack();
            }
        });
        mVideoView.setListener(this);//处理全屏对话框的事件
        mVideoView.mute(false);//非静音
        mVideoView.seekAndResume(mPosition);//从播放出续播视频
        mParentView.addView(mVideoView);//把videoView添加进父容器
        mParentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareScene();
                runEnterAnimation();
                return true;
            }
        });
    }

    /**
     * 退出全屏
     */
    private void clickBack() {
        dismiss();//关闭当前对话框
        if (listener != null) {//通知当前播放时长
            listener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
        }
    }

    /**
     * 屏幕back建返回键按下时
     */
    @Override
    public void onBackPressed() {
        clickBack();
        super.onBackPressed();
    }

    /**
     * 焦点状态改变（可视、不可视）调用此方法
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtil.i(TAG, "onWindowFocusChanged");
        if (!hasFocus) {//未取得焦点
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        } else {//取得焦点-----------部分机型可能无法直接resume
            if (isFirst) {//首次播放且首次取得焦点------为了适配某些手机不执行seekandresume中的播放方法
                mVideoView.seekAndResume(mPosition);
            } else {
                mVideoView.resume();
            }
        }
    }

    /*****************************
     * ADVideoPlayerListener中的接口
     *****************************/
    @Override
    public void onBufferUpdate(int time) {
        try{
            if(mInstance!=null){
                ReportManager.suReport(mInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);//发送监听
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {
        String desationUrl = mInstance.clickUrl;
        if (mSlotListener != null) {
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                mSlotListener.onClickVideo(desationUrl);
                try {
                    ReportManager.pauseVideoReport(mInstance.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //走默认样式
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                Intent intent = new Intent(mContext, AdBrowserActivity.class);
                intent.putExtra(AdBrowserActivity.KEY_URL,mInstance.clickUrl);
                mContext.startActivity(intent);
                try {
                    ReportManager.pauseVideoReport(mInstance.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClickBackBtn() {
        runExitAnimator();//退场动画
    }

    //空
    @Override
    public void onClickPlay() {

    }

    @Override
    public void onVideoLoadSuccess() {
        if(mVideoView!=null){
            mVideoView.resume();
        }
    }

    @Override
    public void onVideoLoadFailed() {

    }
    //单独处理
    @Override
    public void onVideoPlayComplete() {
        dismiss();//销毁对话框
        if(listener!=null){
            listener.playComplete();
        }
    }


    public void setListener(FullToSmallListener listener) {
        this.listener = listener;
    }

    public void setSlotListener(AdSlotListener mSlotListener) {
        this.mSlotListener = mSlotListener;
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }

    //准备动画所需数据
    public void prepareScene(){
        mEndBundle = Utils.getViewProperty(mVideoView);
        /**
         * 将desationview移到originalview位置处
         */
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP)
                - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }
    //准备入场动画
    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }
    /**
     * 准备退场场动画
     */
    private void runExitAnimator() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        try {
                            ReportManager.exitfullScreenReport(mInstance.event.exitFull.content, mVideoView.getCurrentPosition()
                                    / SDKConstant.MILLION_UNIT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (listener != null) {
                            listener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                        }
                    }
                }).start();
    }

    /**
     * VideoAdSlot实现
     */
    public interface FullToSmallListener {
        /**
         * 全屏退出后返回当前播放时长
         *
         * @param position
         */
        void getCurrentPlayPosition(int position);

        /**
         * 全屏播放结束
         */
        void playComplete();
    }

    /**
     * dialog销毁
     */
    @Override
    public void dismiss() {
        LogUtil.e(TAG, "dismis");
        mParentView.removeView(mVideoView);//从父组件中移除VideoView
        super.dismiss();
    }

}
