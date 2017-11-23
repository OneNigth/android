package com.example.yj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adutil.Utils;
import com.example.yj.R;
import com.example.yj.activity.base.BaseActivity;
import com.example.yj.adapter.PhotoPagerAdapter;

import java.util.ArrayList;

/**
 * Created by yj on 2017/11/14.
 */

public class PhotoActivity extends BaseActivity {
    public static final String PHOTO_LIST = "photo_list";
    /**
     * ui
     */
    private TextView mIndictorView;
    private ViewPager mViewPager;
    private ImageView mShareView;
    /**
     * data
     */
    private ArrayList<String> mPhotoList;
    private PhotoPagerAdapter mPhotoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mPhotoList = intent.getStringArrayListExtra(PHOTO_LIST);//获取图片地址

    }

    private void initView() {
        mIndictorView = (TextView) findViewById(R.id.indictor_view);
        mViewPager = (ViewPager) findViewById(R.id.photo_pager);
        mShareView = (ImageView) findViewById(R.id.share_view);
        mPhotoAdapter = new PhotoPagerAdapter(PhotoActivity.this,mPhotoList,false);
        mViewPager.setPageMargin(Utils.dip2px(PhotoActivity.this, 30));
        mViewPager.setAdapter(mPhotoAdapter);
    }
}
