package com.example.yj.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.yj.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by yj on 2017/9/19.
 * 初始化univer-Image-loader
 * 封装网络图片加载
 */

public class ImageLoaderManager {

    private static final int THREAD_COUNT = 4; //图片加载的线程数
    private static final int PROPRITY = 2;//整个图片加载的优先级
    private static final int DISK_CACHE_SIZE = 50 * 1024;//表明最多可缓存多少图片
    private static final int CONNECTION_TIME_OUT = 5 * 1000;//连接超时时间
    private static final int READ_TIME_OUT = 30 * 1000;//读取超时时间

    private static ImageLoader mImageLoader = null;
    private static ImageLoaderManager mInstance = null;

    public static ImageLoaderManager getInstance(Context context) {
        if (mInstance == null) {
            //在同步块中查找
            synchronized (ImageLoaderManager.class) {
                //同步块中mInstance为空，则创建对象
                if (mInstance == null) {
                    mInstance = new ImageLoaderManager(context);
                }
            }
        }

        return mInstance;
    }

    /**
     * 单例模式---------》私有构造方法
     *
     * @param context
     */
    private ImageLoaderManager(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(THREAD_COUNT)   //图片下载最大线程数量
                .threadPriority(Thread.NORM_PRIORITY - PROPRITY) //正常线程优先级减去图片缓存优先级
                .denyCacheImageMultipleSizesInMemory() //防止缓存多套尺寸的图片到内存
                .memoryCache(new WeakMemoryCache())  //使用弱引用内存缓存
                .diskCacheSize(DISK_CACHE_SIZE)     //分配磁盘内存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())   //使用md5命名文件-------------》安全考虑
                .tasksProcessingOrder(QueueProcessingType.FIFO)     //图片下载顺序
                .defaultDisplayImageOptions(getDefultOptions())     //默认加载图片的Options
                .imageDownloader(new BaseImageDownloader(context, CONNECTION_TIME_OUT, READ_TIME_OUT))//设置图片下载器---------》默认
                .writeDebugLogs()   //debug模式输出日志
                .build();

        ImageLoader.getInstance().init(configuration);
        mImageLoader = ImageLoader.getInstance();

    }

    /**
     * 默认的Options
     *
     * @return
     */
    private DisplayImageOptions getDefultOptions() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.xadsdk_img_error)//图片地址为空显示
                .showImageOnFail(R.drawable.xadsdk_img_error)//加载图片失败显示
                .cacheInMemory(true)  //设置图片磕缓存
                .cacheOnDisk(true)   //设置图片缓存在硬盘
                .bitmapConfig(Bitmap.Config.RGB_565)  //使用图片解码类型
                .decodingOptions(new BitmapFactory.Options())   //图片解码配置
                .build();
        return options;
    }

    /**
     * 加载图片api
     * @param imageView
     * @param url
     * @param options
     * @param listener
     */
    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener listener) {
        if (mImageLoader != null) {
            mImageLoader.displayImage(url, imageView, options, listener);
        }
    }

    /**
     * 加载图片api
     * @param imageView
     * @param url
     * @param listener
     */
    public void displayImage(ImageView imageView, String url,ImageLoadingListener listener) {
        if (mImageLoader != null) {
            mImageLoader.displayImage(url, imageView, null, listener);
        }
    }

    /**
     * 加载图片api
     * @param imageView
     * @param url
     */
    public void displayImage(ImageView imageView, String url) {
        if (mImageLoader != null) {
            mImageLoader.displayImage(url, imageView, null, null);
        }
    }
}
