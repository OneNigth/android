package com.example.yj.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yj.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by yj on 2017/11/19.
 */

public class ShareDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private DisplayMetrics dm;
    /**
     * UI
     */
    private RelativeLayout mWeixinLayout;
    private RelativeLayout mWeixinMomentLayout;
    private RelativeLayout mQQLayout;
    private RelativeLayout mQZoneLayout;
    //    private RelativeLayout mDownloadLayout;//下载图标，未显示
    private TextView mCancelView;

    /**
     * share relative
     */
    private int mShareType; //指定分享类型
    private String mShareTitle; //指定分享内容标题
    private String mShareText; //指定分享内容文本
    private String mSharePhoto; //指定分享本地图片
    private String mShareTileUrl;
    private String mShareSiteUrl;
    private String mShareSite;
    private String mUrl;
    private String mResourceUrl;

    public ShareDialog(@NonNull Context context) {
        super(context, R.style.SheetDialogStyle);
        mContext = context;
        dm = mContext.getResources().getDisplayMetrics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share_layout);
        initView();
    }

    private void initView() {
        //获取dialog的window，控制窗口出现位置和宽高
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.width = dm.widthPixels;//宽为屏幕宽度
        dialogWindow.setAttributes(layoutParams);

        mWeixinLayout = (RelativeLayout) findViewById(R.id.weixin_layout);
        mWeixinLayout.setOnClickListener(this);
        mWeixinMomentLayout = (RelativeLayout) findViewById(R.id.moment_layout);
        mWeixinMomentLayout.setOnClickListener(this);
        mQQLayout = (RelativeLayout) findViewById(R.id.qq_layout);
        mQQLayout.setOnClickListener(this);
        mQZoneLayout = (RelativeLayout) findViewById(R.id.qzone_layout);
        mQZoneLayout.setOnClickListener(this);
//        mDownloadLayout = (RelativeLayout) findViewById(R.id.download_layout);
//        mDownloadLayout.setOnClickListener(this);
        mCancelView = (TextView) findViewById(R.id.cancel_view);
        mCancelView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.weixin_layout:
                shareData(ShareManager.PlatformType.WeChat);
                break;
            case R.id.moment_layout:
                shareData(ShareManager.PlatformType.WeChatMoments);
                break;
            case R.id.qq_layout:
                shareData(ShareManager.PlatformType.QQ);
                break;
            case R.id.qzone_layout:
                shareData(ShareManager.PlatformType.Qzone);
                break;
            case R.id.cancel_view:
                dismiss();
                break;
        }
    }

    /**
     * 分享事件监听
     */
    private PlatformActionListener mListener = new PlatformActionListener() {
        //分享成功
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        }

        //分享失败
        @Override
        public void onError(Platform platform, int i, Throwable throwable) {

        }

        //分享取消
        @Override
        public void onCancel(Platform platform, int i) {

        }
    };

    /**
     * 分享数据封装
     *
     * @param type
     */
    private void shareData(ShareManager.PlatformType type) {
        ShareData data = new ShareData();
        Platform.ShareParams params = new Platform.ShareParams();
        params.setShareType(mShareType);
        params.setTitle(mShareTitle);
        params.setTitleUrl(mShareTileUrl);
        params.setSite(mShareSite);
        params.setSiteUrl(mShareSiteUrl);
        params.setText(mShareText);
        params.setImagePath(mSharePhoto);
        params.setUrl(mUrl);

        data.type = type;
        data.params = params;
        ShareManager.getInstance().shareData(data, mListener);
    }

    public void setShareType(int mShareType) {
        this.mShareType = mShareType;
    }

    public void setShareTitle(String mShareTitle) {
        this.mShareTitle = mShareTitle;
    }

    public void setShareText(String mShareText) {
        this.mShareText = mShareText;
    }

    public void setSharePhoto(String mSharePhoto) {
        this.mSharePhoto = mSharePhoto;
    }

    public void setShareTitleUrl(String mShareTileUrl) {
        this.mShareTileUrl = mShareTileUrl;
    }

    public void setShareSiteUrl(String mShareSiteUrl) {
        this.mShareSiteUrl = mShareSiteUrl;
    }

    public void setShareSite(String mShareSite) {
        this.mShareSite = mShareSite;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setResourceUrl(String mResourceUrl) {
        this.mResourceUrl = mResourceUrl;
    }

}
