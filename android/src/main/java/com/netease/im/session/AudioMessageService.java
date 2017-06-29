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

    protected AudioRecorder audioMessageHelper;
    private boolean started = false;
    private int currMaxTime = 60;
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

        audioMessageHelper = new AudioRecorder(context, RecordType.AAC, currMaxTime, this);
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        if (audioMessageHelper == null || !audioMessageHelper.isRecording()) {
                            return;
                        }
                        double db = (double) audioMessageHelper.getCurrentRecordMaxAmplitude() / 1;
                        if (db > 1) {
                            db = 20 * Math.log10(db);
                        }
                        onRefresh((File) msg.obj, db, msg.arg1);
                        try {
                            Message t = Message.obtain(this, msg.what, (int) (System.currentTimeMillis() - currentTime), msg.arg2, msg.obj);
                            sendMessageDelayed(t, 300);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
        audioMessageHelper.startRecord();
        handler.removeMessages(1);
    }

    public void endAudioRecord(SessionService sessionService) {
        this.sessionService = sessionService;
        if (audioMessageHelper == null) {
            return;
        }
        started = false;
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
        started = false;
        audioMessageHelper.completeRecord(true);

    }

    public boolean isStarted() {
        return started;
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
        started = true;
        Message msg = Message.obtain(handler);
        msg.obj = file;
        msg.what = 1;
        msg.arg1 = (int) (System.currentTimeMillis() - currentTime);
        handler.sendMessageDelayed(msg, 300);
    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {

        double db = (double) audioMessageHelper.getCurrentRecordMaxAmplitude() / 1;
        if (db > 1) {
            db = 20 * Math.log10(db);
        }
        onRefresh(audioFile, (int) db, audioLength);
        if (audioLength < 2000L) {
            return;
        }
        currTime = (int) (audioLength / 1000);
        if (sessionService != null)
            sessionService.sendAudioMessage(audioFile.getAbsolutePath(), audioLength, null);
    }

    @Override
    public void onRecordFail() {
        if (started) {
            Toast.makeText(ReactCache.getReactContext(), R.string.recording_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecordCancel() {
    }

    @Override
    public void onRecordReachedMaxTime(int maxTime) {
        currTime = maxTime;
    }

    void onRefresh(File file, double recordPower, long milliseconds) {
        int seconds = new BigDecimal((float) ((float) milliseconds / (float) 1000)).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord((int) recordPower, seconds));
    }
}
