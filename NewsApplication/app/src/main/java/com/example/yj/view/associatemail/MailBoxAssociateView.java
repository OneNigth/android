package com.example.yj.view.associatemail;

import android.content.Context;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;

/**
 * 输入@符号后开始联想
 * Created by yj on 2017/11/13.
 */

public class MailBoxAssociateView extends AppCompatMultiAutoCompleteTextView {
    public MailBoxAssociateView(Context context) {
        super(context);
    }
    public MailBoxAssociateView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MailBoxAssociateView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean enoughToFilter()
    {
        return getText().toString().contains("@") && getText().toString().indexOf("@") > 0;
    }
}
