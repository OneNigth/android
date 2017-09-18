package com.example;

import com.example.https.HttpsUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yj on 2017/9/18.
 */

public class CommonOkHttpClient {

    //超时时间
    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;

    //初始化数据
    static {
        //client构建者
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        //设置连接、读、写超时 ,30秒
        okBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

        //设置请求可转发、重定向
        okBuilder.followRedirects(true);

        //https支持，个人或官方购买的证书
        okBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        //ssl加密
        okBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory());

        //生成client对象
        mOkHttpClient = okBuilder.build();
    }

    /**
     * 发送http/https请求
     *
     * @param request
     * @param commCallback
     * @return Call 用于ondestory方法中销毁（优化）
     */
    public static Call sendRequest(Request request, Callback commCallback) {

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commCallback);

        return call;
    }
}
