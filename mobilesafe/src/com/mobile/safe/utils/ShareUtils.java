package com.mobile.safe.utils;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
/**
 * 分享，待续补充
 * 
 * @author ziyang
 * 
 */
public class ShareUtils {
	private static Intent shareIntent;
	/**
	 * 分享文本
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void shareText(Context mContext, String msg) {
		shareIntent = new Intent();
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
		shareIntent.setType("text/plain");
		mContext.startActivity(Intent.createChooser(shareIntent, "分享内容"));
	}
	/**
	 * 分享单张图片
	 * 
	 * @param mContext
	 * @param imagePath
	 */
	public static void shareSimpleImage(Context mContext, String imagePath) {
		shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		File imagefile = new File(imagePath);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imagefile));
		shareIntent.setType("image/*");
		mContext.startActivity(Intent.createChooser(shareIntent, "分享图片"));
	}
	/**
	 * 分享多张图片
	 * 
	 * @param mContext
	 * @param imageList
	 */
	public static void shareMultiImages(Context mContext,
			ArrayList<String> imageList) {
		ArrayList<Uri> imageUriList = new ArrayList<Uri>();
		for (String imagePath : imageList) {
			File imagefile = new File(imagePath);
			imageUriList.add(Uri.fromFile(imagefile));
		}
		shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
				imageUriList);
		shareIntent.setType("image/*");
		mContext.startActivity(Intent.createChooser(shareIntent, "分享多张图片"));
	}
}
