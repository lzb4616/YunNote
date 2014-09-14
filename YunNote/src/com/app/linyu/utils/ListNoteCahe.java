package com.app.linyu.utils;

import android.os.AsyncTask;

import com.app.linyu.activity.AddNoteActivity;
import com.app.linyu.activity.MainActivity;
import com.app.linyu.client.AppException;
import com.app.linyu.client.HttpClient;
import com.app.linyu.model.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * com.app.linyu.utils
 * Created by zibin on 2014/4/7 0007.
 */
public class ListNoteCahe {
    public static List<Note> noteList;
    public static GetNoteTask noteTask;
    private ListNoteTask mListNoteTask;
    private static HttpClient client = HttpClient.getClient();
    public ListNoteCahe() {
        noteTask = new GetNoteTask();
        mListNoteTask = new ListNoteTask();
        noteList = new ArrayList<Note>();
    }

    public void getAllNote(String noteBookPath,GetNoteCallBack callBack){
        noteTask.noteBookPath = noteBookPath;
        noteTask.mNoteCallBack = callBack;

        Log.i(AddNoteActivity.TAG,"开始执行任务");
        mListNoteTask.execute(noteBookPath);

    }

    // 用于回调callback中的方法，更新界面
    public static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if ((Boolean)msg.obj){
                Log.i(AddNoteActivity.TAG,"这里有运行");
                // 调用callback中的imageloaded方法，通知界面更新数据
                noteTask.mNoteCallBack.getAllNote( noteTask.noteList);
            }
        };
    };
    private static  class ListNoteTask extends AsyncTask<String,Void,List<String>>{

        @Override
        protected void onPostExecute(List<String> strings) {
            Log.i(AddNoteActivity.TAG,"已经拿到笔记的地址"+strings.toString());
            GetAllNoteTask mNoteTask ;
            mNoteTask = new GetAllNoteTask();
              mNoteTask.execute(strings);
            super.onPostExecute(strings);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                return client.listNotes(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class GetAllNoteTask extends AsyncTask<List<String>,Void,List<Note>>{
        @Override
        protected void onPostExecute(List<Note> notes) {

            // 下载完成后发送消息回主线程
            Boolean report;
            Message msg = Message.obtain();
            if (notes.size()!=0) {
                Log.i(AddNoteActivity.TAG,"下载成功"+notes.toString());
                report = true;
                noteTask.noteList = notes;
            }else {
                Log.i(AddNoteActivity.TAG,"下载不成功");
                report = false;
            }
            msg.obj = report;
            mHandler.sendMessage(msg);
            super.onPostExecute(notes);
        }

        @Override
        protected List<Note> doInBackground(List<String>... params) {
            Log.i(AddNoteActivity.TAG,"执行下载笔记任务");
                try {
                    for (String s : params[0]) {
                       noteList.add(client.getNote(s));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AppException e) {
                    e.printStackTrace();
                }

            return noteList;
        }
    }

    private class GetNoteTask{
        List<Note> noteList;
        String noteBookPath;
        GetNoteCallBack mNoteCallBack;

    }

    public interface GetNoteCallBack{
        void getAllNote(List<Note> mNotes);
    }
}
