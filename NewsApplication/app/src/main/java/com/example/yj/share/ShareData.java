package com.example.yj.share;

import cn.sharesdk.framework.Platform;

/**
 * 分享数据实体
 * Created by yj on 2017/11/18.
 */

public class ShareData {
    /**
     * 分享的平台
     */
    public ShareManager.PlatformType type;
    /**
     * 分享的数据
     */
    public Platform.ShareParams params;
}
