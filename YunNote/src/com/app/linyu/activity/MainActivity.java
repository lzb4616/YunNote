package com.app.linyu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.linyu.R;
import com.app.linyu.adapter.GridViewAdapter;
import com.app.linyu.client.HttpClient;
import com.app.linyu.config.Constants;
import com.app.linyu.config.NoteAction;
import com.app.linyu.model.ImageInfo;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private GridView mGirdView;
    private List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
    private Intent _intent ;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init();
        mGirdView = (GridView)findViewById(R.id.grid_view);
        mGirdView.setAdapter(new GridViewAdapter(imageInfoList,this));

        mGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent mIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                        startActivity(mIntent);
                        break;
                    case 1:Intent i = new Intent(MainActivity.this,ShareListNoteActivity.class);
                        startActivity(i);
                        break;
                    case 2:Intent intent = new Intent(MainActivity.this,ListNoteActivity.class);
                        startActivity(intent);
                        break;
                    case 3:_intent = new Intent(MainActivity.this, AddNoteActivity.class);
                    mBundle = new Bundle();
                    mBundle.putInt("LABEL", NoteAction.PICTURE);
                    _intent.putExtras(mBundle);
                    startActivity(_intent);
                        break;
                    case 4:_intent = new Intent(MainActivity.this, AddNoteActivity.class);
                    mBundle = new Bundle();
                    mBundle.putInt("LABEL", NoteAction.REQUEST_CODE_SEARCH);
                    _intent.putExtras(mBundle);
                    startActivity(_intent);
                        break;
                    case 5:_intent = new Intent(MainActivity.this, AddNoteActivity.class);
                    mBundle = new Bundle();
                    mBundle.putInt("LABEL", NoteAction.CAMERA);
                    _intent.putExtras(mBundle);
                    startActivity(_intent);
                        break;
                     default:break;
                }
            }
        });



    }


    private void init(){
        imageInfoList.add(new ImageInfo("添加笔记",R.drawable.icon1));
        imageInfoList.add(new ImageInfo("分享",R.drawable.icon2));
        imageInfoList.add(new ImageInfo("查看笔记",R.drawable.icon3));
        imageInfoList.add(new ImageInfo("添加图片",R.drawable.icon12));
        imageInfoList.add(new ImageInfo("语音输入",R.drawable.icon11));
        imageInfoList.add(new ImageInfo("照相",R.drawable.icon13));
    }








/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

}
