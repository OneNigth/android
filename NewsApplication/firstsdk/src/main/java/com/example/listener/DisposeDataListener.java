package com.example.listener;

/**
 * Created by yj on 2017/9/18.
 * callback事件监听
 */

public interface DisposeDataListener {

    /**
     * 请求成功回调事件处理
     * @param responseObj
     */
    public void onSuccess(Object responseObj);

    /**
     * 请求失败回调事件处理
     * @param reasonObj
     */
    public void onFailure(Object reasonObj);

}
