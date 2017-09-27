package com.example.yj.view.fragment.home;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.exception.OkHttpException;
import com.example.listener.DisposeDataListener;
import com.example.yj.R;
import com.example.yj.adapter.CourseAdapter;
import com.example.yj.model.recommand.BaseRecommandModel;
import com.example.yj.network.http.RequestCenter;
import com.example.yj.view.fragment.BaseFragment;

import static android.content.ContentValues.TAG;

/**
 * Created by yj on 2017/9/15.
 */

public class HomeFragment extends BaseFragment {

    /**
     * UI
     */
    private View view;  //资源获取
    private TextView mCategoryTextView;
    private TextView mSearchView;
    private ListView mListView;
    private ImageView mLoadView;

    /**
     * data
     */
    private BaseRecommandModel mRecommandData ;
    private CourseAdapter mCourseAdapter;

    public HomeFragment(){

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
                Log.d(TAG, "onFailure: " + ((OkHttpException)reasonObj).getEmsg().toString());
            }
        });
    }

    /**
     *请求成功执行方法
     */
    private void showSuccessView() {
        //数据是否为空
        if (mRecommandData.data.list!=null&&mRecommandData.data.list.size()>0){
            mLoadView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            //创建适配器
            mCourseAdapter = new CourseAdapter(mContext,mRecommandData.data.list);
            mListView.setAdapter(mCourseAdapter);
        }else {
            showErrorView();
        }
    }

    /**
     * 显示错误视图
     */
    private void showErrorView() {
    }


}
