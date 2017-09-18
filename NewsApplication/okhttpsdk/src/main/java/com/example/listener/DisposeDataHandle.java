package com.example.listener;

/**
 * Created by yj on 2017/9/18.
 */

public class DisposeDataHandle {

    public DisposeDataListener listener = null;
    public Class<?> clazz = null;

    /**
     * 服务器数据直接回调给应用层
     * @param listener
     */
    public DisposeDataHandle(DisposeDataListener listener) {
        this.listener = listener;
    }

    /**
     * 转换成实体对象再返回给应用层
     * @param listener
     * @param clazz
     */
    public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz) {
        this.listener = listener;
        this.clazz = clazz;
    }
}
