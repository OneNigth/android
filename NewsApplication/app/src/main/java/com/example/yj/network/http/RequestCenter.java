package com.example.yj.network.http;

import com.example.okhttp.CommonOkHttpClient;
import com.example.okhttp.listener.DisposeDataHandle;
import com.example.okhttp.listener.DisposeDataListener;
import com.example.okhttp.request.CommonRequest;
import com.example.okhttp.request.RequestParams;
import com.example.yj.model.course.BaseCourseModel;
import com.example.yj.model.recommand.BaseRecommandModel;
import com.example.yj.model.update.UpdateModel;
import com.example.yj.model.user.User;

/**
 * Created by yj on 2017/9/21.
 * 请求中心
 */

public class RequestCenter {

    //根据参数，发送post请求
    private static void postRequest (String url , RequestParams params , DisposeDataListener listener , Class<?> clazz){
        CommonOkHttpClient.post(CommonRequest.createPostRequest(url , params),new DisposeDataHandle(listener , clazz));
    }

    /**
     * 发送首页资源请求
     * @param listener
     */
    public static void requestRecommandData(DisposeDataListener listener){
        postRequest(HttpConstants.HOME_RECOMMAND ,null ,listener , BaseRecommandModel.class);
    }

    /**
     * 发送版本更新验证请求
     */
    public static void checkVersion(DisposeDataListener listener){
        RequestCenter.postRequest(HttpConstants.CHECK_UPDATE,null,listener, UpdateModel.class);
    }

    /**
     * 发送登陆请求
     */
    public static void doLogin(String username , String password , DisposeDataListener listener){
        RequestParams params = new RequestParams();
        params.put("mb",username);
        params.put("psw",password);
        RequestCenter.postRequest(HttpConstants.LOGIN,params,listener, User.class);
    }

    /**
     * 请求课程详情
     *
     * @param listener
     */
    public static void requestCourseDetail(String courseId, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("courseId", courseId);
        RequestCenter.postRequest(HttpConstants.COURSE_DETAIL, params, listener, BaseCourseModel.class);
    }
}
