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

/**
 * com.app.linyu.activity
 * Created by zibin on 2014/4/16 0016.
 */
public class ShareListNoteActivity extends Activity {

    private GridView mGridView;
    private ListNoteAdapter mListNoteAdapter;
    private ListNoteCahe mListNoteCahe = new ListNoteCahe();
    private List<Note> _listNote;
    HttpClient client = HttpClient.getClient();
    /**定义图片存放的地址*/
    public static String TEST_IMAGE;
    private ShareNoteTask mShareNoteTask;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_note);
        initImagePath();
        //初始化ShareSDK
        AbstractWeibo.initSDK(this);

        mGridView = (GridView) findViewById(R.id.listNote_gv);
        mListNoteCahe.getAllNote(Constants.notebookPath, new ListNoteCahe.GetNoteCallBack() {
            @Override
            public void getAllNote(List<Note> mNotes) {
                _listNote = mNotes;
                Log.i(AddNoteActivity.TAG, "已经获取到notes,大小为" + mNotes.size());
                mListNoteAdapter = new ListNoteAdapter(ShareListNoteActivity.this, mNotes);
                mGridView.setAdapter(mListNoteAdapter);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("note", _listNote.get(position));
                Intent mIntent = new Intent(ShareListNoteActivity.this, AddNoteActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                noteId = position;
                AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(ShareListNoteActivity.this);
                mShareNoteTask = new ShareNoteTask();
                mAlertDialog.setMessage("是否要分享" +"“"+ _listNote.get(position).getTitle()+"”" + "日记");
                mAlertDialog.setNegativeButton("取消", null);
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mShareNoteTask.execute(_listNote.get(noteId).getPath());

                    }
                });

                mAlertDialog.show();
                return false;
            }
        });
    }


    /**
     * 使用快捷分享完成图文分享
     */
    private void shareNote(String shareInfo) {
        Intent i = new Intent(this, ShareAllGird.class);
        // 分享时Notification的图标
        i.putExtra("notif_icon", R.drawable.ic_launcher);
        // 分享时Notification的标题
        i.putExtra("notif_title", this.getString(R.string.app_name));
        // title标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
        i.putExtra("title", this.getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
        i.putExtra("titleUrl", "http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        i.putExtra("text", shareInfo);
        // imagePath是本地的图片路径，所有平台都支持这个字段，不提供，则表示不分享图片
        i.putExtra("imagePath", this.TEST_IMAGE);
        // url仅在微信（包括好友和朋友圈）中使用，否则可以不提供
        i.putExtra("url", "http://sharesdk.cn");
        // thumbPath是缩略图的本地路径，仅在微信（包括好友和朋友圈）中使用，否则可以不提供
        i.putExtra("thumbPath", this.TEST_IMAGE);
        // appPath是待分享应用程序的本地路劲，仅在微信（包括好友和朋友圈）中使用，否则可以不提供
        i.putExtra("appPath", this.TEST_IMAGE);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
        i.putExtra("comment", this.getString(R.string.share));
        // site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
        i.putExtra("site", this.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
        i.putExtra("siteUrl", "http://sharesdk.cn");
        // 是否直接分享
        i.putExtra("silent", false);
        this.startActivity(i);
    }

    /**
     * 初始化分享的图片
     */
    private void initImagePath() {
        try {//判断SD卡中是否存在此文件夹
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && Environment.getExternalStorageDirectory().exists()) {
                TEST_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pic.png";
            } else {
                TEST_IMAGE = this.getApplication().getFilesDir().getAbsolutePath() + "/pic.png";
            }
            File file = new File(TEST_IMAGE);
            //判断图片是否存此文件夹中
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            TEST_IMAGE = null;
        }
    }

    private class ShareNoteTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPostExecute(String s) {
            if (s!=null){
                shareNote(s);
                Toast.makeText(ShareListNoteActivity.this, "开始分享", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ShareListNoteActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return  client.getShare(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 将action转换为String
     */
    public static String actionToString(int action) {
        switch (action) {
            case AbstractWeibo.ACTION_AUTHORIZING: return "ACTION_AUTHORIZING";
            case AbstractWeibo.ACTION_GETTING_FRIEND_LIST: return "ACTION_GETTING_FRIEND_LIST";
            case AbstractWeibo.ACTION_FOLLOWING_USER: return "ACTION_FOLLOWING_USER";
            case AbstractWeibo.ACTION_SENDING_DIRECT_MESSAGE: return "ACTION_SENDING_DIRECT_MESSAGE";
            case AbstractWeibo.ACTION_TIMELINE: return "ACTION_TIMELINE";
            case AbstractWeibo.ACTION_USER_INFOR: return "ACTION_USER_INFOR";
            case AbstractWeibo.ACTION_SHARE: return "ACTION_SHARE";
            default: {
                return "UNKNOWN";
            }
        }
    }
    protected void onDestroy() {
        //结束ShareSDK的统计功能并释放资源
        AbstractWeibo.stopSDK(this);
        super.onDestroy();
    }

}
