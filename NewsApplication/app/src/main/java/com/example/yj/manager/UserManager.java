package com.example.yj.manager;

import com.example.yj.model.user.User;

/**
 * 用户管理类-----单例
 * Created by yj on 2017/11/13.
 */

public class UserManager {

    private static UserManager userManager = null;

    private User user;

    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    /**
     * 获取用户信息
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * 设置用户
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 退出登陆
     */
    public void removeUser() {
        user = null;
    }

    /**
     * 是否登陆
     * @return
     */
    public boolean isLogin() {
        return user == null ? false : true;
    }
}
