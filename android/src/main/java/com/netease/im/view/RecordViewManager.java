package com.netease.im.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by dowin on 2017/6/26.
 */

public class RecordViewManager extends SimpleViewManager<Button> {

    final static String TCT_NAME = "RecordView";
    private ReactContext context;
    private Button button;
    private String[] text = new String[3];

    @Override
    public String getName() {
        return TCT_NAME;
    }

    @Override
    protected Button createViewInstance(ThemedReactContext reactContext) {
        context = reactContext;
        button = new Button(reactContext);
        button.setText("按住 说话");
        initAudioRecordButton(button);
        return button;
    }

    @ReactProp(name = "textArr")
    public void setText(Button view, ReadableArray array) {
        if (array != null && array.size() > 0) {
            text = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                if (array.getType(i) == ReadableType.String)
                    text[i] = array.getString(i);
            }
        }
        if (text.length > 0) {
            button.setText(text[0]);
        }
    }

    private void initAudioRecordButton(final Button view) {
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    touched = true;
//                    initAudioRecord();
                    onStartAudioRecord();
                    updateStatus(UpdateStatus.Start);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                        || event.getAction() == MotionEvent.ACTION_UP) {
//                    touched = false;
                    onEndAudioRecord(isCancelled(v, event));
                    updateStatus(isCancelled(v, event) ? UpdateStatus.Canceled : UpdateStatus.Complete);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    touched = true;
                    cancelAudioRecord(isCancelled(v, event));
                    updateStatus(isCancelled(v, event) ? UpdateStatus.Move : UpdateStatus.Continue);

                }

                return false;
            }
        });
    }

    enum UpdateStatus {
        Start, Canceled, Move, Continue, Complete
    }

    UpdateStatus updateStatus = UpdateStatus.Canceled;

    void updateStatus(UpdateStatus status) {
        if (updateStatus == status) {
            return;
        }
        updateStatus = status;
        WritableMap s = Arguments.createMap();
        s.putString("status", updateStatus.toString());
        context.getJSModule(RCTEventEmitter.class).receiveEvent(button.getId(), "topChange", s);
//        ReactCache.emit(ReactCache.observeAudioRecord, status.toString());
    }

    /**
     * 初始化AudioRecord
     */
    private void initAudioRecord() {
//        if (audioMessageHelper == null) {
//            audioMessageHelper = new AudioRecorder(container.activity, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND, this);
//        }
    }

    private void onStartAudioRecord() {
//        container.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        audioMessageHelper.startRecord();
//        cancelled = false;
        if (text.length > 1) {
            button.setText(text[1]);
        }
    }

    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    /**
     * 结束语音录制
     *
     * @param cancel
     */
    private void onEndAudioRecord(boolean cancel) {
//        started = false;
//        container.activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        audioMessageHelper.completeRecord(cancel);
        if (text.length > 0) {
            button.setText(text[0]);
        }
//        audioRecordBtn.setBackgroundResource(R.drawable.nim_message_input_edittext_box);
//        stopAudioRecordAnim();
    }

    /**
     * 取消语音录制
     *
     * @param cancel
     */
    private void cancelAudioRecord(boolean cancel) {
        // reject
//        if (!started) {
//            return;
//        }
//        // no change
//        if (cancelled == cancel) {
//            return;
//        }
//
//        cancelled = cancel;
        updateTimerTip(cancel);
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     *
     * @param cancel
     */
    private void updateTimerTip(boolean cancel) {
        if (text.length > 2) {
            button.setText(cancel ? text[2] : text[1]);
        }
//        if (cancel) {
//            timerTip.setText(R.string.recording_cancel_tip);
//            timerTipContainer.setBackgroundResource(R.drawable.nim_cancel_record_red_bg);
//        } else {
//            timerTip.setText(R.string.recording_cancel);
//            timerTipContainer.setBackgroundResource(0);
//        }
    }
}
