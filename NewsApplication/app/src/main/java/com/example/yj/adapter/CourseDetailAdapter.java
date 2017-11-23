package com.example.yj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yj.R;
import com.example.yj.model.course.CourseCommentValue;
import com.example.yj.util.ImageLoaderManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yj on 2017/11/20.
 */

public class CourseDetailAdapter extends BaseAdapter {

    private static final int BUILDING_OWNER = 0;//楼主标志
    private Context mContext;

    private List<CourseCommentValue> mList ;
    private LayoutInflater mInflater ;
    private ViewHolder mViewHolder;
    private ImageLoaderManager mImageLoader;

    public CourseDetailAdapter(Context context , List<CourseCommentValue> list){
        mContext = context ;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderManager.getInstance(mContext);
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_comment_layout,parent,false);
            mViewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.photo_view);
            mViewHolder.mNameView = (TextView) convertView.findViewById(R.id.name_view);
            mViewHolder.mCommentView = (TextView) convertView.findViewById(R.id.text_view);
            mViewHolder.mOwnerView = (TextView) convertView.findViewById(R.id.owner_view);
            convertView.setTag(mViewHolder);
        }else {
            mViewHolder = (ViewHolder) convertView.getTag();//从缓存读取
        }
        CourseCommentValue value = (CourseCommentValue) getItem(position);//获取课程详情实体
        if(value.type == BUILDING_OWNER){//是楼主则显示楼主标志
            mViewHolder.mOwnerView.setVisibility(View.VISIBLE);
        }else {
            mViewHolder.mOwnerView.setVisibility(View.GONE);
        }
        mImageLoader.displayImage(mViewHolder.mImageView,value.logo);
        mViewHolder.mNameView.setText(value.name);
        mViewHolder.mCommentView.setText(value.text);
        return convertView;
    }

    private class ViewHolder{
        CircleImageView mImageView;
        TextView mNameView;
        TextView mCommentView;
        TextView mOwnerView;
    }
}
