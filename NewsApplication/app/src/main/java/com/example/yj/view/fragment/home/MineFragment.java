package com.example.yj.view.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adutil.ImageLoaderUtil;
import com.example.okhttp.listener.DisposeDataListener;
import com.example.yj.R;
import com.example.yj.Service.update.UpdateService;
import com.example.yj.activity.LoginActivity;
import com.example.yj.activity.SettingActivity;
import com.example.yj.constant.Constant;
import com.example.yj.manager.UserManager;
import com.example.yj.model.update.UpdateModel;
import com.example.yj.network.http.RequestCenter;
import com.example.yj.share.ShareDialog;
import com.example.yj.util.Util;
import com.example.yj.view.CommonDialog;
import com.example.yj.view.MyQrcodeDialog;
import com.example.yj.view.fragment.BaseFragment;

import cn.sharesdk.framework.Platform;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人信息
 * Created by yj on 2017/9/15.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {

    private View view;//资源获取

    /**
     * UI
     */
    private RelativeLayout mLoginLayout;
    private CircleImageView mPhotoView;
    private TextView mLoginInfoView;
    private TextView mLoginView;
    private RelativeLayout mLoginedLayout;
    private TextView mUserNameView;
    private TextView mTickView;
    private TextView mVideoSettingView;
    private TextView mShareView;
    private TextView mQrCodeView;
    private TextView mUpdateView;
    private LoginBroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        registerBroadcast();//注册用户登陆局部广播
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, null, false);
        initView();//初始化UI
        return view;
    }

    /**
     * 初始化UI
     */
    private void initView() {
        mLoginLayout = (RelativeLayout) view.findViewById(R.id.login_layout);
        mLoginLayout.setOnClickListener(this);
        mLoginedLayout = (RelativeLayout) view.findViewById(R.id.logined_layout);
        mLoginedLayout.setOnClickListener(this);

        mPhotoView = (CircleImageView) view.findViewById(R.id.photo_view);
        mPhotoView.setOnClickListener(this);
        mLoginView = (TextView) view.findViewById(R.id.login_view);
        mLoginView.setOnClickListener(this);
        mVideoSettingView = (TextView) view.findViewById(R.id.video_setting_view);
        mVideoSettingView.setOnClickListener(this);
        mShareView = (TextView) view.findViewById(R.id.share_imooc_view);
        mShareView.setOnClickListener(this);
        mQrCodeView = (TextView) view.findViewById(R.id.my_qrcode_view);
        mQrCodeView.setOnClickListener(this);
        mLoginInfoView = (TextView) view.findViewById(R.id.login_info_view);
        mUserNameView = (TextView) view.findViewById(R.id.username_view);
        mTickView = (TextView) view.findViewById(R.id.tick_view);

        mUpdateView = (TextView) view.findViewById(R.id.update_view);
        mUpdateView.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();//取消用户登陆局部广播
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_setting_view:
                //视频设置---跳转设置界面
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.update_view://更新
                if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {//有sd卡权限
                    checkVersion();
                } else {//无sd卡权限则请求sd卡读写权限
                    requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
                }
                break;
            case R.id.login_view://登陆
                login();//登陆
                break;
            case R.id.my_qrcode_view://我的二维码
                if(UserManager.getInstance().isLogin()){
                    new MyQrcodeDialog(mContext).show();
                }else {
                    login();
                }
                break;
            case R.id.share_imooc_view://分享
                showShareDialog();
                break;
        }
    }

    /**
     * 弹出分享对话框
     */
    private void showShareDialog() {
        ShareDialog dialog = new ShareDialog(mContext);
        dialog.setShareType(Platform.SHARE_IMAGE);
        dialog.setShareTitle("慕课网");
        dialog.setShareTitleUrl("http://www.imooc.com");
        dialog.setShareText("慕课网");
        dialog.setShareSite("imooc");
        dialog.setShareSiteUrl("http://www.imooc.com");
        dialog.setSharePhoto(Environment.getExternalStorageDirectory() + "/test2.jpg");
        dialog.show();
    }

    private void login() {
        startActivity(new Intent(mContext,LoginActivity.class));//跳转到登陆界面
    }

    public void checkVersion() {
        Toast.makeText(mContext, "更新", Toast.LENGTH_SHORT).show();
        RequestCenter.checkVersion(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                final UpdateModel updateModel = (UpdateModel) responseObj;//返回数据
                if (Util.getVersionCode(mContext) < updateModel.data.currentVersion) {//服务器返回版本大于应用版本，则更新
                    //弹出是否更新窗口
                    CommonDialog commonDialog = new CommonDialog(mContext, getString(R.string.update_new_version), getString(R.string.update_title),
                            getString(R.string.update_install), getString(R.string.cancel), new CommonDialog.DialogClickListener() {
                        @Override
                        public void onDialogClick() {
                            Intent intent = new Intent(mContext, UpdateService.class);
                            mContext.startService(intent);//启动后台下载服务
                        }
                    });
                    commonDialog.show();
                } else {//返回版本为当前版本，通知已是最新版本
                    Toast.makeText(mContext, getString(R.string.no_new_version_msg), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    /**
     * 注册用户登陆局部广播
     */
    private void registerBroadcast() {
        broadcastReceiver = new LoginBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(LoginActivity.LOGIN_ACTION);//过滤用户登陆广播action
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver,intentFilter);//接收局部广播
    }

    /**
     * 解绑用户登陆局部广播
     */
    private void unRegisterBroadcast(){
        if(broadcastReceiver!=null){
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
        }
    }
    /**
     * 接收用户登陆广播
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().isLogin()) {//显示用户登陆信息的ui
                if (mLoginedLayout.getVisibility() == View.GONE) {
                    mLoginLayout.setVisibility(View.GONE);
                    mLoginedLayout.setVisibility(View.VISIBLE);
                    mUserNameView.setText(UserManager.getInstance().getUser().data.name);
                    mTickView.setText(UserManager.getInstance().getUser().data.tick);

                    ImageLoaderUtil.getInstance(mContext).displayImage(mPhotoView, UserManager.getInstance().getUser().data.photoUrl);
                }
            }
        }
    }
}
