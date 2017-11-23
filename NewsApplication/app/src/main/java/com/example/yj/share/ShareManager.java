package com.example.yj.share;

import android.content.Context;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by yj on 2017/11/18.
 */

public class ShareManager {

    private static ShareManager mShareManager = null;
    /**
     * 要分享到的平台
     */
    private Platform mCurrentPlatform;
    /**
     * 线程安全的单例模式
     *
     * @return
     */
    public static ShareManager getInstance() {
        if (mShareManager == null) {
            synchronized (ShareManager.class) {
                if (mShareManager == null) {
                    mShareManager = new ShareManager();
                }
            }
        }
        return mShareManager;
    }

    private ShareManager() {

    }

    public static void initShareSDK(Context context){
        ShareSDK.initSDK(context);
    }
    /**
     * 分享
     * @param data 分享的数据
     * @param listener
     */
    public void shareData(ShareData data, PlatformActionListener listener) {
        switch (data.type) {
            case QQ:
                mCurrentPlatform = ShareSDK.getPlatform(QQ.NAME);
                break;
            case Qzone:
                mCurrentPlatform = ShareSDK.getPlatform(QZone.NAME);
                break;
            case WeChat:
                mCurrentPlatform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case WeChatMoments:
                mCurrentPlatform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
        }
        mCurrentPlatform.setPlatformActionListener(listener); //由应用层去处理回调,分享平台不关心。
        mCurrentPlatform.share(data.params);
    }
    /**
     * 删除授权
     */
    public void removeAccount() {
        if (mCurrentPlatform != null) {
            mCurrentPlatform.getDb().removeAccount();
        }
    }
    /**
     * 平台类型
     */
    public enum PlatformType {
        QQ, Qzone, WeChat, WeChatMoments
    }
}
