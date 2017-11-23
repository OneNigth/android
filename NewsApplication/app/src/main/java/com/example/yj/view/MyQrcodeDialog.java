package com.example.yj.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adutil.Utils;
import com.example.yj.R;
import com.example.yj.manager.UserManager;
import com.example.yj.util.Util;

/**
 * 创建二维码
 * Created by yj on 2017/11/14.
 */

public class MyQrcodeDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    private TextView mTickView;
    private TextView mCloseView;
    private ImageView mCodeView;

    public MyQrcodeDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_myqrcode_layout);
        initView();
    }

    private void initView() {
        mTickView = (TextView) findViewById(R.id.tick_view);
        mCloseView = (TextView) findViewById(R.id.close_view);
        mCodeView = (ImageView) findViewById(R.id.qrcode_view);

        mCloseView.setOnClickListener(this);

        String userName = UserManager.getInstance().getUser().data.name;//获取用户名
        mTickView.setText(userName + mContext.getString(R.string.personal_info));

        //显示生成的二维码
        Bitmap bitmap =Util.createQRCode(Utils.dip2px(mContext, 200),
                Utils.dip2px(mContext, 200), userName);
        if(bitmap!=null)
        mCodeView.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.close_view://关闭按钮
                dismiss();
                break;
        }
    }
}
