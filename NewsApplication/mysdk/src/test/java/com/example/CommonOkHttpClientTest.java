package com.example;

import android.util.Log;

import com.example.listener.DisposeDataHandle;
import com.example.listener.DisposeDataListener;
import com.example.request.CommonRequest;
import com.example.response.CommonJsonCallback;

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
        CommonOkHttpClient.sendRequest(CommonRequest.creatGetRequest("http://www.imook.com", null),
                new CommonJsonCallback(new DisposeDataHandle(new DisposeDataListener() {
                    @Override
                    //成功
                    public void onSuccess(Object responseObj) {
                        Log.d("成功", "onSuccess: " + responseObj);
                    }

                    //失败
                    @Override
                    public void onFailure(Object reasonObj) {
                        Log.d("失败", "onFailure: " + reasonObj);
                    }
                })));
    }
}
