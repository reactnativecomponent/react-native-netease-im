package com.netease.im.session;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.netease.im.R;
import com.netease.im.ReactCache;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by dowin on 2017/5/3.
 */

public class AudioMessageService implements IAudioRecordCallback {

    interface OnAudioListener {

        int onProgress(File file, int currentTime, int db);
    }

    final static int WHAT_AUDIO = 1;
    protected AudioRecorder audioMessageHelper;
    private final int currMaxTime = 60;
    private int currTime = 0;
    SessionService sessionService;
    private Handler handler;
    private long currentTime;

    private AudioMessageService() {
    }

    static class InstanceHolder {
        static AudioMessageService instance = new AudioMessageService();
    }

    public static AudioMessageService getInstance() {
        return InstanceHolder.instance;
    }


    public void startAudioRecord(Context context) {

        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(context, RecordType.AAC, currMaxTime, this);
        }
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == WHAT_AUDIO) {
                        if (!isRecording()) {
                            return;
                        }
                        double db = (double) audioMessageHelper.getCurrentRecordMaxAmplitude() / 1;
                        if (db > 1) {
                            db = 20 * Math.log10(db);
                        }
                        onRefresh(db, msg.arg1);
                        try {
                            Message t = Message.obtain(handler, WHAT_AUDIO, (int) (System.currentTimeMillis() - currentTime), msg.arg2);
                            sendMessageDelayed(t, 300);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
        if (isRecording()) {
            cancelAudioRecord();
        }
        audioMessageHelper.startRecord();
    }

    public void endAudioRecord(SessionService sessionService) {
        this.sessionService = sessionService;

        if (audioMessageHelper == null) {
            return;
        }
        handler.removeMessages(1);
        if (currTime < currMaxTime) {
            audioMessageHelper.completeRecord(false);
        } else {
            audioMessageHelper.handleEndRecord(true, currTime);
        }

    }

    public void cancelAudioRecord() {

        if (audioMessageHelper == null) {
            return;
        }
        handler.removeMessages(1);
        audioMessageHelper.completeRecord(true);

    }

    @Override
    public void onRecordReady() {
        currentTime = System.currentTimeMillis();
    }

    public boolean isRecording() {
        return audioMessageHelper != null && audioMessageHelper.isRecording();
    }

    public void onDestroy() {
        // release
        if (audioMessageHelper != null) {
//            audioMessageHelper.destroyAudioRecorder();
        }
    }

    @Override
    public void onRecordStart(File file, RecordType recordType) {
        Message msg = Message.obtain(handler,WHAT_AUDIO,(int) (System.currentTimeMillis() - currentTime));
        handler.sendMessageDelayed(msg, 300);
    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {

        double db = (double) audioMessageHelper.getCurrentRecordMaxAmplitude() / 1;
        if (db > 1) {
            db = 20 * Math.log10(db);
        }
        onRefresh((int) db, audioLength);
        if (audioLength < 2000L) {
            return;
        }
        currTime = (int) (audioLength / 1000);
        if (sessionService != null)
            sessionService.sendAudioMessage(audioFile.getAbsolutePath(), audioLength, null);
    }

    @Override
    public void onRecordFail() {
        Toast.makeText(ReactCache.getReactContext(), R.string.recording_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordCancel() {
    }

    @Override
    public void onRecordReachedMaxTime(int maxTime) {
        currTime = maxTime;
    }

    void onRefresh(double recordPower, long milliseconds) {
        int seconds = new BigDecimal((float) ((float) milliseconds / (float) 1000)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord((int) recordPower, seconds));
    }
}
