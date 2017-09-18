package com.example.yj.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yj.R;
import com.example.yj.activity.base.BaseActivity;
import com.example.yj.view.fragment.BaseFragment;
import com.example.yj.view.fragment.home.HomeFragment;
import com.example.yj.view.fragment.home.MineFragment;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mHomeLayoutView, mMessageLayoutView, mMineLayoutView;
    private ImageView mHomeImageView, mMessageImageView, mMineImageView;
    private TextView mHomeTextView, mMessageTextView, mMineTextView;

    private BaseFragment mHomeFragment, mMessageFragment, mMineFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化界面
        initView();
    }

    private void initView() {
        mHomeLayoutView = (RelativeLayout) findViewById(R.id.home_layout_view);
        mHomeLayoutView.setOnClickListener(this);
        mMessageLayoutView = (RelativeLayout) findViewById(R.id.message_layout_view);
        mMessageLayoutView.setOnClickListener(this);
        mMineLayoutView = (RelativeLayout) findViewById(R.id.mine_layout_view);
        mMineLayoutView.setOnClickListener(this);

        mHomeImageView = (ImageView) findViewById(R.id.home_image_view);
        mHomeImageView.setImageResource(R.drawable.comui_tab_home_selected);
        mMessageImageView = (ImageView) findViewById(R.id.message_image_view);
        mMineImageView = (ImageView) findViewById(R.id.mine_image_view);

        mHomeTextView = (TextView) findViewById(R.id.home_image_text);
        mMessageTextView = (TextView) findViewById(R.id.message_image_text);
        mMineTextView = (TextView) findViewById(R.id.mine_image_text);

        //fragment初始化
        mHomeFragment = new HomeFragment();
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //把布局替换成fragment,先移除R.id.content_layout组件，再添加mHomeFragment
        fragmentTransaction.replace(R.id.content_layout, mHomeFragment);
        fragmentTransaction.commit();

    }

    //点击事件
    @Override
    public void onClick(View v) {
        //开启fragment事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            //首页栏
            case R.id.home_layout_view:
                //图标点亮设置
                mHomeImageView.setImageResource(R.drawable.comui_tab_home_selected);
                mMessageImageView.setImageResource(R.drawable.comui_tab_message);
                mMineImageView.setImageResource(R.drawable.comui_tab_person);
                //隐藏其他fragment
                hideFragment(mMessageFragment,fragmentTransaction);
                hideFragment(mMineFragment,fragmentTransaction);
                //显示mHomeFragment
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.content_layout, mHomeFragment);
                } else {
                    fragmentTransaction.show(mHomeFragment);
                }


                break;
            //消息栏
            case R.id.message_layout_view:
                //图标点亮设置
                mHomeImageView.setImageResource(R.drawable.comui_tab_home);
                mMessageImageView.setImageResource(R.drawable.comui_tab_message_selected);
                mMineImageView.setImageResource(R.drawable.comui_tab_person);

                //隐藏其他fragment
                hideFragment(mHomeFragment,fragmentTransaction);
                hideFragment(mMineFragment,fragmentTransaction);
                //显示mHomeFragment
                if (mMessageFragment == null) {
                    mMessageFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.content_layout, mMessageFragment);
                } else {
                    fragmentTransaction.show(mMessageFragment);
                }

                break;
            //消息栏
            case R.id.mine_layout_view:
                //图标点亮设置
                mHomeImageView.setImageResource(R.drawable.comui_tab_home);
                mMessageImageView.setImageResource(R.drawable.comui_tab_message);
                mMineImageView.setImageResource(R.drawable.comui_tab_person_selected);

                //隐藏其他fragment
                hideFragment(mHomeFragment,fragmentTransaction);
                hideFragment(mMessageFragment,fragmentTransaction);
                //显示mHomeFragment
                if (mMineFragment == null) {
                    mMineFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.content_layout, mMineFragment);
                } else {
                    fragmentTransaction.show(mMineFragment);
                }
                break;
            //提交事务
        }
        fragmentTransaction.commit();
    }

    /**
     * 隐藏fragment
     */
    private void hideFragment(Fragment fragment, FragmentTransaction ft) {
        if (fragment != null) {
            ft.hide(fragment);
        }
    }
}
