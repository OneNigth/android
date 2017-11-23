package com.example.firstsdk;

import com.example.okhttp.CommonOkHttpClient;
import com.example.okhttp.listener.DisposeDataHandle;
import com.example.okhttp.listener.DisposeDataListener;
import com.example.okhttp.request.CommonRequest;

import org.junit.Test;

/**
 * Created by yj on 2017/9/19.
 */
public class CommonOkHttpClientTest {
    @Test
    public void sendRequest() throws Exception {
        //网络请求
//        //callback未封装
//        CommonOkHttpClient.sendRequest(CommonRequest.creatGetRequest("http://www.imook.com", null), new Callback() {
//            //请求错误
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//             //处理异常 数据解析 数据转发
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
        //callback封装后
        CommonOkHttpClient.get(CommonRequest.createGetRequest("http://www.imook.com", null),
                new DisposeDataHandle(new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {

                    }

                    @Override
                    public void onFailure(Object reasonObj) {

                    }
                }));
    }
}
