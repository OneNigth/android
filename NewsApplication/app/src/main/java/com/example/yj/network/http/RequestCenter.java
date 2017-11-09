package com.example.yj.network.http;

import com.example.okhttp.CommonOkHttpClient;
import com.example.okhttp.listener.DisposeDataHandle;
import com.example.okhttp.listener.DisposeDataListener;
import com.example.okhttp.request.CommonRequest;
import com.example.okhttp.request.RequestParams;
import com.example.yj.model.recommand.BaseRecommandModel;

/**
 * Created by yj on 2017/9/21.
 * 请求中心
 */

public class RequestCenter {

    //根据参数，发送post请求
    private static void postRequest (String url , RequestParams params , DisposeDataListener listener , Class<?> clazz){
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url , params),new DisposeDataHandle(listener , clazz));
    }

    /**
     * 发送首页资源请求
     * @param listener
     */
    public static void requestRecommandData(DisposeDataListener listener){
        postRequest(HttpConstants.HOME_RECOMMAND ,null ,listener , BaseRecommandModel.class);
    }

}
