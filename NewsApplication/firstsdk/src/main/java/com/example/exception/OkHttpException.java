package com.example.exception;

/**
 * Created by yj on 2017/9/18.
 * 错误类
 */

public class OkHttpException extends Exception {

    /**
     * 错误码
     */
    private int ecode;

    /**
     * 错误信息
     */
    private Object emsg;

    public OkHttpException(int ecode, Object emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public Object getEmsg() {
        return emsg;
    }
}
