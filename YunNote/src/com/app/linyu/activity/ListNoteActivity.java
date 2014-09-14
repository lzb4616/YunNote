package com.app.linyu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.app.linyu.R;
import com.app.linyu.adapter.ListNoteAdapter;
import com.app.linyu.client.AppException;
import com.app.linyu.client.HttpClient;
import com.app.linyu.config.Constants;
import com.app.linyu.model.Note;
import com.app.linyu.utils.ListNoteCahe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.onekeyshare.ShareAllGird;

public class ListNoteActivity extends Activity {
    private GridView mGridView;
    private ListNoteAdapter mListNoteAdapter;
    private ListNoteCahe mListNoteCahe = new ListNoteCahe();
    private List<Note> _listNote;
    HttpClient client = HttpClient.getClient();
    private DeleteNoteTask mDeleteNoteTask;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_note);
        mGridView = (GridView) findViewById(R.id.listNote_gv);
        mListNoteCahe.getAllNote(Constants.notebookPath, new ListNoteCahe.GetNoteCallBack() {
            @Override
            public void getAllNote(List<Note> mNotes) {
                _listNote = mNotes;
                Log.i(AddNoteActivity.TAG, "已经获取到notes,大小为" + mNotes.size());
                mListNoteAdapter = new ListNoteAdapter(ListNoteActivity.this, mNotes);
                mGridView.setAdapter(mListNoteAdapter);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("note", _listNote.get(position));
                Intent mIntent = new Intent(ListNoteActivity.this, AddNoteActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                noteId = position;
                AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(ListNoteActivity.this);
                mDeleteNoteTask = new DeleteNoteTask();
                mAlertDialog.setMessage("是否要删除" +"“"+ _listNote.get(position).getTitle()+"”" + "日记");
                mAlertDialog.setNegativeButton("取消", null);
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mDeleteNoteTask.execute(_listNote.get(noteId).getPath());

                    }
                });

                mAlertDialog.show();
                return false;
            }
        });
    }


    private class DeleteNoteTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                client.deleteNote(params[0]);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid){
                Toast.makeText(ListNoteActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                mListNoteCahe = new ListNoteCahe();
                mListNoteCahe.getAllNote(Constants.notebookPath, new ListNoteCahe.GetNoteCallBack() {
                    @Override
                    public void getAllNote(List<Note> mNotes) {
                        _listNote.clear();
                        _listNote = mNotes;
                        Log.i(AddNoteActivity.TAG, "已经获取到notes,大小为" + mNotes.size());
                        mListNoteAdapter = new ListNoteAdapter(ListNoteActivity.this, mNotes);
                        mGridView.setAdapter(mListNoteAdapter);
                    }
                });

            }else {
                Toast.makeText(ListNoteActivity.this,"删除失败",Toast.LENGTH_LONG).show();
            }
        }
    }


	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}
    
    
}
