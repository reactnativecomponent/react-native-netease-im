package com.netease.im.session;

import android.content.Context;
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

    protected AudioRecorder audioMessageHelper;
    private boolean started = false;
    private int currMaxTime = 0;
    SessionService sessionService;

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
            audioMessageHelper = new AudioRecorder(context, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND, this);
        }
        audioMessageHelper.startRecord();
    }

    public void endAudioRecord(SessionService sessionService) {
        this.sessionService = sessionService;
        if (audioMessageHelper == null) {
            return;
        }
        started = false;
        if (currMaxTime < AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND*1000) {
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
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord("ready", null, 0L, null));
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
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord("start", file, 0L, recordType));
    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord("success", audioFile, audioLength, recordType));
        if (audioLength < 2000L) {
            return;
        }
//        currMaxTime = (int) audioLength;
        if(sessionService!=null)
        sessionService.sendAudioMessage(audioFile.getAbsolutePath(), audioLength, null);
    }

    @Override
    public void onRecordFail() {
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord("fail", null, 0L, null));
        if (started) {
            Toast.makeText(ReactCache.getReactContext(), R.string.recording_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecordCancel() {
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord("cancel", null, 0L, null));
    }

    @Override
    public void onRecordReachedMaxTime(int maxTime) {
        ReactCache.emit(ReactCache.observeAudioRecord, ReactCache.createAudioRecord("maxTime", null, maxTime, null));
        currMaxTime = maxTime;
    }

}
