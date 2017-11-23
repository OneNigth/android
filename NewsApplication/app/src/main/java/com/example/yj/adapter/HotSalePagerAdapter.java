package com.example.yj.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adutil.LogUtil;
import com.example.yj.R;
import com.example.yj.activity.CourseDetailActivity;
import com.example.yj.model.recommand.RecommandBodyValue;
import com.example.yj.util.ImageLoaderManager;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by yj on 2017/9/26.
 */

public class HotSalePagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<RecommandBodyValue> mData;
    private LayoutInflater mLnflater;
    private ImageLoaderManager mManager;


    public HotSalePagerAdapter(Context context, ArrayList<RecommandBodyValue> list) {
        mContext = context;
        mData = list;
        mLnflater = LayoutInflater.from(mContext);
        mManager = ImageLoaderManager.getInstance(mContext);
    }

    @Override
    public int getCount() {
        //返回最大值---------无限滚动
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //取余-----------无线滚动
        final RecommandBodyValue bodyValue = mData.get(position % mData.size());
        /**
         * 初始化
         */
        View rootView = mLnflater.inflate(R.layout.item_hot_product_pager_layout, null);
        TextView titleView = (TextView) rootView.findViewById(R.id.title_view);
        final TextView infoView = (TextView) rootView.findViewById(R.id.info_view);
        TextView gonggaoView = (TextView) rootView.findViewById(R.id.gonggao_view);
        TextView saleView = (TextView) rootView.findViewById(R.id.sale_num_view);
        ImageView[] imageViews = new ImageView[3];
        imageViews[0] = (ImageView) rootView.findViewById(R.id.image_one);
        imageViews[1] = (ImageView) rootView.findViewById(R.id.image_two);
        imageViews[2] = (ImageView) rootView.findViewById(R.id.image_three);
        /**
         * 组件添加数据
         */
        LogUtil.d(TAG, "instantiateItem: titleView----------------------->"+titleView);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , CourseDetailActivity.class);
                intent.putExtra(CourseDetailActivity.COURSE_ID,bodyValue.adid);
                mContext.startActivity(intent);
            }
        });
        titleView.setText(bodyValue.title);
        infoView.setText(bodyValue.price);
        gonggaoView.setText(bodyValue.info);
        saleView.setText(bodyValue.text);
        //加载图片
        for (int i = 0; i < imageViews.length; i++) {
            mManager.displayImage(imageViews[i], bodyValue.url.get(i));
        }
        container.addView(rootView);
        return rootView;

    }

}
