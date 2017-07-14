package com.netease.im.session;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.media.player.OnPlayListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by dowin on 2017/7/13.
 */

public final class AudioPlayerM {

    enum Type {
        file, raw, assets
    }

    public static final String TAG = "AudioPlayer";
    private MediaPlayer mPlayer;
    private String mAudioFile;
    private Type type = Type.file;
    private AssetFileDescriptor assetFileDescriptor;
    private long mIntervalTime;
    private AudioManager audioManager;
    private OnPlayListener mListener;
    private int audioStreamType;
    private static final int WHAT_COUNT_PLAY = 0;
    private static final int WHAT_DECODE_SUCCEED = 1;
    private static final int WHAT_DECODE_FAILED = 2;
    private Handler mHandler;
    private Context mContext;
    OnAudioFocusChangeListener onAudioFocusChangeListener;

    public AudioPlayerM(Context var1) {
        this(var1, (String) null, (OnPlayListener) null);
    }

    public AudioPlayerM(Context var1, String var2, OnPlayListener var3) {
        this.mContext = var1;
        this.mIntervalTime = 500L;
        this.audioStreamType = 0;
        this.mHandler = new Handler() {
            public void handleMessage(Message var1) {
                switch (var1.what) {
                    case 0:
                        if (AudioPlayerM.this.mListener != null) {
                            AudioPlayerM.this.mListener.onPlaying((long) AudioPlayerM.this.mPlayer.getCurrentPosition());
                        }

                        this.sendEmptyMessageDelayed(0, AudioPlayerM.this.mIntervalTime);
                        return;
                    case 1:
                        AudioPlayerM.this.startInner();
                    default:
                        return;
                    case 2:
                        LogUtil.e("AudioPlayerM", "convert() error: " + AudioPlayerM.this.mAudioFile);
                }
            }
        };
        this.onAudioFocusChangeListener = new OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int var1) {
                switch (var1) {
                    case -3:
                        if (AudioPlayerM.this.isPlaying()) {
                            AudioPlayerM.this.mPlayer.setVolume(0.1F, 0.1F);
                        }
                        break;
                    case -2:
                        AudioPlayerM.this.stop();
                        return;
                    case -1:
                        AudioPlayerM.this.stop();
                        return;
                    case 0:
                    default:
                        break;
                    case 1:
                        if (AudioPlayerM.this.isPlaying()) {
                            AudioPlayerM.this.mPlayer.setVolume(1.0F, 1.0F);
                            return;
                        }
                }

            }
        };
        this.audioManager = (AudioManager) var1.getSystemService(Context.AUDIO_SERVICE);
        this.mAudioFile = var2;
        this.mListener = var3;
    }

    public final void setDataSource(String var1) {
        type = Type.file;
        if (!TextUtils.equals(var1, this.mAudioFile)) {
            this.mAudioFile = var1;
        }

    }

    public final void setDataSource(Type defType, String name) {
        if (defType == Type.file) {
            setDataSource(name);
        } else if (defType == Type.raw) {
            type = Type.raw;
            int resid = mContext.getResources().getIdentifier(name, "raw", mContext.getPackageName());
            assetFileDescriptor = mContext.getResources().openRawResourceFd(resid);
        } else if (defType == Type.assets) {
            try {
                type = Type.assets;
                assetFileDescriptor = mContext.getAssets().openFd(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
    }

    public final void setOnPlayListener(OnPlayListener var1) {
        this.mListener = var1;
    }

    public final OnPlayListener getOnPlayListener() {
        return this.mListener;
    }

    public final void start(int var1) {
        this.audioStreamType = var1;
        this.startPlay();
    }

    public final void stop() {
        if (this.mPlayer != null) {
            this.endPlay();
            if (this.mListener != null) {
                this.mListener.onInterrupt();
            }
        }

    }

    public final boolean isPlaying() {
        return this.mPlayer != null && this.mPlayer.isPlaying();
    }

    public final long getDuration() {
        return this.mPlayer != null ? (long) this.mPlayer.getDuration() : 0L;
    }

    public final long getCurrentPosition() {
        return this.mPlayer != null ? (long) this.mPlayer.getCurrentPosition() : 0L;
    }

    public final void seekTo(int var1) {
        this.mPlayer.seekTo(var1);
    }

    private void startPlay() {
        LogUtil.i("AudioPlayerM", "start() called");
        this.endPlay();
        this.startInner();
    }

    private void endPlay() {
        this.audioManager.abandonAudioFocus(this.onAudioFocusChangeListener);
        if (this.mPlayer != null) {
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
            this.mHandler.removeMessages(0);
        }

    }

    private void startInner() {
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setLooping(false);
        this.mPlayer.setAudioStreamType(this.audioStreamType);
        if (this.audioStreamType == 3) {
            this.audioManager.setSpeakerphoneOn(true);
        } else {
            this.audioManager.setSpeakerphoneOn(false);
        }

        this.audioManager.requestAudioFocus(this.onAudioFocusChangeListener, this.audioStreamType, 2);
        this.mPlayer.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer var1) {
                LogUtil.i("AudioPlayerM", "player:onPrepared");
                AudioPlayerM.this.mHandler.sendEmptyMessage(0);
                if (AudioPlayerM.this.mListener != null) {
                    AudioPlayerM.this.mListener.onPrepared();
                }

            }
        });
        this.mPlayer.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer var1) {
                LogUtil.i("AudioPlayerM", "player:onCompletion");
                AudioPlayerM.this.endPlay();
                if (AudioPlayerM.this.mListener != null) {
                    AudioPlayerM.this.mListener.onCompletion();
                }

            }
        });
        this.mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer var1, int var2, int var3) {
                LogUtil.e("AudioPlayerM", "player:onOnError");
                AudioPlayerM.this.endPlay();
                if (AudioPlayerM.this.mListener != null) {
                    AudioPlayerM.this.mListener.onError(String.format("OnErrorListener what:%d extra:%d", new Object[]{Integer.valueOf(var2), Integer.valueOf(var3)}));
                }

                return true;
            }
        });

        try {
            if (this.mAudioFile != null) {
                if (type == Type.file) {
                    this.mPlayer.setDataSource(this.mAudioFile);
                } else {
                    AssetFileDescriptor afd = assetFileDescriptor;
                    this.mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                }
                this.mPlayer.prepare();
                this.mPlayer.start();
                LogUtil.i("AudioPlayerM", "player:start ok---->" + this.mAudioFile);
            } else {
                if (this.mListener != null) {
                    this.mListener.onError("no datasource");
                }

            }
        } catch (Exception var2) {
            var2.printStackTrace();
            LogUtil.e("AudioPlayerM", "player:onOnError Exception\n" + var2.toString());
            this.endPlay();
            if (this.mListener != null) {
                this.mListener.onError("Exception\n" + var2.toString());
            }

        }
    }

    private void deleteOnExit() {
        File var1;
        if ((var1 = new File(this.mAudioFile)).exists()) {
            var1.deleteOnExit();
        }

    }
}