package com.example.response;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.adutil.ResponseEntityToModule;
import com.example.exception.OkHttpException;
import com.example.listener.DisposeDataHandle;
import com.example.listener.DisposeDataListener;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by yj on 2017/9/18.
 * 处理JSON回调响应
 */

public class CommonJsonCallback implements Callback {

    //与服务器返回字段的对应关系
    protected final String RESULT_CODE = "ecode";//http请求响应码
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    protected final int NETWORK_ERROR = -1;//网络错误
    protected final int JSON_ERROR = -2;//JSON关系错误
    protected final int OTHER_ERROR = -3;//其他错误


    private Handler mDeliverHandler;
    private DisposeDataListener mListener;
    private Class<?> clazz;


    public CommonJsonCallback(DisposeDataHandle disposeDataHandle) {
        this.mListener = disposeDataHandle.listener;
        this.clazz = disposeDataHandle.clazz;
        //获取主线程
        mDeliverHandler = new Handler(Looper.getMainLooper());
    }

    //请求失败，通知主线程发生错误
    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                //返回错误信息
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    //请求成功，处理数据
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        //获取服务器返回数据信息
        final String result = response.body().string();
        Log.d(TAG, "onResponse:(result结果) "+result);
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });

    }

    private void handleResponse(Object responseObj) {
        //服务器返回数据为空
        if (responseObj == null&&responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
            return ;
        }
        try {
            JSONObject result = new JSONObject(responseObj.toString());
            //json数据中存在响应码,尝试解析
            if(result.has(RESULT_CODE)){
                //取出json中的响应码，和RESULT_CODE_VALUE相同则为正确响应
                if(result.getInt(RESULT_CODE) == RESULT_CODE_VALUE){
                    //实体类为空，则不处理直接给应用层
                    if(clazz == null){
                        mListener.onSuccess(result);
                    }else {//否则将json对象转为实习对象
                        Object obj = ResponseEntityToModule.parseJsonObjectToModule(result,clazz);//插件封装
                         //成功转换成实体类,返回实体对象
                        if (obj != null) {
                            mListener.onSuccess(obj);
                        }else {//json解析错误
                            mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                        }

                    }
                }else {//返回json不正常,返回给应用层处理
                    mListener.onFailure(new OkHttpException(OTHER_ERROR,result.get(RESULT_CODE)));
                }
            }
        }catch (Exception e){
            mListener.onFailure(new OkHttpException(OTHER_ERROR,e.getMessage()));
        }

    }
}
