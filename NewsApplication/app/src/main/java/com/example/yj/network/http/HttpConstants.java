package com.example.yj.network.http;

/**
 * Created by yj on 2017/9/21.
 * 所有请求的链接
 */

public class HttpConstants {

    private static final String ROOT_URL ="http://yj.com/api";

    /**
     * 首页请求接口
     */
    public static final String HOME_RECOMMAND = ROOT_URL+"/product/home_recommand.php";

    /**
     * 检查更新接口
     */
    public static String CHECK_UPDATE = ROOT_URL + "/config/check_update.php";

    /**
     * 登陆接口
     */
    public static String LOGIN = ROOT_URL + "/user/login_phone.php";
    /**
     * 课程详情接口
     */
    public static String COURSE_DETAIL = ROOT_URL + "/product/course_detail.php";
}
