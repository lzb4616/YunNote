package com.app.linyu.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.format.DateFormat;

public class GestureImageUtils {

	/*
	 * 将位图保存到指定的路径
	 * 
	 * @param path
	 * @param bitmap
	 * @throws java.io.IOException
	 */
	public static String saveBitmap(Bitmap bitmap)
			throws IOException {
		String painting_mFileName = null;
		painting_mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		painting_mFileName +="/" + "ReadMe"+"/"+ "gesture"+"/"+ "gesture"
		+ new DateFormat().format("yyyyMMdd_hhmmss",// 以ReadMe+时间命名保存在根目录
				Calendar.getInstance(Locale.CHINA))+ ".png";
		
		if (painting_mFileName != null && bitmap != null) {
			File _file = new File(painting_mFileName);
			// 如果文件夹不存在则创建一个新的文件
			if (!_file.exists()) {
				_file.getParentFile().mkdirs();
				_file.createNewFile();
			}
			// 创建输出流
			OutputStream write = new FileOutputStream(_file);
			// 获取文件名
			String fileName = _file.getName();
			// 取出文件的格式名
			String endName = fileName.substring(fileName.lastIndexOf(".") + 1);
			if ("png".equalsIgnoreCase(endName)) {
				// bitmap的压缩格式
				bitmap.compress(CompressFormat.PNG, 100, write);
			} else {
				bitmap.compress(CompressFormat.JPEG, 100, write);
			}			
		}
		return painting_mFileName;
	}
}
