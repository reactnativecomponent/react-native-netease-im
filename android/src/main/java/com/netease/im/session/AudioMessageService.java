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

/**
 * Created by dowin on 2017/5/3.
 */

public class AudioMessageService implements IAudioRecordCallback {

    interface OnAudioListener{

        int onProgress(File file, int currentTime,int db);
    }
    protected AudioRecorder audioMessageHelper;
    private boolean started = false;
    private int currMaxTime = 0;
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


    public void startAudioRecord(Context context,int maxTime) {
        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(context, RecordType.AAC, maxTime, this);
        }
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
                        onRefresh((File) msg.obj,db,msg.arg1);
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
        if (currMaxTime < AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * 1000) {
            audioMessageHelper.completeRecord(false);
        } else {
            audioMessageHelper.handleEndRecord(true, currMaxTime);
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
        currMaxTime = 0;

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
//        currMaxTime = (int) audioLength;
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
        currMaxTime = maxTime;
    }

    void onRefresh(File file, double recordPower, long currentTime){
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord((int) recordPower, currentTime));
    }
}
