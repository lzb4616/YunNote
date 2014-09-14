package com.app.linyu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.linyu.R;
import com.app.linyu.holder.InterFaceHolder;
import com.app.linyu.model.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zibin on 2014/3/31 0031.
 */
public class GridViewAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();

    public GridViewAdapter(List<ImageInfo> imageInfoList, Context mComtext) {
        this.imageInfoList = imageInfoList;
        this.inflater = LayoutInflater.from(mComtext);
    }

    @Override
    public int getCount() {
        return imageInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InterFaceHolder mIFholder ;
        if (convertView == null){
            mIFholder = new InterFaceHolder();
            convertView = inflater.inflate(R.layout.grid_item,null);

            mIFholder.imageView = (ImageView)convertView.findViewById(R.id.seclect_imv);
            mIFholder.textView = (TextView)convertView.findViewById(R.id.select_tv);

            convertView.setTag(mIFholder);
        }else {
            mIFholder = (InterFaceHolder)convertView.getTag();
        }

        mIFholder.textView.setText(imageInfoList.get(position).imageMsg);
        mIFholder.imageView.setImageResource(imageInfoList.get(position).imageId);

        return convertView;
    }
}
