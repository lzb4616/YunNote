package com.app.linyu.listeners;

import android.view.View;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;

/**
 * com.app.linyu.listeners
 * Created by zibin on 2014/4/7 0007.
 */
public class InitVoiceListener implements InitListener {

    private View v;

    public InitVoiceListener(View v) {
        this.v = v;
    }

    @Override
    public void onInit(ISpeechModule iSpeechModule, int code) {
        if (code == ErrorCode.SUCCESS) {
            // findViewById(R.id.iat_recognize_bind).setEnabled(true);
            v.setEnabled(true);
        }
    }
}
