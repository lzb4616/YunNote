package com.app.linyu.listeners;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.linyu.R;
import com.app.linyu.activity.AddNoteActivity;
import com.app.linyu.activity.ListNoteActivity;
import com.app.linyu.activity.MainActivity;
import com.app.linyu.client.AppException;
import com.app.linyu.client.HttpClient;
import com.app.linyu.config.Constants;
import com.app.linyu.config.NoteAction;
import com.app.linyu.model.Note;
import com.app.linyu.utils.ApkInstaller;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechUtility;

/**
 * Created by zibin on 2014/4/5 0005.
 */
public class ImageButtonClickListener implements View.OnClickListener{

    private Activity mContext;
    private CreteNoteAsyTask mCreteNoteAsyTask;
    private UpdateTask mUpdateTask;
    private String content;
    private String title;

    private HttpClient client = HttpClient.getClient();



    public ImageButtonClickListener(Activity mContext,View... views) {
        this.mContext = mContext;
        if (mCreteNoteAsyTask==null) {
            mCreteNoteAsyTask = new CreteNoteAsyTask();
        }
        

    }

    @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.note_pic_imBtn: takePhotoAndGetPicture();
                    break;
                case R.id.note_save_imBtn:
                    title = AddNoteActivity.note_title_et.getText().toString();
                    content = Html.toHtml(AddNoteActivity.note_content_et.getText());
                	if (AddNoteActivity._note==null) {
                        Log.i(AddNoteActivity.TAG,title+content);
                        mCreteNoteAsyTask.execute(title,content);
					} else {
						AddNoteActivity._note.setTitle(title);
						AddNoteActivity._note.setContent(content);
						mUpdateTask = new UpdateTask();
						mUpdateTask.execute(AddNoteActivity._note);
					}
           
                    break;
                case R.id.note_voice_imBtn:voiceToText();
                    break;
            }

    }

    private void takePhotoAndGetPicture(){
        ImageButton choose_camera, choose_picture;
        final Dialog choose = new Dialog(mContext,
                R.style.draw_dialog);
        choose.setContentView(R.layout.addnote_picture);
        // 设置背景模糊参数
        WindowManager.LayoutParams winlp = choose.getWindow()
                .getAttributes();
        winlp.alpha = 0.9f; // 0.0-1.0
        choose.getWindow().setAttributes(winlp);
        choose.show();// 显示弹出框
        choose_camera = (ImageButton) choose
                .findViewById(R.id.choose_camera);
        choose_picture = (ImageButton) choose
                .findViewById(R.id.choose_picture);
        choose_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开手机相册的intent action语句
                choose.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // 取得照片后返回本界面
                mContext.startActivityForResult(intent,NoteAction.PICTURE);
            }
        });
        choose_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开系统的相机的intent action 语句
                choose.dismiss();
                Intent camera = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                mContext.startActivityForResult(camera, NoteAction.CAMERA);
            }
        });
    }


    private void voiceToText(){
        // 检测是否安装了讯飞语音服务
        if (SpeechUtility.getUtility(mContext)
                .queryAvailableEngines() == null
                || SpeechUtility.getUtility(mContext)
                .queryAvailableEngines().length <= 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(
                    mContext);
            dialog.setMessage(mContext.getString(R.string.download_confirm_msg));
            dialog.setNegativeButton(R.string.dialog_cancel_button,
                    null);
            dialog.setPositiveButton(
                    mContext.getString(R.string.dialog_confirm_button),
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialoginterface, int i) {
                            String url = SpeechUtility.getUtility(
                                    mContext)
                                    .getComponentUrl();
                            String assetsApk = "SpeechService_1.0.1017.apk";
                            processInstall( url,
                                    assetsApk);
                        }
                    });
            dialog.show();
            return;
        }
        Intent intent = new Intent();
        // 指定action，调用讯飞的对话默认窗口
        intent.setAction("com.iflytek.speech.action.voiceinput");
        intent.putExtra(SpeechConstant.PARAMS, "asr_ptt=0");
        intent.putExtra(SpeechConstant.VAD_EOS, "1000");
        // 设置弹出框的两个按钮的名称
        intent.putExtra("title_done", "确定");
        intent.putExtra("title_cancle", "取消");
        mContext.startActivityForResult(intent, NoteAction.REQUEST_CODE_SEARCH);
    }
    // 安装语音组件
    protected void processInstall( String url,
                                  String assetsApk) {
        // TODO Auto-generated method stub
        // 直接下载方式
        // ApkInstaller.openDownloadWeb(context, url);
        // 本地安装方式
        if (!ApkInstaller.installFromAssets(mContext, assetsApk)) {
            Toast.makeText(mContext, "安装失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private Note createNote(String title,String content) throws Exception {
        final Note note = new Note();
        note.setAuthor("linyu");
        note.setCreateTime(System.currentTimeMillis());
        note.setSize(content.length());
        note.setSource("");
        note.setTitle(title);
        note.setContent(content);
        try {
            return client.createNote(Constants.notebookPath, note);
        } catch (AppException e) {
            if (e.getErrorCode() == 307 || e.getErrorCode() == 1017) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private class CreteNoteAsyTask extends AsyncTask<String,Void,Note> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Note note) {
            super.onPostExecute(note);
            if (note!=null){
                Log.i(AddNoteActivity.TAG,note.getPath());
                Intent mIntent = new Intent(mContext,MainActivity.class);
                mContext.startActivity(mIntent);
                mContext.finish();
            }
        }

        @Override
        protected Note doInBackground(String... params) {
            try {
                return createNote(params[0],params[1]);
            }catch (Exception e){
                return null;
            }

        }
    }
    private class UpdateTask extends AsyncTask<Note, Void,Boolean>{

		@Override
		protected Boolean doInBackground(Note... params) {
			try {
				client.updateNote(params[0]);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result==true){
		         Toast.makeText(mContext,"更新成功",Toast.LENGTH_LONG).show();
		         Intent mIntent = new Intent(mContext,ListNoteActivity.class);
		         mContext.startActivity(mIntent);
		         mContext.finish();
            }else {
                Toast.makeText(mContext,"更新失败",Toast.LENGTH_LONG).show();
            }
			super.onPostExecute(result);
		}
    }
}
