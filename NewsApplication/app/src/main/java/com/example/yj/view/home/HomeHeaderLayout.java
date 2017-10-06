package com.example.yj.view.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yj.R;
import com.example.yj.adapter.PhotoPagerAdapter;
import com.example.yj.model.recommand.RecommandFooterValue;
import com.example.yj.model.recommand.RecommandHeadValue;
import com.example.yj.util.ImageLoaderManager;
import com.example.yj.view.viewpagerindictor.CirclePageIndicator;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by yj on 2017/9/27.
 */

public class HomeHeaderLayout extends RelativeLayout {
    private Context mContext;
    /**
     * UI
     */
    private RelativeLayout mRootView;
    private AutoScrollViewPager mViewPager;
    private CirclePageIndicator mPagerIndicator;
    private TextView mHotView;
    private PhotoPagerAdapter mAdapter;
    private ImageView[] mImageViews = new ImageView[4];
    private LinearLayout mFootLayout;

    private RecommandHeadValue mHeadValue;
    private ImageLoaderManager mImageLoaderManager;

    public HomeHeaderLayout(Context context, RecommandHeadValue headValue) {
        this(context, null, headValue);
    }

    public HomeHeaderLayout(Context context, AttributeSet attrs, RecommandHeadValue headValue) {
        super(context, attrs);
        mContext = context;
        mHeadValue = headValue;
        mImageLoaderManager = ImageLoaderManager.getInstance(mContext);
        initView();
    }

    private void initView() {
        //初始化控件
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootView = (RelativeLayout) inflater.inflate(R.layout.listview_home_head_layout, this);
        mViewPager = (AutoScrollViewPager) mRootView.findViewById(R.id.pager);
        mPagerIndicator = (CirclePageIndicator) mRootView.findViewById(R.id.pager_indictor_view);
        mHotView = (TextView) mRootView.findViewById(R.id.zuixing_view);
        mImageViews[0] = (ImageView) mRootView.findViewById(R.id.head_image_one);
        mImageViews[1] = (ImageView) mRootView.findViewById(R.id.head_image_two);
        mImageViews[2] = (ImageView) mRootView.findViewById(R.id.head_image_three);
        mImageViews[3] = (ImageView) mRootView.findViewById(R.id.head_image_four);
        mFootLayout = (LinearLayout) mRootView.findViewById(R.id.content_layout);

        //向组件填充数据
        mAdapter = new PhotoPagerAdapter(mContext, mHeadValue.ads, true);
        mViewPager.setAdapter(mAdapter);
        mViewPager.startAutoScroll(3000);
        mPagerIndicator.setViewPager(mViewPager);

        //加载图片
        for (int i = 0; i < mImageViews.length; i++) {
            mImageLoaderManager.displayImage(mImageViews[i],mHeadValue.middle.get(i));
        }

        for (RecommandFooterValue value : mHeadValue.footer){
            mFootLayout.addView(creatItem(value));
        }

        mHotView.setText(mContext.getString(R.string.today_zuixing));
    }

    private View creatItem(RecommandFooterValue value) {
        HomeBottomItem homeBottomItem = new HomeBottomItem(mContext,value);
        return homeBottomItem;
    }
}
