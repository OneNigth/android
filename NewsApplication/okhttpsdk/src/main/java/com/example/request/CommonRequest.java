package com.example.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yj on 2017/9/18.
 * 生成request对象
 */

public class CommonRequest {

    /**
     * 生成post对象
     * @param url  请求链接
     * @param params
     * @return  返回创建好的request
     */
    public static Request creatPostRequest(String url, RequestParams params) {
        FormBody.Builder mBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                mBuilder.add(entry.getKey(),entry.getValue());
            }
        }


        OkHttpClient client = new OkHttpClient();

        return null;
    }
}
