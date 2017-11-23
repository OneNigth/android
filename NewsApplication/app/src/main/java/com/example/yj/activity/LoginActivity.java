package com.example.yj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.okhttp.listener.DisposeDataListener;
import com.example.yj.R;
import com.example.yj.activity.base.BaseActivity;
import com.example.yj.constant.Constant;
import com.example.yj.jpush.PushMessageActivity;
import com.example.yj.manager.UserManager;
import com.example.yj.model.jpush.PushMessage;
import com.example.yj.model.user.User;
import com.example.yj.network.http.RequestCenter;
import com.example.yj.view.associatemail.MailBoxAssociateView;

/**
 * Created by yj on 2017/11/13.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    /**
     * ui
     */
    private MailBoxAssociateView mUsernameView;
    private EditText mPasswordView;
    private TextView mLoginView;

    /**
     * data
     */
    private PushMessage pushMessage;
    private boolean isFromPush;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ininData();
        initView();
    }

    private void ininData() {
        Intent intent = getIntent();
        if(intent.hasExtra(Constant.PUSH_MESSAGE)){
            pushMessage = (PushMessage) intent.getSerializableExtra(Constant.PUSH_MESSAGE);//获取推送数据
        }
        isFromPush = intent.getBooleanExtra(Constant.FROM_JPUSH,false);
    }

    private void initView() {
        mUsernameView = (MailBoxAssociateView) findViewById(R.id.associate_email_input);//输入@符号后开始联想的组件
        mPasswordView = (EditText) findViewById(R.id.login_input_password);
        mLoginView = (TextView) findViewById(R.id.login_button);

        mLoginView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.login_button:
                login();//登陆
                finish();
                break;
        }
    }

    /**
     * 登陆
     */
    private void login() {
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        checkLogin(username , password);//检查合法性
    }

    /**
     * 检查用户名、密码合法性后发送登陆请求
     * @param username
     * @param password
     */
    private void checkLogin(String username, String password) {
        sendLoginRequest(username,password);
    }

    /**
     * 发送登陆请求
     * @param username
     * @param password
     */
    private void sendLoginRequest(String username, String password) {
        RequestCenter.doLogin(username, password, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                User user = (User) responseObj;
                UserManager userManager = UserManager.getInstance();
                userManager.setUser(user);//持久化用户信息
                senLocalBroadcast();//发送局部广播通知用户登陆

                //从推送过来的则跳转到推送界面
                if(isFromPush){
                    Intent intent = new Intent(LoginActivity.this, PushMessageActivity.class);
                    intent.putExtra(Constant.PUSH_MESSAGE,pushMessage);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    //自定义登陆广播Action
    public static final String LOGIN_ACTION = "com.imooc.action.LOGIN_ACTION";
    /**
     * 发送局部广播通知用户已登陆
     */
    private void senLocalBroadcast() {
        LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(new Intent(LOGIN_ACTION));
    }
}
