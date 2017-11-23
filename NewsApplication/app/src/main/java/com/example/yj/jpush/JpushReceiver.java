package com.example.yj.jpush;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.adutil.ResponseEntityToModule;
import com.example.yj.activity.LoginActivity;
import com.example.yj.activity.MainActivity;
import com.example.yj.constant.Constant;
import com.example.yj.manager.UserManager;
import com.example.yj.model.jpush.PushMessage;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by yj on 2017/11/22.
 */

public class JpushReceiver extends BroadcastReceiver {

    private static final String NEED_LOGIN = "2";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {//收到推送

        }
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {//用户点击推送
            //获取推送的数据转为实体对象
            PushMessage pushMessage = (PushMessage) ResponseEntityToModule.parseJsonToModule(bundle.getString(JPushInterface.EXTRA_EXTRA),
                    PushMessage.class);
            if (getCurrentTask(context)) {//应用已启动---前台、后台
                Intent pushIntent = new Intent();
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//无任务栈则创建
                intent.putExtra(Constant.PUSH_MESSAGE, pushMessage);//推送数据传递

                if (pushMessage != null && pushMessage.messageType.equals(NEED_LOGIN)
                        && !UserManager.getInstance().isLogin()) {//需要登陆且未登陆
                    //跳转登陆界面
                    pushIntent.setClass(context, LoginActivity.class);
                    pushIntent.putExtra(Constant.FROM_JPUSH, true);
                } else {//已登陆或无需登陆
                    //跳转推送界面
                    pushIntent.setClass(context, PushMessageActivity.class);
                }
                context.startActivity(pushIntent);
            } else {//应用未启动
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (pushMessage != null && pushMessage.messageType.equals(NEED_LOGIN)) {//需要登陆
                    //跳转到登陆界面
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    loginIntent.putExtra(Constant.PUSH_MESSAGE, pushMessage);
                    loginIntent.putExtra(Constant.FROM_JPUSH, true);
                    //启动activity，最后显示loginActivity
                    context.startActivities(new Intent[]{mainIntent, loginIntent});
                }else {//无需登陆
                    //跳转推送界面
                    Intent pushIntent = new Intent(context,PushMessageActivity.class);
                    pushIntent.putExtra(Constant.PUSH_MESSAGE, pushMessage);
                    context.startActivities(new Intent []{mainIntent,pushIntent});
                }
            }
        }

    }

    /**
     * 获取指定包名的应用程序是否在运行(无论前台还是后台)
     *
     * @return
     */
    private boolean getCurrentTask(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(50);
        for (ActivityManager.RunningTaskInfo process : appProcessInfos) {//获取当前运行栈一一对比

            if (process.baseActivity.getPackageName().equals(context.getPackageName())
                    || process.topActivity.getPackageName().equals(context.getPackageName())) {

                return true;
            }
        }
        return false;
    }
}
