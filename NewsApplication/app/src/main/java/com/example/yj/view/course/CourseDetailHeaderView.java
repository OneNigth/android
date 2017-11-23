package com.example.yj.view.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adutil.Utils;
import com.example.core.video.VideoAdContext;
import com.google.gson.Gson;
import com.example.yj.R;
import com.example.yj.activity.PhotoActivity;
import com.example.yj.model.course.CourseHeaderValue;
import com.example.yj.util.ImageLoaderManager;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yj on 2017/11/20.
 */

public class CourseDetailHeaderView extends RelativeLayout {
    private Context mContext;

    /**
     * ui
     */
    private RelativeLayout mRootView;
    private CircleImageView mPhotoView;
    private TextView mNameView;
    private TextView mDayView;
    private TextView mOldValueView;
    private TextView mNewValueView;
    private TextView mIntroductView;
    private TextView mFromView;
    private TextView mZanView;
    private TextView mScanView;
    private LinearLayout mPhotosLayout;
    private RelativeLayout mVideoLayout;
    private TextView mHotCommentView;
    private VideoAdContext mVideoAdContext;
    /**
     * data
     */
    private CourseHeaderValue mData;
    private ImageLoaderManager mLoaderManager;

    public CourseDetailHeaderView(Context context, CourseHeaderValue headerValue) {
        super(context);
        mContext = context;
        mData = headerValue;
        mLoaderManager = ImageLoaderManager.getInstance(mContext);
        initView();
    }

    private void initView() {
        mRootView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.listview_course_comment_head_layout, this);
        mPhotoView = (CircleImageView) mRootView.findViewById(R.id.photo_view);
        mNameView = (TextView) mRootView.findViewById(R.id.name_view);
        mDayView = (TextView) mRootView.findViewById(R.id.day_view);
        mOldValueView = (TextView) mRootView.findViewById(R.id.old_value_view);
        mNewValueView = (TextView) mRootView.findViewById(R.id.new_value_view);
        mIntroductView = (TextView) mRootView.findViewById(R.id.introduct_view);
        mFromView = (TextView) mRootView.findViewById(R.id.from_view);
        mPhotosLayout = (LinearLayout) mRootView.findViewById(R.id.picture_layout);

        mPhotosLayout.setOnClickListener(new OnClickListener() {//点击图片跳转到图片显示activity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoActivity.class);
                intent.putStringArrayListExtra(PhotoActivity.PHOTO_LIST, mData.photoUrls);
                mContext.startActivity(intent);
            }
        });
        mVideoLayout = (RelativeLayout) mRootView.findViewById(R.id.video_view);
        mZanView = (TextView) mRootView.findViewById(R.id.zan_view);
        mScanView = (TextView) mRootView.findViewById(R.id.scan_view);
        mHotCommentView = (TextView) mRootView.findViewById(R.id.hot_comment_view);

        mLoaderManager.displayImage(mPhotoView, mData.logo);
        mNameView.setText(mData.name);
        mDayView.setText(mData.dayTime);
        mOldValueView.setText(mData.oldPrice);
        mOldValueView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//删除的横杠字体样式
        mNewValueView.setText(mData.newPrice);
        mIntroductView.setText(mData.text);
        mFromView.setText(mData.from);
        mZanView.setText(mData.zan);
        mScanView.setText(mData.scan);
        mHotCommentView.setText(mData.hotComment);

        for (String url : mData.photoUrls) {//动态添加多个图片
            mPhotosLayout.addView(createImageItem(url));
        }
        if (!TextUtils.isEmpty(mData.video.resource)) {
            mVideoAdContext = new VideoAdContext(mVideoLayout,
                    new Gson().toJson(mData.video));
        }
    }

    private ImageView createImageItem(String url) {
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(mContext, 150));
        params.topMargin = Utils.dip2px(mContext, 10);
        imageView.setLayoutParams(params);
        mLoaderManager.displayImage(imageView, url);
        return imageView;
    }

}
