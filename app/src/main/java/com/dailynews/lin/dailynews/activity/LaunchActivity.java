package com.dailynews.lin.dailynews.activity;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.base.BaseActivity;
import com.trycatch.mysnackbar.TSnackbar;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//1.继承自BaseActivity
public class LaunchActivity extends BaseActivity {
    @Bind(R.id.textureView)
    TextureView mTV;
    @Bind(R.id.btn_signin_launch)
    Button btn_signin;
    @Bind(R.id.btn_signup_launch)
    Button btn_signup;
    private MediaPlayer mMediaPlayer;
    int pausePosition;

    @OnClick(R.id.btn_signin_launch)
    public void longin() {
        startActivity(DisembarkActivity.class);
        LaunchActivity.this.finish();
    }

    @OnClick(R.id.btn_signup_launch)
    public void signup() {
        TSnackbar.make(btn_signin,R.string.userid_found,TSnackbar.LENGTH_SHORT).show();
    }

    //MediaPlayer
    //1.播放什么--数据
    //2.到哪播放--SurfaView(缺点：平移，旋转，缩放)，TextuerView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //绑定注解
        ButterKnife.bind(this);
        mMediaPlayer = new MediaPlayer();
        mTV.setSurfaceTextureListener(l);
    }

    private TextureView.SurfaceTextureListener l = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            try {
                mMediaPlayer = new MediaPlayer();
                AssetFileDescriptor afd = getAssets().openFd("ResidentEvil.mp4");
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength()); //设置要播放的数据
                Surface surface = new Surface(surfaceTexture);
                mMediaPlayer.setSurface(surface);//加入储存
                mMediaPlayer.setLooping(true);//是否循环
                mMediaPlayer.prepareAsync();//异步缓存播放
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            //记录暂停位置
            pausePosition = mMediaPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!mMediaPlayer.isPlaying()) {
            //从记录位置播放
            mMediaPlayer.seekTo(pausePosition);
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            //停止播放
            mMediaPlayer.stop();
            //释放资源
            mMediaPlayer.release();
        }
    }
}

