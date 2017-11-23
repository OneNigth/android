package com.example.yj.view.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yj.R;
import com.example.yj.activity.CourseDetailActivity;
import com.example.yj.model.course.CourseFooterRecommandValue;
import com.example.yj.model.course.CourseFooterValue;
import com.example.yj.util.ImageLoaderManager;

/**
 * Created by yj on 2017/11/20.
 */

public class CourseDetailFooterView extends RelativeLayout {

    private Context mContext;
    /**
     * ui
     */
    private RelativeLayout mRootView;
    private ImageView[] mImageViews = new ImageView[2];
    private TextView[] mNameViews = new TextView[2];
    private TextView[] mPriceViews = new TextView[2];
    private TextView[] mZanViews = new TextView[2];
    /**
     * data
     */
    private CourseFooterValue mData;
    private ImageLoaderManager mImageLoader;

    public CourseDetailFooterView(Context context , CourseFooterValue footerValue) {
        super(context);
        mContext = context;
        mData = footerValue;
        mImageLoader = ImageLoaderManager.getInstance(mContext);
        initView();
    }

    private void initView() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootView = (RelativeLayout) inflater.inflate(R.layout.listview_course_comment_footer_layout, this);
//        contentLayout = (LinearLayout) mRootView.findViewById(R.id.line_Layout);
        mImageViews[0] = (ImageView) mRootView.findViewById(R.id.image_one_view);
        mImageViews[1] = (ImageView) mRootView.findViewById(R.id.image_two_view);
        mNameViews[0] = (TextView) mRootView.findViewById(R.id.name_one_view);
        mNameViews[1] = (TextView) mRootView.findViewById(R.id.name_two_view);
        mPriceViews[0] = (TextView) mRootView.findViewById(R.id.price_one_view);
        mPriceViews[1] = (TextView) mRootView.findViewById(R.id.price_two_view);
        mZanViews[0] = (TextView) mRootView.findViewById(R.id.zan_one_view);
        mZanViews[1] = (TextView) mRootView.findViewById(R.id.zan_two_view);

        //动态在foot中添加推荐课程信息
        for (int i = 0 ; i<mData.recommand.size();i++){//将请求信息逐一添加
            final CourseFooterRecommandValue value = mData.recommand.get(i);
            mImageLoader.displayImage(mImageViews[i],value.imageUrl);
            mImageViews[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CourseDetailActivity.class);
                    intent.putExtra(CourseDetailActivity.COURSE_ID,value.courseId);
                    mContext.startActivity(intent);//通过配置文件设置覆盖原先activity
                }
            });
            mNameViews[i].setText(value.name);
            mPriceViews[i].setText(value.price);
            mZanViews[i].setText(value.zan);
        }
    }
}
