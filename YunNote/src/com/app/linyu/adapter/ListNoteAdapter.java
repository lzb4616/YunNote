package com.app.linyu.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.linyu.R;
import com.app.linyu.activity.MainActivity;
import com.app.linyu.client.HttpClient;
import com.app.linyu.holder.ListNoteViewHolder;
import com.app.linyu.model.Note;
import com.app.linyu.utils.URLImageGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * com.app.linyu.adapter
 * Created by zibin on 2014/4/7 0007.
 */
public class ListNoteAdapter extends BaseAdapter{

    private List<Note> noteLists = new ArrayList<Note>();
    ListNoteViewHolder mNoteViewHolder ;
    private LayoutInflater mInflater;
    HttpClient client = HttpClient.getClient();
    URLImageGetter imageGetter = URLImageGetter.getUrlImageGetter(client,4);

    public ListNoteAdapter(Context mContext, List<Note> noteLists) {
        this.noteLists = noteLists;
        this.mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {

        return noteLists.size();
    }

    @Override
    public Object getItem(int position) {
        return noteLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){
            mNoteViewHolder = new ListNoteViewHolder();
            convertView = mInflater.inflate(R.layout.list_note_item,null);
            mNoteViewHolder.content_tv = (TextView)convertView.findViewById(R.id.note_content);
            mNoteViewHolder.tips_tv = (TextView)convertView.findViewById(R.id.note_Tips);
            mNoteViewHolder.content_tv.setText(Html.fromHtml(noteLists.get(position).getContent(), imageGetter, null));
            mNoteViewHolder.tips_tv.setText(noteLists.get(position).getTitle());
            convertView.setTag(mNoteViewHolder);
        }else {
            mNoteViewHolder = (ListNoteViewHolder) convertView.getTag();
        }
   
        return convertView;
    }



}
