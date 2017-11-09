package com.example.core.video;

import android.view.ViewGroup;

import com.example.adutil.LogUtil;
import com.example.adutil.ResponseEntityToModule;
import com.example.core.AdContextInterface;
import com.example.core.video.VideoAdSlot.AdSlotListener;
import com.example.module.AdValue;
import com.example.widget.CustomVideoView.ADFrameImageLoadListener;

/**
 * 管理VideoAdSlot，可直接使用
 * Created by yj on 2017/11/3.
 */

public class VideoAdContext implements AdSlotListener {
    private String TAG = "VideoAdContext";

    private ViewGroup parentView;

    private VideoAdSlot mAdSlot;
    private AdValue mInstance;
    //listener
    private AdContextInterface mListener;
    private ADFrameImageLoadListener mFrameLoadListener;
    public VideoAdContext(ViewGroup parentView , String instance ){
        this.parentView = parentView;
        mInstance = (AdValue) ResponseEntityToModule.parseJsonToModule(instance,AdValue.class);//json转换成实体对象
        load();

    }

    private void load() {
        if(mInstance!=null&&mInstance.resource!=null){
            LogUtil.e(TAG,mInstance.resource+"");
            mAdSlot = new VideoAdSlot(mInstance, this);
            mAdSlot.setFrameLoadListener(mFrameLoadListener);
        }
    }

    /**
     *  根据滑动情况判断是否自动播放，视频出现超过50%自动播放，否则自动暂停
     */
    public void updateVideoInScrollView(){
        if(mAdSlot!=null){
            mAdSlot.updateVedioInScrollView();
        }
    }

    @Override
    public ViewGroup getAdParent() {
        return parentView;
    }

    @Override
    public void onAdVideoLoadSuccess() {

    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {

    }

    @Override
    public void onClickVideo(String url) {

    }

    public void setAdResultListener(AdContextInterface mListener) {
        this.mListener = mListener;
    }
}
