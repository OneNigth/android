package com.example.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by yj on 2017/9/18.
 * 生成request对象
 */

public class CommonRequest {

    /**
     * 生成post请求对象
     *
     * @param url    请求链接
     * @param params
     * @return 返回创建好的request
     */
    public static Request createPostRequest(String url, RequestParams params) {
        FormBody.Builder mBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                //遍历请求参数添加进请求构建类中
                mBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        //获取请求体
        FormBody mBody = mBodyBuilder.build();

        return new Request.Builder().url(url).post(mBody).build();
    }

    /**
     * 生成get请求对象
     *
     * @param url    请求链接
     * @param params
     * @return 返回创建好的request
     */
    public static Request createGetRequest(String url, RequestParams params) {
        StringBuilder urlBuilder = new StringBuilder();
        if(params!=null){
            //拼接参数
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            //除去最末尾的&
            return new Request.Builder().url(urlBuilder.substring(0,urlBuilder.length()-1)).get().build();
        }
        //参数为空。。测试用的
        return new Request.Builder().url(url).get().build();
    }
    /**
     * @param url
     * @param params
     * @return
     */
    public static Request createMonitorRequest(String url, RequestParams params) {
        StringBuilder urlBuilder = new StringBuilder(url).append("&");
        if (params != null && params.hasParams()) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return new Request.Builder().url(urlBuilder.substring(0, urlBuilder.length() - 1)).get().build();
    }
}
