package com.netease.im.session;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;

import com.facebook.react.bridge.ReactContext;
import com.netease.im.ReactCache;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.media.player.OnPlayListener;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by dowin on 2017/5/23.
 */

public class AudioPlayService implements SensorEventListener {

    final static String SCHEME_FILE = "file";
    final static String SCHEME_RAW = "raw";
    final static String SCHEME_ASSETS = "assets";
    AudioPlayerM currentAudioPlayer;
    private String currentFile;
    private int state;
    private int currentAudioStreamType = AudioManager.STREAM_VOICE_CALL;

    protected boolean needSeek = false;
    protected long seekPosition;

    private AudioPlayService() {
    }

    static class InstanceHolder {
        static AudioPlayService instance = new AudioPlayService();
    }

    public static AudioPlayService getInstance() {
        return InstanceHolder.instance;
    }

    public void play(Handler handler, ReactContext context, String filePath) {
        playAudio(handler, context, -1, SCHEME_FILE, filePath);
    }

    public void playAudio(Handler handler, ReactContext context, int audioStreamType, String type, String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        LogUtil.i(type, "path:" + filePath);
        if (audioStreamType == -1) {
            register(context, true);
        } else {
            currentAudioStreamType = audioStreamType;
        }
        if (isPlayingAudio()) {
            stopAudio(handler);
            if (currentFile != null && currentFile.equals(filePath)) {
                return;
            }
        }
        state = AudioControllerState.stop;
        currentFile = filePath;

        if (currentAudioPlayer == null) {
            currentAudioPlayer = new AudioPlayerM(context);
        }
//        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("Volume", context.getCurrentActivity().getVolumeControlStream()));

        if (context.getCurrentActivity() != null) {
            context.getCurrentActivity().setVolumeControlStream(currentAudioStreamType); // 默认使用听筒播放
        }
        if (TextUtils.equals(type, SCHEME_FILE)) {
            currentAudioPlayer.setDataSource(filePath);
        } else if (TextUtils.equals(type, SCHEME_RAW)) {
            currentAudioPlayer.setDataSource(AudioPlayerM.Type.raw, filePath);
        } else if (TextUtils.equals(type, SCHEME_ASSETS)) {
            if (filePath.indexOf(".") == -1) {
                filePath = filePath + "." + type;
            }
            currentAudioPlayer.setDataSource(AudioPlayerM.Type.assets, filePath);
        }

        currentAudioPlayer.setOnPlayListener(new BasePlayerListener(currentAudioPlayer));

        handler.postDelayed(playRunnable, 50);
        state = AudioControllerState.ready;
    }

    public void stopPlay(Handler handler, ReactContext context) {
        stopAudio(handler);
        register(context, true);
    }

    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean hasRegister;

    void register(ReactContext context, boolean register) {

        if (hasRegister && register) {
            return;
        }

        if (sensorManager == null) {
            sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        if (register) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (hasRegister && !register) {
            sensorManager.unregisterListener(this, sensor);
        }
        hasRegister = register;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];

        if (isPlayingAudio()) {
            if (value == sensor.getMaximumRange()) {
                updateAudioStreamType(AudioManager.STREAM_MUSIC);
            } else {
                updateAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
        } else {
            if (value == sensor.getMaximumRange()) {
                currentAudioStreamType = AudioManager.STREAM_VOICE_CALL;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean updateAudioStreamType(int audioStreamType) {
        if (!isPlayingAudio()) {
            return false;
        }

        if (audioStreamType == currentAudioStreamType) {
            return false;
        }

        changeAudioStreamType(audioStreamType);
        return true;
    }

    private void changeAudioStreamType(int audioStreamType) {
        if (isPlayingAudio()) {
            seekPosition = currentAudioPlayer.getCurrentPosition();
            needSeek = true;
            currentAudioStreamType = audioStreamType;
            currentAudioPlayer.start(audioStreamType);
        } else {
            currentAudioStreamType = audioStreamType;
        }
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
            currentAudioPlayer.start(currentAudioStreamType);
        }
    };

    //playing or ready
    private boolean isPlayingAudio() {

        if (currentAudioPlayer != null) {
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

        protected AudioPlayerM listenerPlayingAudioPlayer;
        private long position = -1;

        public BasePlayerListener(AudioPlayerM playingAudioPlayer) {
            listenerPlayingAudioPlayer = playingAudioPlayer;
        }

        protected boolean checkAudioPlayerValid() {
            if (currentAudioPlayer != listenerPlayingAudioPlayer) {
                return false;
            }

            return true;
        }

        @Override
        public void onPrepared() {
            if (!checkAudioPlayerValid()) {
                return;
            }

            state = AudioControllerState.playing;
            if (needSeek) {
                needSeek = false;
                listenerPlayingAudioPlayer.seekTo((int) seekPosition);
                position = seekPosition;
            }
//            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("prepared", position));
        }

        @Override
        public void onCompletion() {
            state = AudioControllerState.stop;
            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("completion", position));
        }

        @Override
        public void onInterrupt() {
            state = AudioControllerState.stop;
            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("stop", position));
        }

        @Override
        public void onError(String s) {
            state = AudioControllerState.stop;
            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("error", position));
        }

        @Override
        public void onPlaying(long curPosition) {
            state = AudioControllerState.playing;
            position = curPosition;
//            ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioPlay("playing", curPosition));
        }
    }
}
