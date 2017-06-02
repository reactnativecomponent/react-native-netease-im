package com.netease.im.session;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;

import com.netease.im.ReactCache;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;

/**
 * Created by dowin on 2017/5/23.
 */

public class AudioPlayService {

    AudioPlayer currentAudioPlayer;
    private String currentFile;
    private int state;

    private AudioPlayService(){}

    static class InstanceHolder {
        static AudioPlayService instance = new AudioPlayService();
    }
    public static AudioPlayService getInstance(){
        return InstanceHolder.instance;
    }
    public void play(Handler handler, Context context, String filePath){

        if(TextUtils.isEmpty(filePath)) {
            return;
        }


        if(isPlayingAudio()) {
            stopAudio(handler);
            if(currentFile!=null&&currentFile.equals(filePath)){
                return;
            }
        }
        state = AudioControllerState.stop;
        currentFile = filePath;

        if(currentAudioPlayer==null){
            currentAudioPlayer= new AudioPlayer(context);
        }
        currentAudioPlayer.setDataSource(filePath);

        currentAudioPlayer.setOnPlayListener(new BasePlayerListener());

        handler.postDelayed(playRunnable,50);
        state = AudioControllerState.ready;
    }
    public void stopPlay(Handler handler) {
        stopAudio(handler);
    }



    Runnable playRunnable = new Runnable() {

        @Override
        public void run() {
            if (currentAudioPlayer == null) {
                LogUtil.audio("playRunnable run when currentAudioPlayer == null");
                return;
            }
//                 AudioManager.STREAM_VOICE_CALL;//听筒模式
//                 AudioManager.STREAM_MUSIC;//扬声器模式
            currentAudioPlayer.start(AudioManager.STREAM_VOICE_CALL);
        }
    };
    //playing or ready
    private boolean isPlayingAudio() {

        if(currentAudioPlayer != null) {
            return state == AudioControllerState.playing
                    || state == AudioControllerState.ready;
        } else {
            return false;
        }
    }
    //stop or cancel
    private void stopAudio(Handler handler) {
        if (state == AudioControllerState.playing) {
            //playing->stop
            currentAudioPlayer.stop();
        } else if (state == AudioControllerState.ready) {
            //ready->cancel
            handler.removeCallbacks(playRunnable);
            resetAudioController();
        }
    }
    protected void resetAudioController() {
        currentAudioPlayer.setOnPlayListener(null);
        currentAudioPlayer = null;

        state = AudioControllerState.stop;
    }
    interface AudioControllerState {
        int stop = 0;
        int ready = 1;
        int playing = 2;
    }

    public class BasePlayerListener implements OnPlayListener {

        private long position = -1;
        @Override
        public void onPrepared() {
//            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("prepared",position));
        }

        @Override
        public void onCompletion() {
            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("completion",position));
        }

        @Override
        public void onInterrupt() {
//            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("interrupt",position));
        }

        @Override
        public void onError(String s) {
            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("error",position));
        }

        @Override
        public void onPlaying(long curPosition) {
            position = curPosition;
//            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("playing",position));
        }
    }
}
