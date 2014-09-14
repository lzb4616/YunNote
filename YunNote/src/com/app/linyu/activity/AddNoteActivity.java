package com.app.linyu.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.linyu.R;
import com.app.linyu.client.AppException;
import com.app.linyu.client.HttpClient;
import com.app.linyu.config.NoteAction;
import com.app.linyu.listeners.ImageButtonClickListener;
import com.app.linyu.listeners.InitVoiceListener;
import com.app.linyu.listeners.VoiceToTextListener;
import com.app.linyu.model.Note;
import com.app.linyu.model.Resource;
import com.app.linyu.utils.URLImageGetter;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.SpeechUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.onekeyshare.ShareAllGird;

public class AddNoteActivity extends Activity {
	public static final String TAG = "AddNoteActivity";

	/** 保存按钮 */
	private ImageButton addnote_save_btn;
	/** 添加图片按钮 */
	private ImageButton addnote_picture_btn;
	/** 语音输入按钮 */
	private ImageButton addnote_voice2text_btn;
	/** 分享按钮 */
	private ImageButton addnote_share_btn;
	/** 笔记添加的标题 */
	public static EditText note_title_et;
	/** 笔记的详细输入内容 */
	public static EditText note_content_et;
	/** 定义图片存放的地址 */
	public static String TEST_IMAGE;
	private Toast mToast;
	public static Note _note = null;
	private VoiceToTextListener mToTextListener;
	private ImageUpLoadTask mUpLoadTask;

	private HttpClient client = HttpClient.getClient();
	private URLImageGetter urlImageGetter = URLImageGetter.getUrlImageGetter(
			client, 3);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note_activiry);

		// 初始化ShareSDK
		AbstractWeibo.initSDK(this);
		initImagePath();
		initWidget();
		imaeButtonClickListener();
		InitVoiceToText();
		responseActivity();
		Intent mIntent = getIntent();
		if (mIntent.getSerializableExtra("note") != null) {
			_note = (Note) mIntent.getSerializableExtra("note");
			note_content_et.setText(Html.fromHtml(_note.getContent(),
					urlImageGetter, null));
			note_title_et.setText(_note.getTitle());
		}

	}


	private void initWidget() {
		addnote_picture_btn = (ImageButton) findViewById(R.id.note_pic_imBtn);
		addnote_save_btn = (ImageButton) findViewById(R.id.note_save_imBtn);
		addnote_voice2text_btn = (ImageButton) findViewById(R.id.note_voice_imBtn);
		addnote_share_btn = (ImageButton) findViewById((R.id.note_share_imBtn));
		note_content_et = (EditText) findViewById(R.id.user_detail);
		note_title_et = (EditText) findViewById(R.id.user_title);
		mToTextListener = new VoiceToTextListener(AddNoteActivity.this,
				note_content_et);
	}

	private void responseActivity() {
		Intent mIntent = getIntent();
		Bundle mBundle = mIntent.getExtras();
		if (mBundle != null) {
			int label  = mBundle.getInt("LABEL");
			if (label == NoteAction.CAMERA) {
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(camera, NoteAction.CAMERA);
			} else if (label == NoteAction.PICTURE) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				// 取得照片后返回本界面
				startActivityForResult(intent, NoteAction.PICTURE);
			} else if (label == NoteAction.REQUEST_CODE_SEARCH) {
				addnote_voice2text_btn.performClick();
			} 
		}
	}
	private void imaeButtonClickListener() {
		addnote_picture_btn.setOnClickListener(new ImageButtonClickListener(
				AddNoteActivity.this));

		addnote_save_btn.setOnClickListener(new ImageButtonClickListener(
				AddNoteActivity.this));

		addnote_voice2text_btn.setOnClickListener(new ImageButtonClickListener(
				AddNoteActivity.this));

		addnote_share_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_note != null) {
					ShareNoteTask mShareNoteTask = new ShareNoteTask();
					mShareNoteTask.execute(_note.getPath());
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != 0 && data != null) {
			switch (requestCode) {
			case NoteAction.PICTURE:
				ContentResolver resolver = getContentResolver();
				try {
					Uri uri = data.getData();
					Bitmap bit = MediaStore.Images.Media.getBitmap(resolver,
							uri);
					String[] proj = { MediaStore.Images.Media.DATA };
					// android多媒体数据库的封装接口
					Cursor cursor = managedQuery(uri, proj, null, null, null);
					// 这个是获得用户选择的图片的索引值
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					// 将光标移至开头 ，这个很重要，不小心很容易引起越界
					cursor.moveToFirst();
					// 最后根据索引值获取图片路径
					String imgPath = cursor.getString(column_index);
					Log.i(TAG, imgPath);
					Toast.makeText(this, imgPath, Toast.LENGTH_LONG).show();
					mUpLoadTask = new ImageUpLoadTask();
					mUpLoadTask.execute(new File(imgPath));
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case NoteAction.CAMERA:
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Toast.makeText(this,
							"SD card is not avaiable/writeable right now.",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String name = new DateFormat().format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".jpg";
				Toast.makeText(this, name, Toast.LENGTH_LONG).show();
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

				FileOutputStream b = null;
				File file = new File("/mnt/sdcard/DCIM/linyuapp/");
				file.mkdirs();// 创建文件夹
				String fileName = "/mnt/sdcard/DCIM/linyuapp/" + name;
				try {
					b = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				mUpLoadTask = new ImageUpLoadTask();
				mUpLoadTask.execute(new File(fileName));
				break;
			case NoteAction.REQUEST_CODE_SEARCH:
				// 取得识别的字符串
				ArrayList<String> results = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String res = results.get(0);// 语音识别的文本
				note_content_et.append(res);
				break;
			case NoteAction.SHARE:
				break;
			default:
				break;
			}

		}

	}

	private void InitVoiceToText() {

		// 设置申请到的应用appid
		SpeechUtility.getUtility(AddNoteActivity.this).setAppid("51ece17f");
		// 初始化识别对象
		SpeechRecognizer mIat = new SpeechRecognizer(AddNoteActivity.this,
				new InitVoiceListener(addnote_voice2text_btn));
		mToast = Toast.makeText(AddNoteActivity.this, "", Toast.LENGTH_LONG);
		// 转写会话
		mIat.setParameter(SpeechConstant.PARAMS, "asr_ptt=1");
		mIat.startListening(mToTextListener);
		// 转写会话停止
		mIat.stopListening(mToTextListener);
		// 转写会话取消
		mIat.cancel(mToTextListener);

	}

	public class ImageUpLoadTask extends AsyncTask<File, Void, Resource> {
		@Override
		protected Resource doInBackground(File... params) {

			Log.i(TAG, params[0].getAbsolutePath());
			try {
				Log.i(TAG, "开始上传");
				return client.uploadResource(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AppException e) {
				if (e.getErrorCode() == 307 || e.getErrorCode() == 1017) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Resource resource) {
			super.onPostExecute(resource);
			Log.i(TAG, "下载开始" + resource.toResourceTag());
			if (resource != null) {
				Log.i(TAG, "上传完毕" + "返回的图片的地址"
						+ resource.toResourceTag().toString());
				if (urlImageGetter != null) {
					Log.i(TAG, "下载图片成功");
					note_content_et.append("\n");
					note_content_et.append(Html.fromHtml(
							resource.toResourceTag(), urlImageGetter, null));
					note_content_et.append("\n");
				}

			}
			Log.i(TAG, "上传失败");

		}
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
		try {// 判断SD卡中是否存在此文件夹
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())
					&& Environment.getExternalStorageDirectory().exists()) {
				TEST_IMAGE = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/pic.png";
			} else {
				TEST_IMAGE = this.getApplication().getFilesDir()
						.getAbsolutePath()
						+ "/pic.png";
			}
			File file = new File(TEST_IMAGE);
			// 判断图片是否存此文件夹中
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(this.getResources(),
						R.drawable.ic_launcher);
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

	private class ShareNoteTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String s) {
			if (s != null) {
				shareNote(s);
				Toast.makeText(AddNoteActivity.this, "分享开始", Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(AddNoteActivity.this, "分享失败", Toast.LENGTH_LONG)
						.show();
			}
			super.onPostExecute(s);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				return client.getShare(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AppException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

	/**
	 * 将action转换为String
	 */
	public static String actionToString(int action) {
		switch (action) {
		case AbstractWeibo.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";
		case AbstractWeibo.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case AbstractWeibo.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case AbstractWeibo.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case AbstractWeibo.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case AbstractWeibo.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		case AbstractWeibo.ACTION_SHARE:
			return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}

	protected void onDestroy() {
		// 结束ShareSDK的统计功能并释放资源
		AbstractWeibo.stopSDK(this);
		super.onDestroy();
	}

}
