package com.example.yj.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.activity.AdBrowserActivity;
import com.example.adutil.LogUtil;
import com.example.adutil.Utils;
import com.example.core.AdContextInterface;
import com.example.core.video.VideoAdContext;
import com.example.yj.R;
import com.example.yj.model.recommand.RecommandBodyValue;
import com.example.yj.util.ImageLoaderManager;
import com.example.yj.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yj on 2017/9/25.
 */

public class CourseAdapter extends BaseAdapter {
    private String TAG = "CourseAdapter";

    private static final int CARD_COUNT = 4;//一共有四种类型
    public static final int VIDEO_TYPE = 0x00;
    public static final int CARD_TYPE_ONE = 0x01;//横向多图
    public static final int CARD_TYPE_TWO = 0x02;//单图
    public static final int CARD_TYPE_THREE = 0x03;//viewpager

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<RecommandBodyValue> mData;
    private ImageLoaderManager mImageLoaderManager;//图片加载
    private ViewHolder mViewHolder;//UI组件
    private VideoAdContext mVideoAdContext;

    public CourseAdapter(Context context, ArrayList<RecommandBodyValue> data) {
        mData = data;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mImageLoaderManager = ImageLoaderManager.getInstance(mContext);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return CARD_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        RecommandBodyValue bodyValue = (RecommandBodyValue) getItem(position);
        return bodyValue.type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //当前position对应类型
        int type = getItemViewType(position);
        final RecommandBodyValue bodyValue = (RecommandBodyValue) getItem(position);
        //初始化数据
        //无缓存时
        if (convertView == null) {
            switch (type) {
                case CARD_TYPE_ONE://多图item
                    mViewHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_product_card_one_layout, parent, false);
                    //初始化CardOne
                    mViewHolder.logoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.footerView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.infoTextView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.titleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.priceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.fromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.zanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    mViewHolder.productLayout = (LinearLayout) convertView.findViewById(R.id.product_photo_layout);
                    break;
                case CARD_TYPE_TWO://单图item
                    mViewHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_product_card_two_layout, parent, false);
                    //初始化CardTwo
                    mViewHolder.logoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.footerView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.infoTextView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.titleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.priceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.fromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.zanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    mViewHolder.productView = (ImageView) convertView.findViewById(R.id.product_photo_view);
                    break;
                case CARD_TYPE_THREE://viewpager
                    mViewHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_product_card_three_layout,parent,false);
                    mViewHolder.viewPager = (ViewPager) convertView.findViewById(R.id.pager);
//                    mViewHolder.viewPager.setPageMargin(Utils.dip2px(mContext,5));
                    //使用工具类转换格式
                    ArrayList<RecommandBodyValue> recommandList = Util.handleData(bodyValue);
                    //添加适配器
                    mViewHolder.viewPager.setAdapter(new HotSalePagerAdapter(mContext,recommandList));
                    //无限循环-----------------从中间开始
                    mViewHolder.viewPager.setCurrentItem(recommandList.size()*100);
                    break;
                case VIDEO_TYPE:
//                    convertView = mInflater.inflate(R.layout.item_product_card_one_layout, parent, false);//晚些时候删除
                    //显示video卡片
                    mViewHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_video_layout, parent, false);
                    mViewHolder.videoContentLayout = (RelativeLayout)
                            convertView.findViewById(R.id.video_ad_layout);
                    mViewHolder.logoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.titleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.infoTextView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.footerView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.shareView = (ImageView) convertView.findViewById(R.id.item_share_view);
                    //为对应布局创建播放器
                    String url = new Gson().toJson(bodyValue);
                    LogUtil.d(TAG,"视频："+url);
                    mVideoAdContext = new VideoAdContext(mViewHolder.videoContentLayout,url);
                    mVideoAdContext.setAdResultListener(new AdContextInterface() {//广告
                        @Override
                        public void onAdSuccess() {
                        }

                        @Override
                        public void onAdFailed() {
                        }

                        @Override
                        public void onClickVideo(String url) {
                            Intent intent = new Intent(mContext, AdBrowserActivity.class);
                            intent.putExtra(AdBrowserActivity.KEY_URL, url);
                            mContext.startActivity(intent);
                        }
                    });
                    break;
            }
            //设置缓存
            convertView.setTag(mViewHolder);
        } else {//从缓存读取
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        //向item填充数据
        switch (type) {
            case VIDEO_TYPE:
                mImageLoaderManager.displayImage(mViewHolder.logoView, bodyValue.logo);
                mViewHolder.titleView.setText(bodyValue.title);
                mViewHolder.infoTextView.setText(bodyValue.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.footerView.setText(bodyValue.text);
//                mViewHolder.shareView.setOnClickListener(new View.OnClickListener() {//分享
//                    @Override
//                    public void onClick(View v) {
//                        ShareDialog dialog = new ShareDialog(mContext, false);
//                        dialog.setShareType(Platform.SHARE_VIDEO);
//                        dialog.setShareTitle(bodyValue.title);
//                        dialog.setShareTitleUrl(bodyValue.site);
//                        dialog.setShareText(bodyValue.text);
//                        dialog.setShareSite(bodyValue.title);
//                        dialog.setShareTitle(bodyValue.site);
//                        dialog.setUrl(bodyValue.resource);
//                        dialog.show();
//                    }
//                });
                break;
            case CARD_TYPE_ONE://多图item
                mViewHolder.footerView.setText(bodyValue.text);
                mViewHolder.titleView.setText(bodyValue.title);
                mViewHolder.infoTextView.setText(bodyValue.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.priceView.setText(bodyValue.price);
                mViewHolder.zanView.setText(bodyValue.zan.concat(mContext.getString(R.string.dian_zan)));
                mViewHolder.fromView.setText(bodyValue.from);
                /**
                 * 图片加载
                 */
                //先删除已有图片再添加view
                mViewHolder.productLayout.removeAllViews();
                for (String url : bodyValue.url) {
                    mViewHolder.productLayout.addView(creatImageView(url));
                }
                break;

            case CARD_TYPE_TWO://单图item
                mViewHolder.footerView.setText(bodyValue.text);
                mViewHolder.titleView.setText(bodyValue.title);
                mViewHolder.infoTextView.setText(bodyValue.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.priceView.setText(bodyValue.price);
                mViewHolder.zanView.setText(bodyValue.zan.concat(mContext.getString(R.string.dian_zan)));
                mViewHolder.fromView.setText(bodyValue.from);
                /**
                 * 图片加载
                 */
                mImageLoaderManager.displayImage(mViewHolder.logoView, bodyValue.logo);
                mImageLoaderManager.displayImage(mViewHolder.productView, bodyValue.url.get(0));
                break;
        }
        return convertView;
    }

    /**
     * 出现屏幕大于50%时自动播放
     */
    public void updateAdInScrollView(){
        if(mVideoAdContext!=null){
            mVideoAdContext.updateVideoInScrollView();
        }
    }
    /**
     * 创建ImageView
     */
    private ImageView creatImageView(String url) {
        ImageView imageView = new ImageView(mContext);
        //LinearLayout使用与viewgroup保持一致
        //设置布局参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utils.dip2px(mContext, 100), LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = Utils.dip2px(mContext, 5);
        imageView.setLayoutParams(layoutParams);

        mImageLoaderManager.displayImage(imageView, url);
        return imageView;
    }

    class ViewHolder {
        //所有Card共有属性
        private CircleImageView logoView;
        private TextView titleView;
        private TextView infoTextView;
        private TextView footerView;

        //videoCard属性
        private RelativeLayout videoContentLayout;
        private ImageView shareView;

        //CardOne、CardTwo属性
        private TextView priceView;
        private TextView fromView;
        private TextView zanView;

        //CardOne属性：横向多图
        private LinearLayout productLayout;

        //CardTwo属性：单图
        private ImageView productView;

        //CardThree属性：viewPager
        private ViewPager viewPager;

    }
}
