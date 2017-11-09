package com.example.yj.view.fragment.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exception.OkHttpException;
import com.example.okhttp.listener.DisposeDataListener;
import com.example.yj.R;
import com.example.yj.adapter.CourseAdapter;
import com.example.yj.model.recommand.BaseRecommandModel;
import com.example.yj.network.http.RequestCenter;
import com.example.yj.view.fragment.BaseFragment;
import com.example.yj.view.home.HomeHeaderLayout;
import com.example.yj.zxing.app.CaptureActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by yj on 2017/9/15.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_QRCODE = 0x01;
    /**
     * UI
     */
    private View view;  //资源获取
    private TextView mCategoryTextView;
    private TextView mSearchView;
    private ListView mListView;
    private ImageView mLoadView;
    private TextView mQrcodeView;//二维码

    /**
     * data
     */
    private BaseRecommandModel mRecommandData;
    private CourseAdapter mCourseAdapter;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //首页数据请求
        requestRecommandData();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //界面资源初始化
        initView();
        return view;
    }

    private void initView() {
        mSearchView = (TextView) view.findViewById(R.id.search_view);
        mCategoryTextView = (TextView) view.findViewById(R.id.category_view);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mLoadView = (ImageView) view.findViewById(R.id.loading_view);
        mQrcodeView = (TextView) view.findViewById(R.id.qrcode_view);
        mQrcodeView.setOnClickListener(this);

        AnimationDrawable anim = (AnimationDrawable) mLoadView.getDrawable();
        anim.start();
    }

    private void requestRecommandData() {
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
//                Log.d(TAG, "onSuccess: " + responseObj.toString());
                mRecommandData = (BaseRecommandModel) responseObj;
                //更新视图
                showSuccessView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.d(TAG, "onFailure:请求失败 " + ((OkHttpException) reasonObj).getEmsg());
            }
        });
    }

    /**
     * 请求成功执行方法
     */
    private void showSuccessView() {
        //数据是否为空
        if (mRecommandData.data.list != null && mRecommandData.data.list.size() > 0) {
            mLoadView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            //创建适配器
            mCourseAdapter = new CourseAdapter(mContext, mRecommandData.data.list);
            mListView.setAdapter(mCourseAdapter);
            //添加列表头
            mListView.addHeaderView(new HomeHeaderLayout(mContext, mRecommandData.data.head));
            //添加滑动时间监听
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mCourseAdapter.updateAdInScrollView();//视频可视面积大于50%自动播放
                }
            });

        } else {
            showErrorView();
        }
    }

    /**
     * 显示错误视图
     */
    private void showErrorView() {
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.qrcode_view://弹出扫码界面
                ActivityCompat.requestPermissions(mContext,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);//6.0以上使用照相机需要在配置清单添加权限，同时需要使用代码动态申请权限
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_QRCODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QRCODE:
                //扫码返回结果处理
                if (resultCode == Activity.RESULT_OK) {
                    String code = data.getStringExtra("SCAN_RESULT");
                    Toast.makeText(mContext,"二维码结果:"+code,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
