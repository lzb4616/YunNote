package com.app.linyu.listeners;

import android.app.Activity;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.linyu.R;
import com.app.linyu.utils.JsonParser;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;

/**
 * com.app.linyu.listeners
 * Created by zibin on 2014/4/7 0007.
 */
public class VoiceToTextListener extends RecognizerListener.Stub{
    private Activity mActivity;
    private View v;
    protected Toast mToast;

    public VoiceToTextListener(Activity mActivity,View v) {
        this.mActivity = mActivity;
        this.v = v;
    }

    @Override
    public void onVolumeChanged(int i) throws RemoteException {
        showTip("onVolumeChanged：" + i);
    }

    @Override
    public void onBeginOfSpeech() throws RemoteException {
        showTip("onBeginOfSpeech");
    }

    @Override
    public void onEndOfSpeech() throws RemoteException {
        showTip("onEndOfSpeech");
    }

    @Override
    public void onResult(final RecognizerResult recognizerResult, boolean b) throws RemoteException {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != recognizerResult) {
                    // 显示
                    String iattext = JsonParser.parseIatResult(recognizerResult
                            .getResultString());
                  EditText note_content_et = (EditText)v;
                  note_content_et.append(iattext);
                } else {
                    showTip("无识别结果");
                }
            }
        });
    }

    @Override
    public void onError(int i) throws RemoteException {
        showTip("onError Code：" + i);
    }

    // 语音部分公用的一个toast提示方法
    protected void showTip(final String str) {
        // TODO Auto-generated method stub
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

}
