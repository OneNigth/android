package com.example.yj.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.okhttp.listener.DisposeDataListener;
import com.example.yj.R;
import com.example.yj.activity.base.BaseActivity;
import com.example.yj.adapter.CourseDetailAdapter;
import com.example.yj.model.course.BaseCourseModel;
import com.example.yj.network.http.RequestCenter;
import com.example.yj.view.course.CourseDetailFooterView;
import com.example.yj.view.course.CourseDetailHeaderView;

/**
 * Created by yj on 2017/11/20.
 */

public class CourseDetailActivity extends BaseActivity {
    public static String COURSE_ID = "courseID";

    /**
     * UI
     */
    private ImageView mBackView;
    private ListView mListView;
    private ImageView mLoadingView;
    private CourseDetailAdapter mAdapter;
    private CourseDetailHeaderView mHeaderView;
    private CourseDetailFooterView mFooterView;

    /**
     * data
     */
    private String mCourseID;
    private BaseCourseModel mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);
        initData();
        initView();
        request();//发送请求
    }

    /**
     * intent启动---activity复用
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//更新intent
        initData();
        initView();
        request();//发送请求
    }

    /**
     * 发送请求
     */
    private void request() {
        RequestCenter.requestCourseDetail(mCourseID, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mData = (BaseCourseModel) responseObj;
                updataUI();
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });
    }

    /**
     * 请求成功后更新UI
     */
    private void updataUI() {
        mLoadingView.setVisibility(View.GONE);
        //显示列表数据
        mListView.setVisibility(View.VISIBLE);
        mAdapter= new CourseDetailAdapter(this,mData.data.body);
        mListView.setAdapter(mAdapter);
        //防止headerView多次添加
        if(mHeaderView!=null){
            mListView.removeHeaderView(mHeaderView);
        }
        mHeaderView = new CourseDetailHeaderView(this, mData.data.head);
        mListView.addHeaderView(mHeaderView);
        //防止footerView多次添加
        if (mFooterView != null) {
            mListView.removeFooterView(mFooterView);
        }
        mFooterView = new CourseDetailFooterView(this, mData.data.footer);
        mListView.addFooterView(mFooterView);

    }

    private void initData() {
        mCourseID = getIntent().getStringExtra(COURSE_ID);//课程详情的id
    }

    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_view);
        mListView = (ListView) findViewById(R.id.comment_list_view);
        mLoadingView = (ImageView) findViewById(R.id.loading_view);
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();

    }
}
