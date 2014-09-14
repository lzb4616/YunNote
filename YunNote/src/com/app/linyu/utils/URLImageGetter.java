package com.app.linyu.utils;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.app.linyu.activity.AddNoteActivity;
import com.app.linyu.client.AppException;
import com.app.linyu.client.HttpClient;
import com.app.linyu.tools.BitMapTools;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;


public class URLImageGetter implements ImageGetter {


	HttpClient client;
	int scale;

    private static URLImageGetter mUrlImageGetter;
    // 图片缓存集合
    private static HashMap<String, SoftReference<Bitmap>> mHashMap_caches ;

	private URLImageGetter(HttpClient client,int scale) {
		this.client =client;
		this.scale = scale;
        mHashMap_caches  = new HashMap<String, SoftReference<Bitmap>>();
	}

    public static URLImageGetter getUrlImageGetter(HttpClient client,int scale){
        if(mUrlImageGetter == null) {
            synchronized (URLImageGetter.class) {
                if(mUrlImageGetter == null) {
                    mUrlImageGetter = new URLImageGetter(client,scale);
                }
            }
        }
        return mUrlImageGetter;
    }




	@Override
	public Drawable getDrawable(String source) {
        Bitmap bitmap = null;
        BitmapDrawable drawable = null;
        // 如果内存缓存中存在该路径，则从内存中直接获取该图片
        if (mHashMap_caches.containsKey(source)) {
            Log.i(AddNoteActivity.TAG,"缓存中有图片");
            bitmap = mHashMap_caches.get(source).get();
            // 如果缓存中的图片已经被释放，则从该缓存中移除图片路径
            if (bitmap == null) {
                mHashMap_caches.remove(source);

                Log.i(AddNoteActivity.TAG,"缓存中图片删了，重新下载"+source);
                ImageGetterAsyncTask getImageTask = new ImageGetterAsyncTask();
                getImageTask.execute(source);
                try {
                    bitmap = getImageTask.get();
                    if (bitmap != null) {
                        mHashMap_caches.put(source,
                                new SoftReference<Bitmap>(bitmap));
                        drawable = new BitmapDrawable(bitmap);
                        drawable.setBounds(0, 0,drawable.getIntrinsicWidth()*scale,
                                drawable.getMinimumHeight()*scale);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }else{
                drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0,drawable.getIntrinsicWidth()*scale,
                        drawable.getMinimumHeight()*scale);
            }
        }else {
            Log.i(AddNoteActivity.TAG,"缓存中没图片"+source);
            ImageGetterAsyncTask getImageTask = new ImageGetterAsyncTask();
            getImageTask.execute(source);
            try {
                bitmap = getImageTask.get();
                if (bitmap != null) {
                    mHashMap_caches.put(source,
                            new SoftReference<Bitmap>(bitmap));
                    drawable = new BitmapDrawable(bitmap);
                    drawable.setBounds(0, 0,drawable.getIntrinsicWidth()*scale,
                            drawable.getMinimumHeight()*scale);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
		return drawable;
	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Bitmap> {



		@Override
		protected void onPostExecute(Bitmap result) {

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap bitmap = null;
			try {
				bitmap = BitMapTools.getBitmap(downloadImage(params[0]), scale);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}


		/**下载图片*/
	    private  InputStream downloadImage(final String url) throws Exception {
	        InputStream body = null;
	        try {
	        	   body = client.downloadResource(url);
	        } catch (AppException e) {
	            if (e.getErrorCode() == 307 || e.getErrorCode() == 1017) {

	            } else {
	                throw e;
	            }
	        }
			return body;
	    }

	}

}
