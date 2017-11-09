package com.example.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.adutil.LogUtil;
import com.example.adutil.Utils;
import com.example.constant.SDKConstant;
import com.example.core.AdParameters;
import com.example.firstsdk.R;

/**
 * 播放器
 * 负责播放、暂停、回收等事件处理
 * Created by yj on 2017/10/22.
 */

public class CustomVideoView extends RelativeLayout implements View.OnClickListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener {

    /**
     * constant
     */
    private static final String TAG = "CustomVideoView";
    private static final int TIME_MSG = 0x01;//事件类型-------handler
    private static final int TIME_INVAL = 1000;//时间间隔
    //播放器状态
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;

    private static final int LOAD_TOTAL_COUNT = 3;//重试加载次数

    /**
     * UI
     */
    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;//帧数据
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private ImageView mFrameView;
    private AudioManager mAudioManager;//音量控制器
    private Surface mSurface;//真正显示数据的类

    /**
     * data
     */
    private String url;//视频地址
    private String mFrameURI;
    private boolean isMute; //是否静音
    private int mScreenWidth, mDestationHeight;//视频宽、高

    /**
     * Status状态保护
     */
    private boolean canplay = true;
    private boolean isRealPause;
    private boolean isComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;//默认空闲状态

    private MediaPlayer mediaPlayer;
    private ADVideoPlayerListener mListener;//监听事件回调
    private ScereenEventReceiver mScereenEventReceiver;//监听屏幕状态

    //定时更新
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    if (isPlaying()) {
                        //定时更新
                        LogUtil.i(TAG, "TIME_MSG");
                        if (mListener != null) {
                            mListener.onBufferUpdate(getCurrentPosition());//返回当前播放时间
                        }
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);//定时更新
                    }
                    break;
            }
        }
    };

    public CustomVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();//广播注册
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = (TextureView) mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);//保持屏幕亮起
        mVideoView.setSurfaceTextureListener(this);
        initSmallView();//显示小屏布局

    }

    /**
     * 小屏幕
     */
    private void initSmallView() {
        LayoutParams params = new LayoutParams(mScreenWidth, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);
        mMiniPlayBtn = (Button) mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = (ImageView) mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mFrameView = (ImageView) mPlayerView.findViewById(R.id.framing_view);
        mLoadingBar = (ImageView) mPlayerView.findViewById(R.id.loading_bar);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    /**
     * 视频小屏宽高定义
     */
    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int) (mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);//高的9/16
    }

    /**
     * 缓存更新
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 完成播放
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mListener != null) {
            mListener.onVideoPlayComplete();
        }
        playback();//播放器返回初始状态
        setIsRealPause(true);
        setIsComplete(true);
    }

    /**
     * 播放器产生异常
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.d(TAG, "Video is error" + what);
        setPlayerState(STATE_ERROR);
        mediaPlayer = mp;
        if (mp != null) {
            mp.reset();
        }
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            if (mListener != null) {
                mListener.onVideoLoadFailed();
            }
            showPauseView(false);
        }
        stop();
//        reload();
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return true;
    }

    /**
     * 播放器就绪
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtil.i(TAG, "onPrepared");
        showPlayView();
        setPlayerState(STATE_PLAYING);
//        mediaPlayer.start();
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (mListener != null) {
                mListener.onVideoLoadSuccess();
            }
            //满足自动播放条件，则直接播放
            if (Utils.canAutoPlay(getContext(),
                    AdParameters.getCurrentSetting()) &&
                    Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
                setPlayerState(STATE_PAUSING);
                resume();
            } else {
                setPlayerState(STATE_PLAYING);
                pause();
            }
        }
    }

    private void decideCanPlay() {
        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT)
            //来回切换页面时，只有 >50,且满足自动播放条件才自动播放
            resume();
        else
            pause();
    }

    /**
     * TextureView就绪
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtil.i(TAG, "onSurfaceTextureAvailable");
        mSurface = new Surface(surface);
        checkMediaPlayer();//确保mediaplayer再surface创建后
        mediaPlayer.setSurface(mSurface);//防止Texture回收
        load();//在surface初始化后开始加载
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtil.i(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {
        if (v == mMiniPlayBtn) {
            if (playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    mListener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v == mFullBtn) {
            mListener.onClickFullScreenBtn();
        } else if (v == mVideoView) {
            mListener.onClickVideo();
        }
    }



    /**
     * view显示与否变化
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        LogUtil.e(TAG, "onVisibilityChanged:" + visibility);
        super.onVisibilityChanged(changedView, visibility);
        //视频播放器可见且在播放状态
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause || isComplete) {//已经播放完成或手动暂停状态
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }
    }

    /**
     * 自行处理在播放器view中的触摸事件
     * 防止与父容器中事件产生冲突
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 视频UI点击事件的监听回调
     */
    public interface ADVideoPlayerListener {
        void onBufferUpdate(int time);

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onVideoLoadSuccess();

        void onVideoLoadFailed();

        void onVideoPlayComplete();
    }

    /**
     * 加载视频
     */
    public void load() {
        if (playerState != STATE_IDLE) {
            return;
        }
        showLoadingView();
        LogUtil.d(TAG, "Video is loading.......url is:" + url);
        try {
            setPlayerState(STATE_IDLE);
            checkMediaPlayer();//创建mediaPlayer
            mute(false);//先打开声音---------------------正式再改为true
            mediaPlayer.setDataSource(url);
            mediaPlayer.setSurface(mSurface);//
            mediaPlayer.prepareAsync();//异步加载
        } catch (Exception e) {
            e.printStackTrace();
            stop();
//            reload();
        }
    }

    /**
     * 暂停视频
     */
    public void pause() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        LogUtil.d(TAG, "do pause");
        setPlayerState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!canplay) {
                mediaPlayer.seekTo(0);
            }
        }
        showPauseView(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 恢复播放
     */
    public void resume() {
        if (playerState != STATE_PAUSING) {
            return;
        }
        LogUtil.d(TAG, "Video is doing resume");
        if (!isPlaying()) {
            entryResumeState();//进入播放状态
            mediaPlayer.setOnSeekCompleteListener(null);
            showPauseView(true);
            mediaPlayer.start();
            mHandler.sendEmptyMessage(TIME_MSG);
        } else {
            showPauseView(false);
        }
    }

    /**
     * 进入播放状态
     */
    private void entryResumeState() {
        canplay = true;
        setPlayerState(STATE_PLAYING);
        setIsComplete(false);
        setIsRealPause(false);
    }

    /**
     * 完成播放后恢复初始状态
     */
    public void playback() {
        LogUtil.d(TAG, "video is playback");
        //播放完成后进度跳转到0并处于暂停状态,———————>下次播放不耗费流量
        setPlayerState(STATE_PAUSING);
        mHandler.removeCallbacksAndMessages(null);
        showPauseView(false);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
    }

    /**
     * 播放停止
     */
    public void stop() {
        LogUtil.d(TAG, "video is doing stop");
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);//移除handler循环
        setPlayerState(STATE_IDLE);//设置空闲状态
        if (mCurrentCount < LOAD_TOTAL_COUNT) { //满足重新加载的条件
            mCurrentCount += 1;
            load();
        } else {
            showPauseView(false); //显示暂停状态
        }
    }

//    /**
//     * 重新加载
//     */
//    private void reload() {
//        //小于三次重新加载，否则显示加载失败
//        if (mCurrentCount < LOAD_TOTAL_COUNT) {
//            load();
//            mCurrentCount++;
//        } else {
//            showPauseView(false);
//        }
//    }

    /**
     * 销毁当前自定义view
     */
    public void destroy() {
        LogUtil.d(TAG, "video is doing destroy");
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        setPlayerState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unregisterBroadcastReceiver();
        mHandler.removeCallbacksAndMessages(null); //release all message and runnable
        showPauseView(false); //除了播放和loading外其余任何状态都显示pause
    }

    /**
     * 跳到指定点播放视频
     */
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    LogUtil.d(TAG, "video is doing seek and resume");
                    mediaPlayer.start();
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    /**
     * 跳到制定点暂停视频
     */
    public void seekAndPause(int position) {
        if (playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setPlayerState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);//无法确定完成时机，通过回调来监听--------》适配
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {//跳转成功后
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    LogUtil.d(TAG, "video is doing seek and pause");
                    mediaPlayer.pause();
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    public void setListener(ADVideoPlayerListener listener) {
        mListener = listener;
    }

    /**
     * mediaplayer为空则创建
     */
    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();
        }
    }

    /**
     * 创建mediaplayer
     */
    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (mSurface != null && mSurface.isValid()) {
            mediaPlayer.setSurface(mSurface);
        } else {
            stop();
        }

        return mediaPlayer;
    }

    private void registerBroadcastReceiver() {
        if (mScereenEventReceiver == null) {
            mScereenEventReceiver = new ScereenEventReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);//只能通过代码动态的形式注册
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScereenEventReceiver, intentFilter);
        }
    }

    private void unregisterBroadcastReceiver() {
        if (mScereenEventReceiver != null) {
            getContext().unregisterReceiver(mScereenEventReceiver);
        }
    }

    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        loadFrameImage();
    }

    private ADFrameImageLoadListener mFrameLoadListener;

    public void setFrameImageLoadListener(ADFrameImageLoadListener frameListener) {
        mFrameLoadListener = frameListener;
    }

    /**
     * 异步加载定帧图
     */
    private void loadFrameImage() {
        if (mFrameLoadListener != null) {
            mFrameLoadListener.onStartFrameLoad(mFrameURI, new ImageLoaderListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    if (loadedImage != null) {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mFrameView.setImageBitmap(loadedImage);
                    } else {
                        mFrameView.setScaleType(ImageView.ScaleType.CENTER);
                        mFrameView.setImageResource(R.drawable.xadsdk_img_error);
                    }
                }
            });
        }
    }

    private void showPauseView(boolean isShow) {
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        if (!isShow) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
    }

    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isRealPause() {
        return isRealPause;
    }


    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        } else
            return false;
    }


    /**
     * true is no voice
     *
     * @param mute
     */
    public void mute(boolean mute) {
        LogUtil.d(TAG, "mute");
        isMute = mute;
        if (mediaPlayer != null && mAudioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 监听屏幕事件
     */
    private class ScereenEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //锁屏时暂停，解锁时继续播放
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF://屏幕锁屏
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
                case Intent.ACTION_USER_PRESENT://解锁
                    if (playerState == STATE_PAUSING) {
                        if (isRealPause) {//播放完成后的暂停，手动暂停则仍然暂停
                            pause();
                        } else {
                            decideCanPlay();
                        }
                    }
                    break;
            }
        }
    }

    public interface ADFrameImageLoadListener {
        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public boolean isFrameHidden() {
        return mFrameView.getVisibility() == View.VISIBLE ? false : true;
    }

    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         *
         * @param loadedImage
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

    public void setDataSoure(String url) {
        this.url = url;
    }

    public void setFrameURI(String mFrameURI) {
        this.mFrameURI = mFrameURI;
    }

    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }

    /**
     * 播放时长
     */
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 当前播放时间
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void setIsRealPause(boolean realPause) {
        isRealPause = realPause;
    }

    public void setIsComplete(boolean complete) {
        isComplete = complete;
    }

    public void setFrameLoadListener(ADFrameImageLoadListener frameLoadListener) {
        mFrameLoadListener = frameLoadListener;
    }
}
